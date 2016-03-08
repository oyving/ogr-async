package no.ogr.async;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FutureContext {

    private final ExecutorService executorService;

    public FutureContext() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public FutureContext(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <T> Future<T> future(Supplier<T> supplier) {
        final Promise<T> promise = new DefaultPromise<>(executorService);
        executorService.submit(new FutureTask<T>(supplier, promise));
        return promise.future();
    }

    public <T> Future<T> completed(T value) {
        final Promise<T> promise = new DefaultPromise<>(executorService);
        final Future<T> future = promise.future();
        promise.fulfill(value);
        return future;
    }

    public <T> Future<T> failed(Throwable error) {
        final Promise<T> promise = new DefaultPromise<>(executorService);
        final Future<T> future = promise.future();
        promise.fail(error);
        return future;
    }

    public <T> Promise<T> promise() {
        return new DefaultPromise<>(executorService);
    }

    private static class FutureTask<T> implements Runnable {
        private final Supplier<T> computation;
        private final Promise<T> promise;

        private FutureTask(Supplier<T> computation, Promise<T> promise) {
            this.computation = computation;
            this.promise = promise;
        }


        @Override
        public void run() {
            try {
                final T value = computation.get();
                promise.fulfill(value);
            } catch (Throwable e) {
                promise.fail(e);
            }
        }
    }
}