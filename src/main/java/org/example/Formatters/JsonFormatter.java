package org.example.Formatters;

import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JsonFormatter {

    private final Gson gson;

    public JsonFormatter(Gson gson) {
        this.gson = Objects.requireNonNullElseGet(gson, () -> new GsonBuilder().setPrettyPrinting().create());
    }

    public String prettyJson(final byte[] json) {
        return prettyJson(new String(json, StandardCharsets.UTF_8));
    }

    public String prettyJson(final String json) {
        var parsedJson = JsonParser.parseString(json);
        return this.gson.toJson(parsedJson);
    }
}
