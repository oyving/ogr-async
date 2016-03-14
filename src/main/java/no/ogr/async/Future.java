package no.ogr.async;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Future<T> {

    /**
     * Create a new future that receives the value of this future through the mapping function.
     * @param mapper A function that maps a value from T to R.
     * @param <R> Return type of mapping and type of future.
     * @return A new future of type R.
     */
    <R> Future<R> map(Function<T, R> mapper);

    /**
     * Create a new future that receives the value of this future through the flat mapping function.
     * @param mapper A function that maps a value from T to future of R.
     * @param <R> Return type of the future.
     * @return A ne future of type R.
     */
    <R> Future<R> flatMap(Function<T, Future<R>> mapper);

    /**
     * Callbacks to be executed when this future completes.
     * @param onSuccess Consumer to be invoked if the future completes successfully.
     * @param onFailure Consumer to be invoked if the future completes with error.
     */
    void onComplete(Consumer<T> onSuccess, Consumer<Throwable> onFailure);
}
