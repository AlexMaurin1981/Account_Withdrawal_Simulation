package user.repository;

import user.model.Account;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TransferRepositoryImpl implements TransferRepository {
    ConcurrentHashMap<Long, Account> map = new ConcurrentHashMap<>();

    public TransferRepositoryImpl() {

        map.put(101L, new Account(101L, new BigDecimal("1000.0"), false));
        map.put(102L, new Account(102L, new BigDecimal("0.0"), false));
        map.put(103L, new Account(103L, new BigDecimal("100.0"), true));
        map.put(104L, new Account(104L, BigDecimal.valueOf(-100.0), false));
    }

    @Override
    public Optional<Account> findByID(long accountID) {
      return Optional.ofNullable(map.get(accountID));


    }

    @Override
    public void saveAccount(Account account) {
    }
}
