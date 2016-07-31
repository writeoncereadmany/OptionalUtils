package com.writeoncereadmany.optionals;


import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Optionals
{
    public static <I, O> O either(Optional<I> optional, Function<I, O> onPresent, Supplier<O> onAbsent)
    {
        return optional.map(onPresent).orElseGet(onAbsent);
    }

    public static <I> void consume(Optional<I> optional, Consumer<I> onPresent, Runnable onAbsent)
    {
        if(optional.isPresent())
        {
            optional.ifPresent(onPresent);
        }
        else
        {
            onAbsent.run();
        }
    }

    public static <T> Stream<T> stream(Optional<T> optional)
    {
        return either(optional, Stream::of, Stream::empty);
    }

    public static <T> void ifPresent(Optional<T> optional, Consumer<T> onPresent)
    {
        consume(optional, onPresent, () -> {});
    }

    public static <T> void ifAbsent(Optional<T> optional, Runnable onAbsent)
    {
        consume(optional, x -> {}, onAbsent);
    }
}
