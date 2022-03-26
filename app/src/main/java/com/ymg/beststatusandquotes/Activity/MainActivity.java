package com.ymg.beststatusandquotes.Activity;

import static com.ymg.beststatusandquotes.Utils.Constant.ADMOB;
import static com.ymg.beststatusandquotes.Utils.Constant.AD_STATUS_ON;
import static com.ymg.beststatusandquotes.Utils.Constant.APPLOVIN;
import static com.ymg.beststatusandquotes.Utils.Constant.BANNER_HOME;
import static com.ymg.beststatusandquotes.Utils.Constant.INTERSTITIAL_POST_LIST;
import static com.ymg.beststatusandquotes.Utils.Constant.STARTAPP;
import static com.ymg.beststatusandquotes.Utils.Constant.UNITY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.onesignal.OneSignal;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.ymg.beststatusandquotes.Adapter.CategoriesListAdapter;
import com.ymg.beststatusandquotes.BuildConfig;
import com.ymg.beststatusandquotes.Config;
import com.ymg.beststatusandquotes.Model.Category;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.AdNetwork;
import com.ymg.beststatusandquotes.Utils.AdsPref;
import com.ymg.beststatusandquotes.Utils.DataBaseHandler;
import com.ymg.beststatusandquotes.Utils.GDPR;
import com.ymg.beststatusandquotes.Utils.PrefManager;
import com.ymg.beststatusandquotes.Utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{


    DataBaseHandler db;
    private AlertDialog dialog;
    public static final int IntialQteOfDayId = 8;
    private LinearLayout ll_liked_quotes, ll_todays_quote, llMaker, ll_sounds, ll_about, ll_contact_us, ll_rate_app, ll_share_app, ll_like_fb, ll_follow_insta,ll_privacy_policy ;
    final Context context = this;
    SharedPreferences preferences;
    private static final int RESULT_SETTINGS = 1;
    private ArrayList<Category> imageArry = new ArrayList<Category>();
    private CategoriesListAdapter adapter;
    private GridView dataList;
    EditText searchedit;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private int STORAGE_PERMISSION_CODE = 1;
    private Boolean DialogOpened = false;
    PrefManager prf;
    private static final String ONESIGNAL_APP_ID = "########-####-####-####-############";

    AdNetwork adNetwork;
    AdsPref adsPref;
    private static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar  = findViewById(R.id.toolbar);
        setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //onesignal setup
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        db = new DataBaseHandler(this);
        db.openDataBase() ;

        adsPref = new AdsPref(this);
        prf = new PrefManager(this);

        if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
            switch (adsPref.getAdType()) {
                case STARTAPP:
                    StartAppSDK.setUserConsent(this, "pas", System.currentTimeMillis(), true);
                    StartAppAd.disableSplash();
                    break;
                case ADMOB:
                    MobileAds.initialize(this, initializationStatus -> {
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                            assert status != null;
                            Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                            Log.d("Open Bidding", "FAN open bidding with AdMob as mediation partner selected");
                        }
                    });
                    GDPR.updateConsentStatus(this);
                    break;
                case UNITY:
                    UnityAds.addListener(new IUnityAdsListener() {
                        @Override
                        public void onUnityAdsReady(String placementId) {
                            Log.d(TAG, placementId);
                        }

                        @Override
                        public void onUnityAdsStart(String placementId) {

                        }

                        @Override
                        public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {

                        }

                        @Override
                        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String message) {

                        }
                    });
                    UnityAds.initialize(getApplicationContext(), adsPref.getUnityGameId(), BuildConfig.DEBUG, new IUnityAdsInitializationListener() {
                        @Override
                        public void onInitializationComplete() {
                            Log.d(TAG, "Unity Ads Initialization Complete");
                            Log.d(TAG, "Unity Ads Game ID : " + adsPref.getUnityGameId());
                        }

                        @Override
                        public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                            Log.d(TAG, "Unity Ads Initialization Failed: [" + error + "] " + message);
                        }
                    });
                    break;
                case APPLOVIN:
                    AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
                    AppLovinSdk.getInstance(this).initializeSdk(config -> {
                    });
                    final String sdkKey = AppLovinSdk.getInstance(getApplicationContext()).getSdkKey();
                    if (!sdkKey.equals(getString(R.string.applovin_sdk_key))) {
                        Log.e(TAG, "AppLovin ERROR : Please update your sdk key in the manifest file.");
                    }
                    Log.d(TAG, "AppLovin SDK Key : " + sdkKey);
                    break;
            }
        }

        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadInterstitialAdNetwork(INTERSTITIAL_POST_LIST);
        adNetwork.loadApp(prf.getString("VDN"));


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_action_action);

        db = new DataBaseHandler(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("IS_FIRST_TIME", true)) {
            showPermissionDialog();
            sharedPreferences.edit().putBoolean("IS_FIRST_TIME", false).apply();
        }

        final List<Category> categories = db.getAllCategories("");
        for (Category cat : categories) {
            imageArry.add(cat);
        }

        //Adapter Code
        adapter = new CategoriesListAdapter(this, R.layout.category_items,imageArry);
        dataList = findViewById(R.id.categoryList);
        dataList.setAdapter(adapter);
        dataList.setTextFilterEnabled(true);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long idInDB) {

                Category srr = imageArry.get(position);
                Intent i = new Intent(getApplicationContext(),
                        QuotesActivity.class);
                i.putExtra("category", srr.getName());
                i.putExtra("mode", "isCategory");
                startActivity(i);
                showInterstitialAd();

            }
        });


        //Search Box
        searchedit = findViewById(R.id.searchedit);

        searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                imageArry.clear();

                List<Category> authors = db.getAllCategories(searchedit.getText());
                for (Category cn : authors) {

                    imageArry.add(cn);
                }
                dataList.setAdapter(adapter);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //Drawer Menu Click Listner
        ll_liked_quotes= findViewById(R.id.ll_liked_quotes);
        ll_todays_quote= findViewById(R.id.ll_todays_quote);
        llMaker= findViewById(R.id.ll_quote_maker);
        ll_sounds= findViewById(R.id.ll_setting);
        ll_about= findViewById(R.id.ll_about);
        ll_contact_us=  findViewById(R.id.ll_contact_us);
        ll_rate_app= findViewById(R.id.ll_rate_app);
        ll_share_app=  findViewById(R.id.ll_share_app);
        ll_privacy_policy =  findViewById(R.id.ll_privacy_policy);

        //show your favorite quotes
        ll_liked_quotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent favorites = new Intent(MainActivity.this,
                        QuotesActivity.class);
                favorites.putExtra("mode", "isFavorite");
                startActivity(favorites);
                showInterstitialAd();

            }
        });

        //quote of the day
        ll_todays_quote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                preferences = PreferenceManager
                        .getDefaultSharedPreferences(context);

                Intent qteDay = new Intent(MainActivity.this,
                        QuoteActivity.class);
                qteDay.putExtra("id",
                        preferences.getInt("id", IntialQteOfDayId));
                qteDay.putExtra("mode", "qteday");
                startActivity(qteDay);
                showInterstitialAd();
            }
        });

        //show your favorite quotes
        llMaker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent favorites = new Intent(context,
                        MakerActivity.class);
                favorites.putExtra("quote", "");
                context.startActivity(favorites);
                showInterstitialAd();

            }
        });

        //setting tab
        ll_sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                showInterstitialAd();
            }
        });

        //about my app
        ll_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        //Contact us
        ll_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{Config.contactUsEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, Config.emailSubject);
                intent.putExtra(Intent.EXTRA_TEXT, Config.emailBodyText);
                try {
                    startActivity(Intent.createChooser(intent, "Send mail"));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //rate my app
        ll_rate_app.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showRateDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        //share app on social media
        ll_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
                startActivity(Intent.createChooser(intent,"share via"));
            }
        });

        //read our privacy policy
        ll_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                showInterstitialAd();
            }
        });

    }

    private void showRateDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_rating_app);

        TextView btnLater = dialog.findViewById(R.id.btnLater);
        TextView btnMore= dialog.findViewById(R.id.btnMore);
        TextView btnRate = dialog.findViewById(R.id.btnRate);
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String package_name = BuildConfig.APPLICATION_ID;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + package_name)));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showPermissionDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_permission);

        TextView btnLater = dialog.findViewById(R.id.btnLater);
        TextView btnAllow = dialog.findViewById(R.id.btnAllow);
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyPermissions().booleanValue()) {
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public Boolean verifyPermissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        return false;
    }


    public void showInterstitialAd() {
        adNetwork.showInterstitialAdNetwork(INTERSTITIAL_POST_LIST, adsPref.getInterstitialAdInterval());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_prime) {
            showDialog_pay();
        }

        return super.onOptionsItemSelected(item);

    }

    public void showDialog_pay() {
        @SuppressLint("WrongConstant") View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.dialog_subscribe, (ViewGroup) null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(R.color.white);
        TextView text_view_go_pro = (TextView) bottomSheetDialog.findViewById(R.id.text_view_go_pro);
        ((RelativeLayout) bottomSheetDialog.findViewById(R.id.relativeLayout_close_rate_gialog)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(context, "Disable in Demo App", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override

            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i != 4) {
                    return true;
                }
                bottomSheetDialog.dismiss();
                return true;
            }
        });
        bottomSheetDialog.show();
        DialogOpened = true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_exit_app);

        TextView btnNo = dialog.findViewById(R.id.btnNo);
        TextView btnExit = dialog.findViewById(R.id.btnExit);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showAboutDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_about_app);

        TextView btnMore = dialog.findViewById(R.id.btnMore);
        TextView btnClose = dialog.findViewById(R.id.btnClose);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
                dialog.dismiss();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}