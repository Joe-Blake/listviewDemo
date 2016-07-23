package com.example.joe.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{

    ListView mlistView;
    private static String newsURL = "http://www.imooc.com/api/teacher?type=4&num=30";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mlistView = (ListView) findViewById(R.id.lv);
        new NewsAsyncTask().execute(newsURL);   //传递newsURL给doInBackground()
    }

    /*
    * 网络异步访问
    * */
    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsBeans);
            mlistView.setAdapter(adapter);
        }
    }

    /*
    *将url中的json数据转换为自定义的NewsBean
    * */
    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jsonString = readStream(new URL(url).openStream());
            Log.d("xys", jsonString);
            JSONObject jsonObject;
            NewsBean newsBean;
            try {
                jsonObject = new JSONObject(jsonString);
                //data是一个jsonArray
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                //遍历获取jsonArray中各元素,添加到newsBeanList中
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    newsBean = new NewsBean();
                    newsBean.newsContent = jsonObject.getString("description");
                    newsBean.newsImageRes = jsonObject.getString("picSmall");
                    newsBean.newsTitle = jsonObject.getString("name");
                    newsBeanList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }

    //解析网页返回的数据
    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while((line=br.readLine())!=null) {
                result += line;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
