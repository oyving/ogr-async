package no.ogr.async;

import org.junit.Test;
import static org.fest.assertions.Assertions.*;

public class FutureContextTest {
    private FutureContext context = new FutureContext();

    @Test
    public void test_completed() {
        final Integer integer = 42;
        context.completed(integer).onComplete(
                value -> assertThat(value).isEqualTo(integer),
                error -> { throw new RuntimeException(error); }
        );
    }

    @Test
    public void test_failed() {
        final Throwable exception = new RuntimeException("Halp");
        context.failed(exception).onComplete(
                value -> { throw new RuntimeException("Not supposed to succeed"); },
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_future_failing() {
        final RuntimeException exception = new RuntimeException("Halp");
        context.future(() -> { throw exception; }).onComplete(
                value -> { throw new RuntimeException("Not supposed to succeed"); },
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_future_succeeding() {
        final Integer integer = 42;
        context.future(() -> integer).onComplete(
                value -> assertThat(value).isEqualTo(integer),
                error -> { throw new RuntimeException(error); }
        );
    }
}
