package com.hivmedical.medical.repository;

import com.hivmedical.medical.entitty.Transaction;
import com.hivmedical.medical.entitty.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.date BETWEEN :start AND :end")
    Long sumAmountByTypeAndDateBetween(TransactionType type, LocalDate start, LocalDate end);
}