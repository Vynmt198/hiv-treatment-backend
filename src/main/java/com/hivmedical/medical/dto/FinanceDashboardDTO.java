package com.hivmedical.medical.dto;

public class FinanceDashboardDTO {
    private Long totalIncome;
    private Long totalExpense;
    private Long balance;

    public FinanceDashboardDTO() {
    }

    public FinanceDashboardDTO(Long totalIncome, Long totalExpense, Long balance) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
    }

    public Long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Long totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}