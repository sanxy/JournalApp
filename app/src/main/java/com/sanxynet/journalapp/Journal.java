package com.sanxynet.journalapp;

import android.app.PendingIntent;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;


@Entity (tableName = "journal")
public class Journal implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mJournalId;

    @ColumnInfo(name = "name")
    private String mJournalName;

    @ColumnInfo(name = "category")
    private Category mJournalCategory;

    @ColumnInfo(name = "date")
    private Date mJournalDate;

    @ColumnInfo(name = "time")
    private long mJournalTime;

    @ColumnInfo(name = "desc")
    private String mJournalDesc;

    @ColumnInfo(name = "priority")
    private int mJournalPriority;

    @ColumnInfo(name = "alarm")
    private boolean mJournalSetAlarm;

    public long getJournalId() {
        return mJournalId;
    }

    public void setJournalId(long mJournalId) {
        this.mJournalId = mJournalId;
    }

    public String getJournalName() {
        return mJournalName;
    }

    public void setJournalName(String mJournalName) {
        this.mJournalName = mJournalName;
    }

    public Category getJournalCategory() {
        return mJournalCategory;
    }

    public void setJournalCategory(Category mJournalCategory) {
        this.mJournalCategory = mJournalCategory;
    }

    public Date getJournalDate() {
        return mJournalDate;
    }

    public void setJournalDate(Date mJournalDate) {
        this.mJournalDate = mJournalDate;
    }

    public long getJournalTime() {
        return mJournalTime;
    }

    public void setJournalTime(long mJournalTime) {
        this.mJournalTime = mJournalTime;
    }

    public String getJournalDesc() {
        return mJournalDesc;
    }

    public void setJournalDesc(String mJournalDesc) {
        this.mJournalDesc = mJournalDesc;
    }

    public int getJournalPriority() {
        return mJournalPriority;
    }

    public void setJournalPriority(int mJournalPriority) {
        this.mJournalPriority = mJournalPriority;
    }

    public boolean isJournalSetAlarm() {
        return mJournalSetAlarm;
    }

    public void setJournalSetAlarm(boolean mJournalSetAlarm) {
        this.mJournalSetAlarm = mJournalSetAlarm;
    }

    public String getIcon() {
        return String.valueOf(mJournalName.trim().charAt(0)).toUpperCase();
    }

}
