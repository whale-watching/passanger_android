package com.general.files;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fragments.MyWalletFragment;
import com.solicity.user.R;
import com.utils.Logger;
import com.utils.Utils;
import com.view.anim.loader.AVLoadingIndicatorView;

//import ru.noties.scrollable.CanScrollVerticallyDelegate;
//import ru.noties.scrollable.ScrollableLayout;

public class IssueWebDialog extends AppCompatActivity {
    private Context mContext;

    /* public SafetyDialog(Context mContext) {
         this.mContext = mContext;
     }


 */
    AVLoadingIndicatorView loaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailog_webview_issue);

        ImageView backArrowImgView = (ImageView) findViewById(R.id.backArrowImgView);


        backArrowImgView.setOnClickListener(v -> finish());

        WebView mWebView = (WebView) findViewById(R.id.webView);
         loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        String url = getIntent().getStringExtra("URL");
        mWebView.setWebViewClient(new myWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setFocusable(true);
        mWebView.setVisibility(View.VISIBLE);
        loaderView.setVisibility(View.VISIBLE);


        Logger.d("WEBVIEWURL", "::" + url + "&fromapp=yes");


    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    public void open(String url, String imageURL) {
        final Dialog addAddressDailog = new Dialog(mContext);
        View contentView = View.inflate(mContext, R.layout.dailog_safety_measure, null);

        addAddressDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(ViewGroup.LayoutParams.MATCH_PARENT, mContext)));
        addAddressDailog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addAddressDailog.setCancelable(true);
        //a   BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        int screenHeight = ((int) Utils.getScreenPixelHeight(mContext));
        int peekHeight = 0;
        int bottomMarginForLoader = Utils.dpToPx(50, mContext);

        //    mBehavior.setPeekHeight(peekHeight);

//        View bottomSheetView = addAddressDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

//        Utils.getScreenPixelHeight(mContext)


        ImageView backArrowImgView = (ImageView) addAddressDailog.findViewById(R.id.backArrowImgView);

        backArrowImgView.setOnClickListener(v -> addAddressDailog.dismiss());
        RelativeLayout container = (RelativeLayout) addAddressDailog.findViewById(R.id.container);
        WebView mWebView = (WebView) addAddressDailog.findViewById(R.id.webView);
        AVLoadingIndicatorView loaderView = (AVLoadingIndicatorView) addAddressDailog.findViewById(R.id.loaderView);

        RelativeLayout.LayoutParams loaderView_ly_params = (RelativeLayout.LayoutParams) loaderView.getLayoutParams();
        loaderView_ly_params.bottomMargin = screenHeight - peekHeight + bottomMarginForLoader;
        loaderView.setLayoutParams(loaderView_ly_params);

        mWebView.setWebViewClient(new myWebClient());
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress > 90) {
//                    loaderView.setVisibility(View.GONE);
//                }
//            }
//        });

        mWebView.getSettings().setJavaScriptEnabled(true);

        // mWebView.setOnTouchListener((v, event) -> true);

        mWebView.loadUrl(url + "&fromapp=yes");
        Logger.d("WebViewUrl", "::" + url + "&fromapp=yes");
//        clearCookies(mContext);

        loaderView.setVisibility(View.VISIBLE);

        addAddressDailog.show();
    }

    /*public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("Api", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("Api", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }*/

    public class myWebClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // loaderView.setVisibility(View.VISIBLE);
        }


        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Logger.d("Test", "::" + errorCode + "::" + description);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Logger.d("Test", "::" + url);

            loaderView.setVisibility(View.GONE);
        }
    }
}
