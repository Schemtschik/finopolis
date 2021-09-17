package ru.finopolis.pojo;

/**
 * @author eshemchik
 */
public class DepositResponse {
    private final double monthlyAmount;
    private final double profit;

    public DepositResponse(double monthlyAmount, double profit) {
        this.monthlyAmount = monthlyAmount;
        this.profit = profit;
    }

    public double getMonthlyAmount() {
        return monthlyAmount;
    }

    public double getProfit() {
        return profit;
    }
}
