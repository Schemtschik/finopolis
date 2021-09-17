package ru.finopolis.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ru.finopolis.pojo.CategoryStat;
import ru.finopolis.pojo.DepositResponse;
import ru.finopolis.pojo.Transaction;

/**
 * @author eshemchik
 */
public class TransactionsDao {
    private static final double BASE_INTEREST_RATE = 0.07;

    private final Map<Long, List<Transaction>> transactionsByUser;

    private TransactionsDao(Map<Long, List<Transaction>> transactionsByUser) {
        this.transactionsByUser = transactionsByUser;
    }

    public static TransactionsDao cons(String filename, String categoriesFilename) throws IOException {
        Map<Long, String> categoriesById = getBigCategoryById(categoriesFilename);
        List<Transaction> transactions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String s = reader.readLine();
        while ((s = reader.readLine()) != null) {
            Optional<Transaction> t = Transaction.fromString(s, categoriesById);
            if (t.isPresent()) {
                transactions.add(t.get());
            };
        }
        return new TransactionsDao(
                transactions
                        .stream()
                        .collect(Collectors.groupingBy(
                                Transaction::getClientId,
                                Collectors.toList())
                        )
        );
    }

    private static  Map<Long, String> getBigCategoryById(String filename) throws IOException {
        Map<Long, String> result = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String s = reader.readLine();
        while ((s = reader.readLine()) != null) {
            String[] arr = s.split("\t");
            result.put(Long.parseLong(arr[0]), arr[2]);
        }
        return result;
    }

    public List<Transaction> getTransactions(long userId) {
        return transactionsByUser.get(userId);
    }

    public Map<String, List<CategoryStat>> getCategoriesStat(long userId) {
        Map<String, Map<Long, List<Transaction>>> transactionsByKey = getTransactions(userId)
                .stream()
                .collect(Collectors.groupingBy(
                        t -> LocalDate.ofEpochDay(t.getTimestamp()).getMonth().getValue()
                                + "."
                                + LocalDate.ofEpochDay(t.getTimestamp()).getYear(),
                        Collectors.groupingBy(
                                Transaction::getCategoryId,
                                Collectors.toList()
                        )
                ));
        Map<String, List<CategoryStat>> result = new HashMap<>();
        transactionsByKey.forEach((key, value) -> result.put(key, value
                        .values()
                        .stream()
                        .map(transactions -> new CategoryStat(
                                transactions.get(0).getCategoryDescription(),
                                transactions.stream().mapToDouble(Transaction::getAmount).sum()
                        ))
                        .collect(Collectors.toList())
        ));
        Map<String, Double> sumHistoryByCategory = result
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        CategoryStat::getCategoryDescription,
                        Collectors.averagingDouble(CategoryStat::getAmount)
                ));
        result.put(
                "predict",
                sumHistoryByCategory
                        .entrySet()
                        .stream()
                        .map(entry -> new CategoryStat(
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .collect(Collectors.toList())
        );
        return result;
    }

    public DepositResponse getDeposit(long userId) {
        double amount = getFreeMoneyMonthly(userId);
        double afterYear = 0;
        for (int i = 0; i < 12; i++) {
            afterYear = (afterYear) * (1. + BASE_INTEREST_RATE / 12) + amount;
        }
        return new DepositResponse(amount, afterYear - amount * 12);
    }

    public Double getLoan(long userId) {
        return getFreeMoneyMonthly(userId) * 12. / (1 + BASE_INTEREST_RATE);
    }

    private double getFreeMoneyMonthly(long userId) {
        return Math.max(0.0, getTransactions(userId)
                .stream()
                .collect(Collectors.groupingBy(
                        t -> LocalDate.ofEpochDay(t.getTimestamp()).getMonth().getValue()
                                + "."
                                + LocalDate.ofEpochDay(t.getTimestamp()).getYear(),
                        Collectors.averagingDouble(Transaction::getAmount)
                ))
                .values()
                .stream()
                .mapToDouble(v -> v)
                .average()
                .orElse(0.0));
    }
}
