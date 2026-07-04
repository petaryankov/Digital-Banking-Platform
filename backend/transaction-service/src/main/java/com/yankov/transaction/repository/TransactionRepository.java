package com.yankov.transaction.repository;

import com.yankov.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountNumber(String sourceAccountNumber);

    List<Transaction> findByTargetAccountNumber(String targetAccountNumber);
}
