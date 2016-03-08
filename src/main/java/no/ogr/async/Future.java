package no.ogr.async;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Future<T> {
    <R> Future<R> map(Function<T, R> mapper);
    <R> Future<R> flatMap(Function<T, Future<R>> mapper);
    void onComplete(Consumer<T> onSuccess, Consumer<Throwable> onFailure);
}
