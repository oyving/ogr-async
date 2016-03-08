package no.ogr.async;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

final class PromisedFuture<T> implements Future<T> {

    private final ExecutorService executorService;
    private final DefaultPromise.State<T> state;

    PromisedFuture(ExecutorService executorService, DefaultPromise.State<T> state) {
        this.executorService = executorService;
        this.state = state;
    }

    public <R> Future<R> map(Function<T, R> mapper) {
        final Promise<R> promise = new DefaultPromise<R>(executorService);
        onComplete(
                (result) -> {
                    try {
                        final R v = mapper.apply(result);
                        promise.fulfill(v);
                    } catch (Throwable e) {
                        promise.fail(e);
                    }
                },
                promise::fail
        );
        return promise.future();
    }

    public <R> Future<R> flatMap(Function<T, Future<R>> mapper) {
        final Promise<R> promise = new DefaultPromise<>(executorService);
        onComplete(
                (result) -> {
                    try {
                        final Future<R> value = mapper.apply(result);
                        value.onComplete(promise::fulfill, promise::fail);
                    } catch (Throwable e) {
                        promise.fail(e);
                    }
                },
                promise::fail
        );
        return promise.future();
    }

    public Optional<T> value() {
        if (state.latch.getCount() == 0) {
            return Optional.of(state.value.get());
        } else {
            return Optional.empty();
        }
    }

    public void onComplete(Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        executorService.submit(new CompletionTask<T>(state, onSuccess, onFailure));
    }

    private static class CompletionTask<T> implements Runnable {
        final DefaultPromise.State<T> state;
        final Consumer<T> onSuccess;
        final Consumer<Throwable> onError;

        CompletionTask(DefaultPromise.State<T> state,
                       Consumer<T> onSuccess,
                       Consumer<Throwable> onError)
        {
            this.state = state;
            this.onSuccess = onSuccess;
            this.onError = onError;
        }

        @Override
        public void run() {
            try {
                state.latch.await();
                if (state.error.get() != null) {
                    onError.accept(state.error.get());
                } else {
                    onSuccess.accept(state.value.get());
                }
            } catch (InterruptedException e) {
                // do nothing, we are just terminated
            }
        }
    }
}
