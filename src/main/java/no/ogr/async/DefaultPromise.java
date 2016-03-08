package no.ogr.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public final class DefaultPromise<T> implements Promise<T> {

    private final ExecutorService executorService;
    private final State<T> state = new State<T>();

    DefaultPromise(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Future<T> future() {
        return new PromisedFuture<T>(executorService, state);
    }

    public void fulfill(T value) {
        synchronized (state) {
            if (state.latch.getCount() == 0) {
                throw new IllegalStateException("Promise already fulfilled");
            }

            state.value.set(value);
            state.latch.countDown();
        }
    }

    public void fail(Throwable throwable) {
        synchronized (this) {
            if (state.latch.getCount() == 0) {
                throw new IllegalStateException("Promise already fulfilled");
            }

            state.error.set(throwable);
            state.latch.countDown();
        }
    }

    static class State<A> {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<A> value = new AtomicReference<>(null);
        final AtomicReference<Throwable> error = new AtomicReference<>(null);
    }
}
