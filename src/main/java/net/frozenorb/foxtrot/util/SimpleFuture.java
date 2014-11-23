package net.frozenorb.foxtrot.util;

import java.util.concurrent.Callable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleFuture<V> {
    private V value;
    @NonNull private Callable<V> callable;

    public void start() {
        try {
            value = callable.call();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public V get() {
        while (value == null) {}

        return value;
    }
}
