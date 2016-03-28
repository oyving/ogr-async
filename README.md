# Simple implementation of Future and Promise

[![Build Status](https://travis-ci.org/oyving/ogr-async.svg?branch=master)](https://travis-ci.org/oyving/ogr-async)

```java
FutureContext context = new FutureContext();

// create a fulfilled future
context.completed(42).onComplete(
    System.out::println, // prints "42"
    System.err::println  // prints nothing - this is not an error
);

// create a failed promise
context.failed(new RuntimeException()).onComplete(
    System.out::println, // prints nothing - this is not a success
    System.err::println  // prints "java.lang.RuntimeException"
);

// create an async computation
context.future(
    () -> {
        try {
            Thread.sleep(1000);
            return 42;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
).onComplete(
    System.out::println,
    System.err::println
);

Future<Integer> f1 = context.future(() -> 42);
Future<String> f2 = f1.map(Object::toString);
```

