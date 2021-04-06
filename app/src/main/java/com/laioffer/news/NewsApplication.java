package com.laioffer.news;

import android.app.Application;

import androidx.room.Room;

import com.laioffer.news.database.NewsDatabase;


public class NewsApplication extends Application {

    private NewsDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, NewsDatabase.class, "news_db").build();
    }

    public NewsDatabase getDatabase() {
        return database;
    }
}
