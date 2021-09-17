package ru.finopolis.pojo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * @author eshemchik
 */
public class Transaction {
    private final long clientId;
    private final long cardId;
    private final boolean isCredit;
    private final String date;
    private final long timestamp;
    private final double amount;
    private final String currency;
    private final String description;
    private final long categoryId;
    private final String categoryDescription;

    public Transaction(
            long clientId,
            long cardId,
            boolean isCredit,
            String date, long timestamp,
            double amount,
            String currency,
            String description,
            long categoryId,
            String categoryDescription)
    {
        this.clientId = clientId;
        this.cardId = cardId;
        this.isCredit = isCredit;
        this.date = date;
        this.timestamp = timestamp;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryDescription = categoryDescription;
    }

    public long getClientId() {
        return clientId;
    }

    public long getCardId() {
        return cardId;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public static Optional<Transaction> fromString(String line, Map<Long, String> categoriesById) {
        String[] arr = line.split("\t");
        try {
            return Optional.of(new Transaction(
                    Long.parseLong(arr[0]),
                    Long.parseLong(arr[1]),
                    arr[2].equalsIgnoreCase("Да"),
                    arr[3],
                    LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(arr[3])).toEpochDay(),
                    Double.parseDouble(arr[4].replace(",", ".")),
                    arr[5],
                    arr[6],
                    Long.parseLong(arr[7]),
                    categoriesById.containsKey(Long.parseLong(arr[7]))
                            && !categoriesById.get(Long.parseLong(arr[7])).equals("Unique")
                            ? categoriesById.get(Long.parseLong(arr[7]))
                            : arr[8].substring(arr[8].indexOf(' '))
            ));
        } catch (ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "clientId=" + clientId +
                ", cardId=" + cardId +
                ", isCredit=" + isCredit +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", categoryId=" + categoryId +
                ", categoryDescription='" + categoryDescription + '\'' +
                '}';
    }
}
