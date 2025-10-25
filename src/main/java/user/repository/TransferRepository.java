package user.repository;

import user.model.Account;

import java.util.Optional;

public interface TransferRepository {

    Optional<Account> findByID(long accountID);

 void saveAccount(Account account);
}
