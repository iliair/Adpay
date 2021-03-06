package com.example.videostatus.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.videostatus.R;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;



public class Faq extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private TextView textView;
    private LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Method.forceRTLIfSupported(getWindow(), Faq.this);

        method=new Method(Faq.this);

        toolbar = findViewById(R.id.toolbar_faq);
        toolbar.setTitle(getResources().getString(R.string.faq));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = findViewById(R.id.linearLayout_faq);
        WebView webView = findViewById(R.id.webView_faq);
        textView = findViewById(R.id.textView_noData_found_faq);
        textView.setVisibility(View.GONE);

        if (Constant_Api.aboutUsList != null) {

            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.setFocusableInTouchMode(false);
            webView.setFocusable(false);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            String mimeType = "text/html";
            String encoding = "utf-8";
            String htmlText = Constant_Api.aboutUsList.getApp_faq();

            String text = "<html><head>"
                    + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/IRANSansMobile_Medium.ttf\")}body{font-family: MyFont;text-align:justify; direction:rtl;line-height:1.6}"
                    + "</style></head>"
                    + "<body>"
                    + htmlText
                    + "</body></html>";

            webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        } else {
            textView.setVisibility(View.VISIBLE);
        }

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout, Faq.this);
        } else {
            method.showNonPersonalizedAds(linearLayout, Faq.this);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
