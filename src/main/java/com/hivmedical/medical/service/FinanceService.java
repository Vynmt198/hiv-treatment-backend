package com.hivmedical.medical.service;

import com.hivmedical.medical.dto.FinanceDashboardDTO;
import com.hivmedical.medical.entitty.Transaction;
import com.hivmedical.medical.entitty.TransactionType;
import com.hivmedical.medical.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class FinanceService {
    private final TransactionRepository transactionRepository;

    public FinanceService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return transactionRepository.findByDateBetween(start, end);
    }

    public FinanceDashboardDTO getDashboardByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        Long totalIncome = transactionRepository.sumAmountByTypeAndDateBetween(TransactionType.INCOME, start, end);
        Long totalExpense = transactionRepository.sumAmountByTypeAndDateBetween(TransactionType.EXPENSE, start, end);
        if (totalIncome == null)
            totalIncome = 0L;
        if (totalExpense == null)
            totalExpense = 0L;
        return new FinanceDashboardDTO(totalIncome, totalExpense, totalIncome - totalExpense);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction updated) {
        Transaction t = transactionRepository.findById(id).orElseThrow();
        t.setDate(updated.getDate());
        t.setType(updated.getType());
        t.setDescription(updated.getDescription());
        t.setAmount(updated.getAmount());
        return transactionRepository.save(t);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}