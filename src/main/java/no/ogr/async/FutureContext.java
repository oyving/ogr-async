package no.ogr.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class FutureContext {

    private final ExecutorService executorService;

    public FutureContext() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public FutureContext(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Create a future that executes the supplier in this context's thread pool.
     * @param supplier The computation to execute.
     * @param <T> Return type of the computation.
     * @return A future which will contain the value of the supplier on success or an exception if it failed.
     */
    public <T> Future<T> future(Supplier<T> supplier) {
        return unit().map(x -> supplier.get());
    }

    /**
     * Create a completed future with the given value.
     * @param value Value to contain in the future.
     * @param <T> Type of the value.
     * @return A completed future containing the value.
     */
    public <T> Future<T> completed(T value) {
        final Promise<T> promise = new DefaultPromise<>(executorService);
        final Future<T> future = promise.future();
        promise.fulfill(value);
        return future;
    }

    /**
     * Create a failed future with the given error.
     * @param error Error to contain in the future.
     * @param <T> Type of the future.
     * @return A failed future containing the error.
     */
    public <T> Future<T> failed(Throwable error) {
        final Promise<T> promise = new DefaultPromise<>(executorService);
        final Future<T> future = promise.future();
        promise.fail(error);
        return future;
    }

    /**
     * @return A complete future containing no value.
     */
    public Future<Void> unit() {
        return completed(null);
    }

    /**
     * @return A future that never completes.
     */
    public Future<Void> never() {
        final Promise<Void> promise = new DefaultPromise<>(executorService);
        return promise.future();
    }

    /**
     * @param <T> Type of the promise
     * @return A promise of given type.
     */
    public <T> Promise<T> promise() {
        return new DefaultPromise<>(executorService);
    }

}
