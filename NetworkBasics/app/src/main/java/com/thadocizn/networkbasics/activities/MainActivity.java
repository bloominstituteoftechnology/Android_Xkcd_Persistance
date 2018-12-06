package com.thadocizn.networkbasics.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thadocizn.networkbasics.classes.NetworkAdapter;
import com.thadocizn.networkbasics.R;
import com.thadocizn.networkbasics.Xkcd.XkcdComic;
import com.thadocizn.networkbasics.Xkcd.XkcdDao;


public class MainActivity extends AppCompatActivity {
    private static final String HTTP_REQUEST = "https://xkcd.com/info.0.json";
    private Button previous, random, next;
    XkcdComic comicTracker, preComic, nextComic, ranComic;
    TextView title;
    ImageView image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previous = findViewById(R.id.btnPrevious);
        random = findViewById(R.id.btnRandom);
        next = findViewById(R.id.btnNext);
        title = (TextView) findViewById(R.id.tvTitle);
        image = findViewById(R.id.ivComic);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       preComic = XkcdDao.getPreviousComic(comicTracker);
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               updateUI(preComic);
                               comicTracker = preComic;
                           }
                       });

                    }
                }).start();
            }
        });

        findViewById(R.id.btnRandom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       ranComic = XkcdDao.getRandomComic();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUI(ranComic);
                                comicTracker = ranComic;
                            }
                        });
                    }
                }).start();
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                     nextComic =   XkcdDao.getNextComic(comicTracker);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            comicTracker = nextComic;
                            updateUI(nextComic);

                        }
                    });
                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkAdapter.httpRequest(HTTP_REQUEST);
                comicTracker = XkcdDao.getRecentComic();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(comicTracker);
                    }
                });
            }
        }).start();
    }

    public void updateUI(XkcdComic comic){
        Log.i("Chrl","test" + comic.getTitle());
            title.setText(comic.getTitle());
            image.setImageBitmap(comic.getImage());
            if (comic.getNum() == XkcdDao.maxComicNumber){
                next.setEnabled(false);
            }else {
                next.setEnabled(true);
            }
            if (comic.getNum() == 1){
                previous.setEnabled(false);
            }else {
                previous.setEnabled(true);
            }
        }
    }
