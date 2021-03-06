package com.google.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

class Gson$FutureTypeAdapter<T> extends TypeAdapter<T> {
    private TypeAdapter<T> delegate;

    Gson$FutureTypeAdapter() {
    }

    public void setDelegate(TypeAdapter<T> typeAdapter) {
        if (this.delegate != null) {
            throw new AssertionError();
        }
        this.delegate = typeAdapter;
    }

    public T read(JsonReader in) throws IOException {
        if (this.delegate != null) {
            return this.delegate.read(in);
        }
        throw new IllegalStateException();
    }

    public void write(JsonWriter out, T value) throws IOException {
        if (this.delegate == null) {
            throw new IllegalStateException();
        }
        this.delegate.write(out, value);
    }
}
