package com.hivmedical.medical.controller;

import com.hivmedical.medical.dto.FinanceDashboardDTO;
import com.hivmedical.medical.entitty.Transaction;
import com.hivmedical.medical.service.FinanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/finance")
public class FinanceController {
    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactionsByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return financeService.getTransactionsByMonth(year, month);
    }

    @GetMapping("/dashboard")
    public FinanceDashboardDTO getDashboardByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return financeService.getDashboardByMonth(year, month);
    }

    @PostMapping("/transaction")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return financeService.createTransaction(transaction);
    }

    @PutMapping("/transaction/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return financeService.updateTransaction(id, transaction);
    }

    @DeleteMapping("/transaction/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        financeService.deleteTransaction(id);
    }
}