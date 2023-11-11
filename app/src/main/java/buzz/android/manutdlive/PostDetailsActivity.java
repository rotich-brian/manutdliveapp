package buzz.android.manutdlive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class PostDetailsActivity extends AppCompatActivity {

    private ModelPost modelPost;
    ImageButton backBtn;
    //LinearLayout backBtn;
    WebView webView;
    SwipeRefreshLayout swipeRefresh;
    String url;

    private  static  final  String TAG = "POST_DETAILS_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar1);
        setContentView(R.layout.activity_post_details);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        modelPost = (ModelPost) getIntent().getSerializableExtra("data");
        url = modelPost.getUrl();

        //actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        //swipeRefresh.setRefreshing(true);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipeRefresh.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                final String url1 = request.getUrl().toString();
                if (url1.contains("itrend.buzz")) {
                    webView.loadUrl(url1);
                }else {

                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    view.getContext().startActivity(intent);
                    //return super.shouldOverrideUrlLoading(view, request);
                }
                    return true;
            }
        });

        webView.loadUrl(url);
        refreshPage();

    }

    private void refreshPage() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}