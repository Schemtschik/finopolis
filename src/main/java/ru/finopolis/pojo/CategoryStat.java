package ru.finopolis.pojo;

/**
 * @author eshemchik
 */
public class CategoryStat {
    private final String categoryDescription;
    private final double amount;

    public CategoryStat( String categoryDescription, double amount) {
        this.categoryDescription = categoryDescription;
        this.amount = amount;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public double getAmount() {
        return amount;
    }
}
