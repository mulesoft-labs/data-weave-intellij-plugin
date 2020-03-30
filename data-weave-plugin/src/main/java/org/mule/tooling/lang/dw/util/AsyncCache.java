package org.mule.tooling.lang.dw.util;


import org.mule.tooling.lang.dw.WeaveConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This async cache blocks a maximum of {@value WeaveConstants#SERVER_TIMEOUT}ms resolving a value.
 * If the timeout is reached and there's a previous value, it is returned, if not then null.
 * Even if there's a timeout the result is stored.
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class AsyncCache<K, V> {

    private BiConsumer<K, Consumer<V>> resolver;
    private Map<K, CacheEntry<V>> cache = new HashMap<>();
    private long serverTimeout = WeaveConstants.SERVER_TIMEOUT;

    /**
     * This constructor should be used when the resolver is non-blocking.
     * The 2nd parameter of the BiConsumer is the cache callback that should be executed on completion, so that
     * the value can be
     */
    public AsyncCache(BiConsumer<K, Consumer<V>> resolver) {
        this.resolver = resolver;
    }

    /**
     * This constructor should be used when the resolver is blocking
     */
    public AsyncCache(Function<K, V> resolverFn) {
        this.resolver = toBiconsumer(resolverFn);
    }

    public AsyncCache<K, V> withTimeOut(long serverTimeout) {
        this.serverTimeout = serverTimeout;
        return this;
    }

    public void invalidate(K key) {
        if (cache.containsKey(key)) {
            cache.get(key).invalidate();
        }
    }

    public void invalidateAll() {
        cache.values().forEach((e) -> e.invalidate());
    }

    public Optional<V> resolve(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && entry.isValid()) {
            return Optional.of(entry.getValue());
        }

        final CompletableFuture<V> futureResult = new CompletableFuture<>();
        resolver.accept(key, (value) -> {
            cache.put(key, new CacheEntry<>(value));
            futureResult.complete(value);
        });
        try {

            return Optional.of(futureResult.get(serverTimeout, TimeUnit.MILLISECONDS));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (entry != null) {
                return Optional.of(entry.getValue());
            } else {
                return Optional.empty();
            }
        }
    }

    public void setResolver(BiConsumer<K, Consumer<V>> resolver) {
        this.resolver = resolver;
    }

    public void setResolver(Function<K, V> resolverFn) {
        this.resolver = toBiconsumer(resolverFn);
    }

    private static <K, V> BiConsumer<K, Consumer<V>> toBiconsumer(Function<K, V> resolverFn) {
        return (key, callback) -> callback.accept(resolverFn.apply(key));
    }
}