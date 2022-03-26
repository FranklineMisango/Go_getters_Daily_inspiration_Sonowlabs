package com.sonowlabs.GogettersDailyInspiration.Utils;

import android.content.Context;

import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;


public final class AnalyticsTrackers {

    public enum Target {
        APP,

    }

    private static AnalyticsTrackers sInstance;

    public static synchronized void initialize(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Extra call to initialize analytics trackers");
        }

        sInstance = new AnalyticsTrackers(context);
    }

    public static synchronized AnalyticsTrackers getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call initialize() before getInstance()");
        }

        return sInstance;
    }

    private final Map<Target, Tracker> mTrackers = new HashMap<Target, Tracker>();
    private final Context mContext;


    private AnalyticsTrackers(Context context) {
        mContext = context.getApplicationContext();
    }

    public synchronized Tracker get(Target target) {
        if (!mTrackers.containsKey(target)) {
            Tracker tracker;
            switch (target) {
                case APP:
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled analytics target " + target);
            }
        }

        return mTrackers.get(target);
    }
}