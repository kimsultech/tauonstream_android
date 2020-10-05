package com.kangtech.tauonstream.util;


import android.app.Application;
import android.content.SharedPreferences;

import com.kangtech.tauonstream.MainActivity;
import com.kangtech.tauonstream.api.ApiServiceInterface;
import com.kangtech.tauonstream.api.RetrofitClient;

import static android.content.Context.MODE_PRIVATE;

public class Server extends Application {

        static String ip = SharedPreferencesUtils.getString("ip", "127.0.0.1");
        static String port = SharedPreferencesUtils.getString("port", "7590");

        public final static String BASE_URL = ip;



        public static ApiServiceInterface getApiServiceInterface() {
                return RetrofitClient.getClient("http://" + BASE_URL + ":" + port + "/").create(ApiServiceInterface.class);
        }
}
