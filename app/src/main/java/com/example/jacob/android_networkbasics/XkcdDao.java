package com.example.jacob.android_networkbasics;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

public class XkcdDao {
    private final static String BASE_URL = "https://xkcd.com/";
    private final static String END_URL = "/info.0.json";
    private final static String RECENT_COMIC = BASE_URL + END_URL;
    private final static String SPECIFIC_COMIC = BASE_URL + "%d/" + END_URL;
    public static int maxComicNumber;
    public static XkcdComic currentComic;


    public static XkcdComic getComic(String urlString) {
        String url = NetworkAdapter.httpRequest(urlString);
        XkcdComic comic = null;
        try {
            JSONObject comicAsJson = new JSONObject(url);
            comic = new XkcdComic(comicAsJson);

            String imageUrl = null;
            imageUrl = comic.getImg();
            if (imageUrl != null) {
                Bitmap bitmap;
                bitmap = NetworkAdapter.httpImageRequest(imageUrl);
                if (bitmap != null) {
                    comic.setBitmap(bitmap);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return comic;
        }
        currentComic = comic;
        if (comic != null) {
            XkcdDbInfo comicDbInfo = XkcdDbDao.readComic(comic.getNum());
            if (comicDbInfo == null) {
                comicDbInfo = new XkcdDbInfo(0, 0);
                comicDbInfo.setTimestamp(Math.toIntExact(System.currentTimeMillis() / 1000));
                comicDbInfo.setFavorite(0);
                comic.setXkcdDbInfo(comicDbInfo);
                XkcdDbDao.createComic(comic);
            } else {
                comicDbInfo.setTimestamp(Math.toIntExact(System.currentTimeMillis() / 1000));
                comic.setXkcdDbInfo(comicDbInfo);
                XkcdDbDao.updateComic(comic);
            }
        }
        return comic;
    }

    public static XkcdComic getComic(int id) {
        XkcdComic comic = null;
        String url = SPECIFIC_COMIC.replace("%d/", Integer.toString(id));
        comic = getComic(url);
        return comic;
    }

    public static XkcdComic getRecentComic() {
        XkcdComic comic = null;
        comic = getComic(RECENT_COMIC);
        maxComicNumber = comic.getNum();
        return comic;
    }

    public static XkcdComic getNextComic() {
        XkcdComic comic = null;
        int comicNum = -1;
        comicNum = currentComic.getNum();
        if (comicNum != -1) {
            comicNum += 1;
            String url = SPECIFIC_COMIC.replace("%d/", Integer.toString(comicNum));
            comic = getComic(url);
        }
        return comic;
    }

    public static XkcdComic getPreviousComic() {
        XkcdComic comic = null;
        int comicNum = -1;
        comicNum = currentComic.getNum();
        if (comicNum != -1) {
            comicNum -= 1;
            String url = SPECIFIC_COMIC.replace("%d/", Integer.toString(comicNum));
            comic = getComic(url);
        }
        return comic;
    }

    public static XkcdComic getRandomComic() {
        XkcdComic comic = null;
        String randomNumber = String.valueOf((Math.round(Math.random() * maxComicNumber + 1)));
        String url = SPECIFIC_COMIC.replace("%d/", randomNumber);
        comic = getComic(url);
        return comic;
    }

    public static void setFavorite(boolean favorite) {
        if (favorite) {
            currentComic.getXkcdDbInfo().setFavorite(1);
        } else {
            currentComic.getXkcdDbInfo().setFavorite(0);
        }
        XkcdDbDao.updateComic(currentComic);
    }
}
