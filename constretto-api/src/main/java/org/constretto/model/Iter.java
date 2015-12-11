package org.constretto.model;

import java.util.Iterator;

public class Iter<T> implements Iterable<T> {

    private Iterable<T>[] input;

    public Iter(Iterable<T>... input) {
        this.input = input;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                if (index >= input.length) {
                    return false;
                }
                return input[index].iterator().hasNext();
            }

            @Override
            public T next() {
                return input[index++].iterator().next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
