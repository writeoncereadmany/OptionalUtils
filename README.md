# OptionalUtils

Some very basic static functions to encourage safe usage of Java 8 Optionals (as opposed 
to unsafe usage of Optional::get).

## Optionals.either(Optional<T> maybe, Function<T, R> ifPresent, Supplier<R> ifAbsent)

This is the recommended basic way of interacting with Optionals, because it forces the
user to consider both the present and the absent case, without presenting alluring but
unsafe options (Optional::get in particular).

The following two pieces of code are equivalent:
```java
R result = maybe.map(ifPresent).orElseGet(ifAbsent);
```
```java
R result = Optionals.either(maybe, ifPresent, ifAbsent);
```
The advantages of the latter form are:
* Naming: either is a more natural way of describing the two alternate possibilities
* Coherence: there's a single expression which is incomplete without specifying both 
alternatives, so you can't ignore the negative case
* Focus: once you've decided what to do in the positive case, there's only one shape of 
negative case to consider, unlike following map() where four alternatives exist.

## Optionals.consume(Optional<T> maybe, Consumer<T> ifPresent, Runnable ifAbsent)

This is simply an implementation of Optionals.either which returns void.

## Optionals.ifPresent() and Optionals.ifAbsent()

Optional already provides an ifPresent(Consumer<T>) method, but does not have an
equivalent overload for ifAbsent(). In the case where actions are required in both the
positive and negative cases, then consume() is also available, but sometimes it's clearer
to label them separately. Because of this, ifPresent() is implemented as a static method
for consistency with ifAbsent.

For example:
```java
maybe.ifPresent(x -> LOGGER.log("We got a thingy: " + x));
if(!maybe.isPresent()) {
   LOGGER.error("We didn't get a thingy at all :(");
}
```
```java
Optionals.consume(maybe, 
                  x -> LOGGER.log("We got a thingy: " + x),
                  () -> LOGGER.error("We didn't get a thingy at all :("));
```
```java
Optionals.ifPresent(maybe, x -> LOGGER.log("We got a thingy: " + x);
Optionals.ifAbsent(maybe, () -> LOGGER.log("We didn't get a thingy at all :("));
```
## Optionals.stream(Optional<T> maybe)

This is simply Optionals.either(maybe, Stream::of, Stream::empty), used to convert an
Optional to a stream of length 1 (if present) or 0 (if absent). This is primarily used 
to replace
```java
Stream.of(someListOfOptionals)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(toList);
```
with
```java
Stream.of(someListOfOptionals)
      .flatMap(Optionals::stream)
      .collect(toList);
```
Note that from Java 9, Optional will implement stream() at which point this formulation
will be obsoleted.
