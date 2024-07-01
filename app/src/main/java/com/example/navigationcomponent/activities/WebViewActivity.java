package com.example.navigationcomponent.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.navigationcomponent.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {
    ActivityWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String urlTVShow = getIntent().getStringExtra("urlTVShow");
        String nameTVShow = getIntent().getStringExtra("nameTVShow");

        setUpWebView();
        clickWebView(urlTVShow);

        binding.imageBack.setOnClickListener(view -> {
            finish();
            binding.webView.destroy();
        });
        binding.NameTvShow.setText(nameTVShow);
    }
    private void setUpWebView(){
        WebSettings settings = binding.webView.getSettings();

        settings.setJavaScriptEnabled(true);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        binding.webView.getSettings().setBuiltInZoomControls(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
    }

    private void clickWebView(String urlTVShow){
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(urlTVShow);
                return true;
            }
        });
        binding.webView.loadUrl(urlTVShow);
    }
}