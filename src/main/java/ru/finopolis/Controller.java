package ru.finopolis;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.finopolis.data.TransactionsDao;
import ru.finopolis.pojo.CategoryStat;
import ru.finopolis.pojo.DepositResponse;
import ru.finopolis.pojo.Transaction;

/**
 * @author eshemchik
 */
@RestController
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final TransactionsDao transactionsDao;

    public Controller() throws IOException {
        this.transactionsDao = TransactionsDao.cons(
                "/home/eshemchik/finopolis/transactions.tsv",
                "/home/eshemchik/finopolis/categories.tsv"
        );
    }

    @RequestMapping(value = "/get-transactions")
    public Response<List<Transaction>> getPlaces(@RequestParam("id") long id) {
        return run(() -> transactionsDao.getTransactions(id));
    }

    @RequestMapping(value = "/get-categories-stat")
    public Response<Map<String, List<CategoryStat>>> getCategoriesStat(@RequestParam("id") long id) {
        return run(() -> transactionsDao.getCategoriesStat(id));
    }

    @RequestMapping(value = "/get-deposit")
    public Response<DepositResponse> getDeposit(@RequestParam("id") long id) {
        return run(() -> transactionsDao.getDeposit(id));
    }

    @RequestMapping(value = "/get-loan")
    public Response<Double> getLoan(@RequestParam("id") long id) {
        return run(() -> transactionsDao.getLoan(id));
    }

    private <T> Response run(Supplier<T> supplier) {
        try {
            return Response.ok(supplier.get());
        } catch (Exception e) {
            return Response.error(e);
        }
    }

    private Response run(Runnable runnable) {
        try {
            runnable.run();
            return Response.ok("");
        } catch (Exception e) {
            return Response.error(e);
        }
    }
}
