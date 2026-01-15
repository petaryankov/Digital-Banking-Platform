package com.yankov.backend.repository;

import com.yankov.backend.model.Account;
import com.yankov.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccount(Account account);

    List<Transaction> findByTargetAccount(Account account);
}
