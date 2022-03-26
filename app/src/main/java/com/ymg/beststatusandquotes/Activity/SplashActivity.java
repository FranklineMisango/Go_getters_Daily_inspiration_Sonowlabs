package com.ymg.beststatusandquotes.Activity;


import static com.ymg.beststatusandquotes.Config.ADMOB_PUB_ID;
import static com.ymg.beststatusandquotes.Config.AD_STATUS;
import static com.ymg.beststatusandquotes.Config.AD_TYPE;
import static com.ymg.beststatusandquotes.Config.APPLOVIN_BANNER_ID;
import static com.ymg.beststatusandquotes.Config.APPLOVIN_INTER_ID;
import static com.ymg.beststatusandquotes.Config.BANNER_ID;
import static com.ymg.beststatusandquotes.Config.DEVELOPER_NAME;
import static com.ymg.beststatusandquotes.Config.FACEBOOK_BANNER_ID;
import static com.ymg.beststatusandquotes.Config.FACEBOOK_INTER_ID;
import static com.ymg.beststatusandquotes.Config.FACEBOOK_NATIVE_ID;
import static com.ymg.beststatusandquotes.Config.INTERSTITIAL_ADS_INTERVAL;
import static com.ymg.beststatusandquotes.Config.INTER_ID;
import static com.ymg.beststatusandquotes.Config.NATIVE_ADS_INTERVAL;
import static com.ymg.beststatusandquotes.Config.NATIVE_ADS_POSITION;
import static com.ymg.beststatusandquotes.Config.NATIVE_ID;
import static com.ymg.beststatusandquotes.Config.STARTAPP_ID;
import static com.ymg.beststatusandquotes.Config.UNITY_BANNER_ID;
import static com.ymg.beststatusandquotes.Config.UNITY_GAME_ID;
import static com.ymg.beststatusandquotes.Config.UNITY_INTER_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.ads.AdSettings;
import com.ymg.beststatusandquotes.Config;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.AdsPref;
import com.ymg.beststatusandquotes.Utils.Constant;
import com.ymg.beststatusandquotes.Utils.PrefManager;
import com.ymg.beststatusandquotes.Utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    AdsPref adsPref;
    SharedPref sharedPref;
    PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(this ,R.color.colorBack2));

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        prf = new PrefManager(this);

        getData();

    }

    private void getData() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constant.URL_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    final String VDN = response.getString("DN");
                    prf.setString("VDN",VDN);

                    getConfig();

                    Timer myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // If you want to modify a view in your Activity
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                    finish();

                                }
                            });
                        }
                    }, 1000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void getConfig() {
        adsPref.saveAds(
                AD_STATUS,
                AD_TYPE,
                ADMOB_PUB_ID,
                ADMOB_PUB_ID,
                BANNER_ID,
                INTER_ID,
                NATIVE_ID,
                DEVELOPER_NAME,
                FACEBOOK_BANNER_ID,
                FACEBOOK_INTER_ID,
                FACEBOOK_NATIVE_ID,
                STARTAPP_ID,
                UNITY_GAME_ID,
                UNITY_BANNER_ID,
                UNITY_INTER_ID,
                APPLOVIN_BANNER_ID,
                APPLOVIN_INTER_ID,
                INTERSTITIAL_ADS_INTERVAL,
                NATIVE_ADS_INTERVAL,
                NATIVE_ADS_POSITION,
                prf.getString("VDN"),
                ""
        );
    }

    public void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}