package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class Serializer extends StdSerializer<String> {
    public Serializer() {
        this(null);
    }

    public Serializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String name, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (name != null && !name.equals("")) {
            gen.writeString(name);
        } else {
            gen.writeString("");
        }
    }
}