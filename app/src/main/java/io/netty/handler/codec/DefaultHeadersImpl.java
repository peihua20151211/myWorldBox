package io.netty.handler.codec;

import io.netty.handler.codec.DefaultHeaders.NameValidator;
import io.netty.util.HashingStrategy;

public final class DefaultHeadersImpl<K, V> extends DefaultHeaders<K, V, DefaultHeadersImpl<K, V>> {
    public DefaultHeadersImpl(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, NameValidator<K> nameValidator) {
        super(nameHashingStrategy, valueConverter, nameValidator);
    }
}
