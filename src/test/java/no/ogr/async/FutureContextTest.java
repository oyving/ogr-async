package no.ogr.async;

import org.junit.Test;

import static org.fest.assertions.Assertions.*;
import static org.fest.assertions.Fail.*;

public class FutureContextTest {
    private FutureContext context = new FutureContext();

    @Test
    public void test_completed() {
        final Integer integer = 42;
        context.completed(integer).onComplete(
                value -> assertThat(value).isEqualTo(integer),
                error -> fail("Not supposed to get error", error)
        );
    }

    @Test
    public void test_failed() {
        final Throwable exception = new RuntimeException("Halp");
        context.failed(exception).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_future_failing() {
        final RuntimeException exception = new RuntimeException("Halp");
        context.future(() -> { throw exception; }).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_future_succeeding() {
        final Integer integer = 42;
        context.future(() -> integer).onComplete(
                value -> assertThat(value).isEqualTo(integer),
                error -> fail("Not supposed to get error", error)
        );
    }

    @Test
    public void test_future_map_succeeding() {
        final Integer integer = 42;
        context.completed(integer).map(Object::toString).onComplete(
                value -> assertThat(value).isEqualTo("42"),
                error -> fail("Not supposed to get error", error)
        );
    }

    @Test
    public void test_future_map_failing() {
        final Integer integer = 42;
        final RuntimeException exception = new RuntimeException("Halp");
        context.completed(integer).map(x -> { throw exception; }).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_failed_future_map_failing() {
        final RuntimeException exception = new RuntimeException("Halp");
        context.failed(exception).map(Object::toString).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_future_flatmap_succeeding() {
        final Integer integer = 42;
        context.completed(integer).flatMap(x -> context.future(() -> x * 2)).onComplete(
                value -> assertThat(value).isEqualTo(integer * 2),
                error -> fail("Not supposed to get error", error)
        );
    }

    @Test
    public void test_future_flatmap_failing() {
        final Integer integer = 42;
        final RuntimeException exception = new RuntimeException("Halp");
        context.completed(integer).flatMap(x -> context.failed(exception)).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_future_flatmap_failing_in_mapper() {
        final Integer integer = 42;
        final RuntimeException exception = new RuntimeException("Halp");
        context.completed(integer).flatMap(x -> { throw exception; }).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_failed_future_flatmap_failing() {
        final RuntimeException exception = new RuntimeException("Halp");
        context.failed(exception).flatMap(x -> context.future(x::toString)).onComplete(
                value -> fail("Not supposed to succeed"),
                error -> assertThat(error).isEqualTo(exception)
        );
    }

    @Test
    public void test_promise_cant_be_fulfilled_twice() {
        final Promise<Integer> promise = context.promise();
        promise.fulfill(42);
        try {
            promise.fulfill(43);
            fail("promise.fulfill not supposed to succeed on second execution");
        } catch (IllegalStateException e) {
            assertThat(e).describedAs("Promise already fulfilled");
        }

    }
}
