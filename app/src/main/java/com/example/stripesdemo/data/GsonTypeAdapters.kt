package com.example.stripesdemo.data

import com.google.gson.*
import java.lang.reflect.Type
import java.util.regex.Pattern

class PatternTypeAdapter : JsonDeserializer<Pattern>, JsonSerializer<Pattern> {
    override fun deserialize(
        jsonElement: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Pattern {
        val jsonObject: JsonObject = jsonElement.asJsonObject
        val pattern: String = jsonObject.get("pattern").asString
        if (!jsonObject.has("flags")) {
            return Pattern.compile(pattern)
        }
        return Pattern.compile(pattern, jsonObject.get("flags").asInt);
    }

    override fun serialize(
        pattern: Pattern,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("pattern", pattern.pattern())
        jsonObject.addProperty("flags", pattern.flags())
        return jsonObject
    }
}