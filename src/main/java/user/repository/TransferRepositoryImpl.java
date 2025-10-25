package user.repository;

import user.model.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TransferRepositoryImpl implements TransferRepository {

   private Account account;
    Map<Long, Account> map = new HashMap<>();
    public TransferRepositoryImpl() {


        map.put(101L, new Account(101L, new BigDecimal(1000.0), false));
        map.put(102L, new Account(102L, new BigDecimal(0.0), false));
        map.put(103L, new Account(103L, new BigDecimal(100.0), true));
        map.put(104L, new Account(104L, new BigDecimal(-100.0), false));
    }

    @Override
    public Optional<Account> findByID(long accountID) {
      return Optional.ofNullable(map.get(accountID));


    }

    @Override
    public void saveAccount(Account account) {
    }
}
