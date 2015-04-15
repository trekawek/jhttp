package eu.rekawek.jhttp;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Miscellaneous methods related to functional programming in Java 8.
 * 
 * @author Tomasz Rękawek
 *
 */
public final class LambdaUtils {

    private LambdaUtils() {
    }

    /**
     * Curry operation binding the given value as a first argument of the two-argument function.
     * 
     * @param function Binary function
     * @param t Argument to bind as the first parameter
     * @return Unary function
     */
    public static <T, U, R> Function<U, R> curry(BiFunction<T, U, R> function, T t) {
        return u -> function.apply(t, u);
    }

    /**
     * Cut the stream on the first element not accepting the predicate. Inspired by the <a href=
     * "http://stackoverflow.com/questions/20746429/java-8-limit-infinite-stream-by-a-predicate/24531394#24531394"
     * >StackOverflow answer</a>.
     * 
     * @param stream Stream to limit
     * @param predicate Predicate to test
     * @return A stream that ends on the first element not accepting the predicate
     */
    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
        return StreamSupport.stream(new PredicateSpliterator<T>(stream, predicate), false);
    }

    private static class PredicateSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
        private final Iterator<T> iterator;

        private final Predicate<T> predicate;

        public PredicateSpliterator(Stream<T> stream, Predicate<T> predicate) {
            super(Long.MAX_VALUE, IMMUTABLE);
            this.iterator = stream.iterator();
            this.predicate = predicate;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (iterator.hasNext()) {
                T value = iterator.next();
                if (predicate.test(value)) {
                    action.accept(value);
                    return true;
                }
            }
            return false;
        }
    }

}
