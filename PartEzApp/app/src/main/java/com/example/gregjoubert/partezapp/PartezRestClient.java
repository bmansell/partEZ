package com.example.gregjoubert.partezapp;

/**
 * Created by gregjoubert on 2016-03-08.
 */

import com.loopj.android.http.*;

public class PartezRestClient
{
    private static final String TAG = "PartezRestClient";
    private static final String BASE_URL = "http://ec2-54-84-176-22.compute-1.amazonaws.com/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}