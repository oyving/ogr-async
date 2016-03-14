package no.ogr.async;

public interface Promise<T> {

    /**
     * @return A future that will be completed when fulfill or fail is invoked.
     */
    Future<T> future();

    /**
     * Complete the promise (and associated futures) with the given value.
     *
     * May only be invoked once. May not be invoked if fail has been invoked.
     *
     * @param value The value to fulfill the promise
     * @throws IllegalStateException If Promise is already fulfilled or failed.
     */
    void fulfill(T value);

    /**
     * Fail the promise (and associated futures) with the given error.
     *
     * May only be invoked once. May not be invoked if fulfill has been invoked.
     *
     * @param error The error to break the promise
     * @throws IllegalStateException If Promise is already fulfilled or failed.
     */
    void fail(Throwable error);
}
