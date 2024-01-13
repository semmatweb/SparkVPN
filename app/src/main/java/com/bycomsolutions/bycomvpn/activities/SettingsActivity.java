package com.bycomsolutions.bycomvpn.activities;

import static com.bycomsolutions.bycomvpn.utils.LicenseUtils.verifyLicense;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bycomsolutions.bycomvpn.R;


public class SettingsActivity extends AppCompatActivity {

    LinearLayout ll_share, ll_more, ll_privacy, ll_terms, ll_faq, ll_about;
    ImageView backToActivity;
    TextView activity_name;

    RelativeLayout rl_speed_test,rl_bypass_apps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        verifyLicense(this);
        activity_name = findViewById(R.id.activity_name);
        backToActivity = findViewById(R.id.finish_activity);
        ll_share = findViewById(R.id.ll_share);
        ll_more = findViewById(R.id.ll_more);
        ll_privacy = findViewById(R.id.ll_privacy);
        ll_terms = findViewById(R.id.ll_terms);
        ll_faq = findViewById(R.id.ll_faq);
        ll_about = findViewById(R.id.ll_about);
        rl_speed_test = findViewById(R.id.rl_speed_test);
        rl_bypass_apps = findViewById(R.id.rl_bypass_apps);

        rl_speed_test.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this,SpeedTestActivity.class)));

        rl_bypass_apps.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, BypassAppsActivity.class)));

        activity_name.setText(R.string.settings);
        backToActivity.setOnClickListener(view -> finish());

        ll_share.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String sAux = getResources().getString(R.string.app_share_description) + "\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(intent, getString(R.string.share_app)));
        });

        ll_more.setOnClickListener(v -> {
            String Url = getString(R.string.play_store_developer_link);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
            startActivity(intent);
        });

        ll_privacy.setOnClickListener(view -> openWebView(getResources().getString(R.string.privacy_policy_link)));

        ll_terms.setOnClickListener(view -> openWebView(getResources().getString(R.string.terms_link)));

        ll_faq.setOnClickListener(view -> openWebView(getResources().getString(R.string.faq_link)));

        ll_about.setOnClickListener(view -> openWebView(getResources().getString(R.string.about_link)));
    }


    public void openWebView(String url){
        Intent intent = new Intent(this,WebViewActivity.class);
        intent.putExtra("URL",url);
        startActivity(intent);
    }


}

