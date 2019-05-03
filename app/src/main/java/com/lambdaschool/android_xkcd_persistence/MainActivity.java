package com.lambdaschool.android_xkcd_persistence;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private TextView textViewHeading;
    private TextView textViewStats;
    private ImageView imageViewComic;
    private String maxNum;
    private String currentNum;

    //private XkcdComic comicForTesting;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XkcdSqlDao.closeInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHeading = findViewById(R.id.text_view_heading);
        textViewStats = findViewById(R.id.text_view_stats);
        imageViewComic = findViewById(R.id.image_view_comic);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        XkcdSqlDao.initializeInstance(this);

        (new Thread(new Runnable() {
            @Override
            public void run() {
                final XkcdComic xkcdComic = XkcdDao.getRecentComic();
                //comicForTesting = xkcdComic;
                maxNum = xkcdComic.getNum();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(xkcdComic);
                    }
                });
            }
        })).start();
        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        XkcdSqlDao.createComic(comicForTesting);
        //comicForTesting.setNum("3333333");
        comicForTesting.getXkcdDbInfo().setFavorite(1);
        comicForTesting.getXkcdDbInfo().setTimestamp(789789789);
        XkcdSqlDao.updateComic(comicForTesting);
        XkcdDbInfo xkcdDbInfo = XkcdSqlDao.readComic(Integer.parseInt(comicForTesting.getNum()));
        XkcdSqlDao.deleteComic(Integer.parseInt(comicForTesting.getNum()));*/
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = 0;
            switch (item.getItemId()) {
                case R.id.navigation_previous:
                    textViewHeading.setText(R.string.title_previous);
                    break;
                case R.id.navigation_random:
                    itemId = 1;
                    textViewHeading.setText(R.string.title_random);
                    break;
                case R.id.navigation_next:
                    itemId = 2;
                    textViewHeading.setText(R.string.title_next);
                    break;
                default:
                    return false;
            }
            (new ShowComicTask()).execute(itemId, Integer.valueOf(currentNum));

            return true;
        }
    };

    public class ShowComicTask extends AsyncTask<Integer, Void, XkcdComic> {
        @Override
        protected XkcdComic doInBackground(Integer... integers) {
            XkcdComic xkcdComic = XkcdDao.getSpecificComic(integers[1].toString());
            if (integers[0] == 0) {
                xkcdComic = XkcdDao.getPreviousComic(xkcdComic);
            } else if (integers[0] == 1) {
                xkcdComic = XkcdDao.getRandomComic();
            } else if (integers[0] == 2) {
                xkcdComic = XkcdDao.getNextComic(xkcdComic);
            }
            return xkcdComic;
        }

        @Override
        protected void onPostExecute(XkcdComic xkcdComic) {
            updateUI(xkcdComic);
        }
    }

    private void updateUI(XkcdComic xkcdComic) {
        textViewStats.setText(xkcdComic.toString());
        imageViewComic.setImageBitmap(xkcdComic.getBitmap());

        if (xkcdComic.getNum().equals(maxNum)) {
            navigation.getMenu().getItem(2).setEnabled(false);
        } else {
            navigation.getMenu().getItem(2).setEnabled(true);
        }

        if (xkcdComic.getNum().equals("1")) {
            navigation.getMenu().getItem(0).setEnabled(false);
        } else {
            navigation.getMenu().getItem(0).setEnabled(true);

        }

        currentNum = xkcdComic.getNum();
    }
}
