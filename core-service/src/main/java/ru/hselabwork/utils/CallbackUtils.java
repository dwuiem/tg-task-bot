package ru.hselabwork.utils;

import org.bson.types.ObjectId;

import java.util.AbstractMap;

public class CallbackUtils {
    public static AbstractMap.SimpleEntry<String, ObjectId> parseCallbackData(String callbackData) {
        String[] parts = callbackData.split(":");
        if (parts.length == 2) {
            return new AbstractMap.SimpleEntry<>(parts[0], new ObjectId(parts[1]));
        } else {
            return new AbstractMap.SimpleEntry<>(parts[0], new ObjectId());
        }
    }
}
