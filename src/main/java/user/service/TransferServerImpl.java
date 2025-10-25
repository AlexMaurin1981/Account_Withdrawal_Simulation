package user.service;

import user.model.Account;
import user.model.TransferRequest;
import user.repository.TransferRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class TransferServerImpl implements TransferServer {

    private final TransferRepository transferRepository;

    public TransferServerImpl(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    public TransferStatus processTransfer(TransferRequest transferRequest) {

        if (transferRequest == null) {
            throw new RuntimeException("Request is null");

        }

        long toAccountId = transferRequest.getToAccountId();
        long fromAccountId = transferRequest.getFromAccount(); // достаем из трансфера ID аккаунта отправителя

        Optional<Account> toAccountOptional = transferRepository.findByID(toAccountId);// достаем наш аккаунт из БД в данном случае из MAP<>
        Optional<Account> fromAccountoptional = transferRepository.findByID(fromAccountId);


        if (toAccountOptional.isEmpty() || fromAccountoptional.isEmpty()) {
            return TransferStatus.ACCOUNT_NOT_FOUND;
        }

        if (toAccountOptional.get().isFrozen() || fromAccountoptional.get().isFrozen()) {
            return TransferStatus.ACCOUNT_FROZEN; // если один из счетов заблокирован
        }

        if (toAccountOptional.get().getAccountID() == fromAccountoptional.get().getAccountID()) {
            return TransferStatus.SAME_ACCOUNT_TRANSFER; // если один и тот же счет
        }

        BigDecimal amount = transferRequest.getAmount();
        System.err.println("Error: Transfer amount is null.");
        if (amount == null) {
            return TransferStatus.INVALID_AMOUNT; //ечли сумма трансфера равна null;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return TransferStatus.INVALID_AMOUNT; // если сумма транзакции отрицательная
        }

        if (amount.compareTo(fromAccountoptional.get().getBalance()) > 0) {
            return TransferStatus.INSUFFICIENT_FUNDS; // сумма тарнзанкции больше балана на счету
        }

        Account firstLockAccount;
        Account secondLockAccount;

        if (fromAccountId < toAccountId) { // определяем порядок блокировки
            firstLockAccount = fromAccountoptional.get();
            secondLockAccount = toAccountOptional.get();
        } else {
            firstLockAccount = toAccountOptional.get();
            secondLockAccount = fromAccountoptional.get();
        }


        Lock firstLock = firstLockAccount.getLock();
        Lock secondLock = secondLockAccount.getLock();

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                BigDecimal fromAccountBalance = fromAccountoptional.get().getBalance(); // повторная проверка на наличие денег на счету
                if(amount.compareTo(fromAccountBalance)>0) {
                    return TransferStatus.INSUFFICIENT_FUNDS;
                }

                BigDecimal newBalanceFromBalance = fromAccountBalance.subtract(amount); // списание денег со чета
                fromAccountoptional.get().setBalance(newBalanceFromBalance);

                BigDecimal newBalanceForToAccount = toAccountOptional.get().getBalance(); // зачисление денег на другой счет
                BigDecimal toAccontNewBalance = newBalanceForToAccount.add(amount);
                toAccountOptional.get().setBalance(toAccontNewBalance);

                transferRepository.saveAccount(fromAccountoptional.get());
                transferRepository.saveAccount(fromAccountoptional.get());

            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }


        return TransferStatus.SUCCESS;
    }
}
