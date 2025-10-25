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
            System.err.println("Error: Transfer request is null.");
            return TransferStatus.ACCOUNT_NOT_FOUND;
        }

        long toAccount = transferRequest.getToAccountId();// достаем из трансфера ID аккаунта получателя
        long fromAccount = transferRequest.getFromAccount(); // достаем из трансфера ID аккаунта отправителя

        Optional<Account> toAccountOptional = transferRepository.findByID(toAccount);// достаем наш аккаунт из БД в данном случае из MAP<>
        Optional<Account> fromAccountoptional = transferRepository.findByID(fromAccount);


        if (toAccountOptional.isEmpty() || fromAccountoptional.isEmpty()) {
            return TransferStatus.ACCOUNT_NOT_FOUND;
        }
        Account toaccount = toAccountOptional.get(); // счет получателя
        Account fromaccount = fromAccountoptional.get();// счет отправителя


        if (toaccount.isFrozen() || fromaccount.isFrozen()) {
            return TransferStatus.ACCOUNT_FROZEN; // если один из счетов заблокирован
        }

        if (toaccount.getAccountID() == fromaccount.getAccountID()) {
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

        if (amount.compareTo(fromaccount.getBalance()) > 0) {
            return TransferStatus.INSUFFICIENT_FUNDS; // сумма тарнзанкции больше балана на счету
        }

        Account firstLockAccount;
        Account secondLockAccount;

        if (fromAccount < toAccount) { // определяем порядок блокировки
            firstLockAccount = fromaccount;
            secondLockAccount = toaccount;
        } else {
            firstLockAccount = toaccount;
            secondLockAccount = fromaccount;
        }


        Lock firstLock = firstLockAccount.getLock();
        Lock secondLock = secondLockAccount.getLock();

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                BigDecimal fromAccountBalance = fromaccount.getBalance(); // повторная проверка на наличие денег на счету
                if(amount.compareTo(fromAccountBalance)>0) {
                    return TransferStatus.INSUFFICIENT_FUNDS;
                }

                BigDecimal newBalace = fromAccountBalance.subtract(amount); // списание денег со чета
                fromaccount.setBalance(newBalace);

                BigDecimal newBalanceForToAccount = toaccount.getBalance(); // зачисление денег на другой счет
                BigDecimal toAccontNewBalance = newBalanceForToAccount.add(amount);
                toaccount.setBalance(toAccontNewBalance);

                transferRepository.saveAccount(fromaccount);
                transferRepository.saveAccount(toaccount);

            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }


        return TransferStatus.SUCCESS;
    }
}
