package ru.finopolis;

/**
 * @author eshemchik
 */
public class Response<T> {
    private final String status;
    private final T result;

    private Response(String status, T result) {
        this.status = status;
        this.result = result;
    }

    public static <T> Response<T> ok(T result) {
        return new Response<>("ok", result);
    }

    public static Response<String> error(Exception exception) {
        return new Response<>("error", exception.getMessage());
    }

    public String getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }
}
