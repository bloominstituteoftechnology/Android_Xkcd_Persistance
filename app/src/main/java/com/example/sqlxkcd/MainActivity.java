package com.example.sqlxkcd;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private SqlXkcdDao dbDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        dbDao = new SqlXkcdDao(this);
        XkcdDbHelper helper = new XkcdDbHelper(this);
        final SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        new Thread(new Runnable() {
            public void run() {
                XkcdDAO.getSpecificComic(343);
               List<XkcdComic> comics = dbDao.getAllComics();
            }
        }).start();
    }
}
