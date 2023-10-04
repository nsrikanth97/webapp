package edu.neu.csye6225.repository;

import edu.neu.csye6225.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM USER LIMIT 1)")
    int checkIfUsersAreAvail();

    Account findUserByEmail(String email);

}
