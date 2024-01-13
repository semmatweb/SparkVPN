package com.bycomsolutions.bycomvpn.activities;

import static com.bycomsolutions.bycomvpn.utils.BillConfig.INAPPSKUUNIT;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.IN_PURCHASE_KEY;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.One_Month_Sub;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.One_Year_Sub;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.PRIMIUM_STATE;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.PURCHASETIME;
import static com.bycomsolutions.bycomvpn.utils.BillConfig.Six_Month_Sub;
import static com.bycomsolutions.bycomvpn.utils.LicenseUtils.verifyLicense;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aemerse.iap.DataWrappers;
import com.aemerse.iap.IapConnector;
import com.aemerse.iap.SubscriptionServiceListener;
import com.bycomsolutions.bycomvpn.Preference;
import com.bycomsolutions.bycomvpn.R;

import java.util.ArrayList;
import java.util.Map;


public class GetPremiumActivity extends AppCompatActivity {

    public String SKU_DELAROY_MONTHLY;
    public String SKU_DELAROY_SIXMONTH;
    public String SKU_DELAROY_YEARLY;
    public String base64EncodedPublicKey;
    LinearLayout one_month, six_months, twelve_months;
    TextView one_month_sub_cost, six_months_sub_cost, one_year_sub_cost;

    boolean mSubscribedToDelaroy = false;
    String mDelaroySku = "";
    boolean mAutoRenewEnabled = false;
    private Preference preference;


    ImageView backToActivity;
    TextView activity_name;

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_premium);
        activity_name = findViewById(R.id.activity_name);
        backToActivity = findViewById(R.id.finish_activity);

        activity_name.setText("Get Premium");
        backToActivity.setOnClickListener(view -> finish());

        preference = new Preference(GetPremiumActivity.this);
        one_month = findViewById(R.id.one_month_layout);
        six_months = findViewById(R.id.six_months_layout);
        twelve_months = findViewById(R.id.twelve_months_layout);
        one_month_sub_cost = findViewById(R.id.one_month_sub_cost);
        six_months_sub_cost = findViewById(R.id.six_months_sub_cost);
        one_year_sub_cost = findViewById(R.id.one_year_sub_cost);


        base64EncodedPublicKey = preference.getStringpreference(IN_PURCHASE_KEY, base64EncodedPublicKey);
        SKU_DELAROY_MONTHLY = preference.getStringpreference(One_Month_Sub, SKU_DELAROY_MONTHLY);
        SKU_DELAROY_SIXMONTH = preference.getStringpreference(Six_Month_Sub, SKU_DELAROY_SIXMONTH);
        SKU_DELAROY_YEARLY = preference.getStringpreference(One_Year_Sub, SKU_DELAROY_YEARLY);
        verifyLicense(this);
        ArrayList<String> nonConsumableKeys = new ArrayList<>();

        ArrayList<String> consumableKeys = new ArrayList<>();

        ArrayList<String> subscriptionKeys = new ArrayList<>();
        subscriptionKeys.add(SKU_DELAROY_MONTHLY);
        subscriptionKeys.add(SKU_DELAROY_SIXMONTH);
        subscriptionKeys.add(SKU_DELAROY_YEARLY);

        IapConnector iapConnector = new IapConnector(
                this,
                nonConsumableKeys,
                consumableKeys,
                subscriptionKeys,
                base64EncodedPublicKey,
                true
        );

        unlockData();
        iapConnector.addSubscriptionListener(new SubscriptionServiceListener() {
            @Override
            public void onSubscriptionRestored(@NonNull DataWrappers.PurchaseInfo purchaseInfo) {
                Log.e("Subscribe", "yes" + purchaseInfo.getSku());
                if (purchaseInfo.getSku().equals(SKU_DELAROY_MONTHLY) && purchaseInfo.isAutoRenewing()) {
                    mDelaroySku = SKU_DELAROY_MONTHLY;
                    mAutoRenewEnabled = true;
                    mSubscribedToDelaroy = true;
                } else if (purchaseInfo.getSku().equals(SKU_DELAROY_SIXMONTH) && purchaseInfo.isAutoRenewing()) {
                    mDelaroySku = SKU_DELAROY_SIXMONTH;
                    mAutoRenewEnabled = true;
                    mSubscribedToDelaroy = true;
                } else if (purchaseInfo.getSku().equals(SKU_DELAROY_YEARLY) && purchaseInfo.isAutoRenewing()) {
                    mDelaroySku = SKU_DELAROY_YEARLY;
                    mAutoRenewEnabled = true;
                    mSubscribedToDelaroy = true;
                } else {
                    mDelaroySku = "";
                    mAutoRenewEnabled = false;
                    mSubscribedToDelaroy = false;
                }

                if (!mDelaroySku.equals("")) {
                    preference.setStringpreference(INAPPSKUUNIT, mDelaroySku);
                    preference.setLongpreference(PURCHASETIME, purchaseInfo.getPurchaseTime());
                }
                unlockData();

            }

            @Override
            public void onSubscriptionPurchased(@NonNull DataWrappers.PurchaseInfo purchaseInfo) {


                if (purchaseInfo.getSku().equals(SKU_DELAROY_MONTHLY)
                        || purchaseInfo.getSku().equals(SKU_DELAROY_SIXMONTH)
                        || purchaseInfo.getSku().equals(SKU_DELAROY_YEARLY)) {
                    preference.setStringpreference(INAPPSKUUNIT, purchaseInfo.getSku());
                    preference.setLongpreference(PURCHASETIME, purchaseInfo.getPurchaseTime());
                    unlock();
                    alert();
                    mSubscribedToDelaroy = true;
                    mAutoRenewEnabled = purchaseInfo.isAutoRenewing();
                    mDelaroySku = purchaseInfo.getSku();

                }

            }

            @Override
            public void onPricesUpdated(@NonNull Map<String, DataWrappers.SkuDetails> map) {

            }
        });

        one_month.setOnClickListener(view -> iapConnector.subscribe(GetPremiumActivity.this, SKU_DELAROY_MONTHLY));
        six_months.setOnClickListener(view -> iapConnector.subscribe(GetPremiumActivity.this, SKU_DELAROY_SIXMONTH));
        twelve_months.setOnClickListener(view -> iapConnector.subscribe(GetPremiumActivity.this, SKU_DELAROY_YEARLY));

    }



    void alert() {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage("Thank you for subscribing!");
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    private void unlockData() {
        if (mSubscribedToDelaroy) {
            unlock();
        } else {
            preference.setBooleanpreference(PRIMIUM_STATE, false);
        }
    }

    public void unlock() {
        preference.setBooleanpreference(PRIMIUM_STATE, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
