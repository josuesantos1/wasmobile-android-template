package com.josue.android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private WebView wasmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wasmView = (WebView) findViewById(R.id.wasmview);

        WebSettings settings = wasmView.getSettings();
        settings.setJavaScriptEnabled(true);

        wasmView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                String path = request.getUrl().getLastPathSegment();

                try {
                    String mime;
                    AssetManager assetManager = getAssets();

                    if (path.endsWith(".html")) mime = "text/html";
                    else if (path.endsWith(".wasm")) mime = "application/wasm";
                    else if (path.endsWith(".js")) mime = "text/javascript";
                    else
                        return super.shouldInterceptRequest(view, request);

                    InputStream input = assetManager.open("ui/" + path);

                    return new WebResourceResponse(mime, "utf-8", input);
                } catch (IOException e) {
                    e.printStackTrace();
                    ByteArrayInputStream result = new ByteArrayInputStream
                            (("X:" + path + " E:" + e.toString()).getBytes());
                    return new WebResourceResponse("text/plain", "utf-8", result);
                }
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }

        });
        // Assets are hosted under http(s)://appassets.androidplatform.net/... .
        // If the application's assets are in the "main/assets" folder this will read the file
        // from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/index.html
        wasmView.loadUrl("https://appassets.androidplatform.net/index.html");
    }

}


