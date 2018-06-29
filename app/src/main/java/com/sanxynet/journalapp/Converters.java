package com.sanxynet.journalapp;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;


public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Category stringToCategory(String category){
        return category == null ? null : new Category(category);
    }

    @TypeConverter
    public static String categoryToString(Category category){
        return category == null ? null : category.getCategory();
    }
}
