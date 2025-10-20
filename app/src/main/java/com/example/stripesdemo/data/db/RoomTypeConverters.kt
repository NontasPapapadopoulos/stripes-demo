package com.example.stripesdemo.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.time.LocalDateTime
import java.util.regex.Pattern

class RoomTypeConverters {

    @TypeConverter
    fun stringToMap(value: String): Map<String, String> {
        return gson.fromJson(value, object : TypeToken<Map<String, String>>() {}.type)
    }

    @TypeConverter
    fun mapToString(value: Map<String, String>?): String {
        return if (value == null) "" else gson.toJson(value)
    }


    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? =
        dateString?.let { LocalDateTime.parse(dateString) }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? = date?.let { date.toString() }

    @TypeConverter
    fun toPattern(value: String?): Pattern? =
        value?.let { Pattern.compile(it) }

    @TypeConverter
    fun toPatternString(pattern: Pattern?): String? = pattern?.pattern()


    companion object {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Pattern::class.java, PatternTypeAdapter())
            .create()
    }
}