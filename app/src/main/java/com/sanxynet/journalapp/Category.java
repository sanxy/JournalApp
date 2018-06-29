package com.sanxynet.journalapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity (tableName = "category")
public class Category {

    @PrimaryKey
    @NonNull
    private String mCategory;

    public Category(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }
}
