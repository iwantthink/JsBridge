package com.github.lzyzsd.jsbridge.example;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class SeconedActivity extends AppCompatActivity implements View.OnClickListener {

    WebView mWebView;
    Button mBtnTest1, mBtnTest2, mBtnTest3, mBtnTest4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seconed);
        mWebView = (WebView) findViewById(R.id.wv_test);
        mBtnTest1 = (Button) findViewById(R.id.btn_test1);
        mBtnTest2 = (Button) findViewById(R.id.btn_test2);
        mBtnTest3 = (Button) findViewById(R.id.btn_test3);
        mBtnTest4 = (Button) findViewById(R.id.btn_test4);

        mBtnTest1.setOnClickListener(this);
        mBtnTest2.setOnClickListener(this);
        mBtnTest3.setOnClickListener(this);
        mBtnTest4.setOnClickListener(this);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String str) {
                Log.d("SeconedActivity", "shouldOverrideUrlLoading");
                // 步骤2：根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
                Uri uri = Uri.parse(str);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {

                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        //  步骤3：
                        // 执行JS所需要调用的逻辑
                        Toast.makeText(SeconedActivity.this, "js调用了Android的方法", Toast.LENGTH_SHORT).show();
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();

                    }

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, str);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.d("SeconedActivity", "onJsPrompt");
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void hello() {
                Toast.makeText(SeconedActivity.this, "i am called by js", Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void hello2(String msg) {
                Toast.makeText(SeconedActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void hello3() {
                mWebView.loadUrl("javascript:functionName()");
            }

        }, "test_name");


    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test1:
                mWebView.loadUrl("file:///android_asset/sample.html");
                break;
            case R.id.btn_test2:
                //无参数 无返回
                mWebView.loadUrl("javascript:java2Js1()");
                break;
            case R.id.btn_test3:
                //有参数 无返回
                mWebView.loadUrl("javascript:java2Js2(\"from native\")");
                break;
            case R.id.btn_test4:
                mWebView.evaluateJavascript("javascript:java2Js3()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Toast.makeText(SeconedActivity.this, value, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
