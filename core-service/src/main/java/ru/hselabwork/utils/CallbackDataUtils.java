package ru.hselabwork.utils;

import org.bson.types.ObjectId;

import java.util.AbstractMap;

public class CallbackDataUtils {
    public static AbstractMap.SimpleEntry<String, ObjectId> parseCallbackData(String callbackData) {
        String[] parts = callbackData.split(":");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid callback data");
        }
        return new AbstractMap.SimpleEntry<>(parts[0], new ObjectId(parts[1]));
    }
}
