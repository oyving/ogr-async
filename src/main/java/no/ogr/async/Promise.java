package no.ogr.async;

public interface Promise<T> {
    Future<T> future();
    void fulfill(T value);
    void fail(Throwable error);
}
