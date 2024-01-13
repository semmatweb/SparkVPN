package com.bycomsolutions.bycomvpn.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BillConfig {

    private static final String PREF_NAME = "snow-intro-slider";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    public static final String INAPPSKUUNIT = "inappskuunit";
    public static final String PURCHASETIME = "purchasetime";
    public static final String PRIMIUM_STATE = "primium_state";//boolean

    public static final String COUNTRY_DATA = "Country_data";
    public static final String BUNDLE = "Bundle";
    public static final String SELECTED_COUNTRY = "selected_country";

    public static final String IN_PURCHASE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs1+MhNbvRXPufL3+tUYcAXUErJoZuce7mCQ7326cGKNZ9UtlB47DcLB0sBW6z+fqXd10eVP3U9MXyrI1qb5X8jevQ+WfDaZ7oyQH21AgagYLaQ4QsygsZ9P/DGTKudlZSNhFfN9hueTv0DMsK0GmGCq03uRo/ZZaBheKY0v4Op4yZ0VKiFHMsvff2fcgQHEBYrZ8eEA9GP2cV1qKdXg6asC68Uv9DXY5S/LjDMiBOZ7IYqAuWsktZUuuSQfWUiJP2qj//z2orxDt6rviPikp3+OlSwCV+2Vvnpft2WOCiXZJ0niD6h5GG4Qfy/SRyBFMZxBLExwgp3hDiHoT6PIufwIDAQAB";
    public static final String One_Month_Sub = "oll_feature_for_onemonth";
    public static final String Six_Month_Sub = "oll_feature_for_sixmonth";
    public static final String One_Year_Sub = "oll_feature_for_year";


    public BillConfig(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

}