package com.example.joe.test;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by joe on 16/7/22.
 */
public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;
    //近期最少使用缓存(底层使用LinkedHashMap保存)
    private LruCache<String, Bitmap> mCache;

    public ImageLoader() {
        //最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //设置当前缓存
        int CacheSize = maxMemory / 5;
        mCache = new LruCache<String, Bitmap>(CacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回当前图片大小
                return value.getByteCount();
            }
        };
    }

    //添加bitmap到缓存
    public void addBitmapToCache(String url,Bitmap bitmap) {

        //缓存中未包含此图片
        if (getBitmapFromCache(url) == null) {
            //将bitmap保存到缓存
            mCache.put(url, bitmap);
        }

    }
    //从缓存获取bitmap
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    //多线程实现
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap((Bitmap) msg.obj);
//            }
//
//        }
//    };
//
//    public void showImage(ImageView imageView, final String url) {
//        mImageView = imageView;
//        mUrl = url;
//
//        new Thread() {
//
//            @Override
//            public void run() {
//                super.run();
//                Bitmap bitmap = getBitmap(url);
//                Message message = Message.obtain();
//                message.obj = bitmap;
//                mHandler.sendMessage(message);
//            }
//        }.start();
//    }

    public Bitmap getBitmapFromURL(String url) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showAsyncTask(ImageView imageView, String url) {

        Bitmap bitmap = getBitmapFromCache(url);
        //检查是否存在缓存
        if (bitmap == null) {
            new NewsAsyncTask(imageView, url).execute(url);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String murl;
        private ImageView mImageView;

        public NewsAsyncTask(ImageView imageView, String url) {
            this.murl = url;
            this.mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String url = params[0];
            Bitmap bitmap = getBitmapFromURL(url);
            //添加到缓存
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(murl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }
}