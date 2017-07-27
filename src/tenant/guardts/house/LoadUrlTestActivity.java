package tenant.guardts.house;


import tenant.guardts.house.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadUrlTestActivity extends Activity {
    /** Called when the activity is first created. */
	WebView webView;
    ProgressBar progressBar;
    //String DEFINE_URL = "http://www.guardts.com/output/html5.html";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.load_url);
        String tabName = getIntent().getStringExtra("tab_name"); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText(tabName);
        webView = (WebView) findViewById(R.id.webView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        
        webView.getSettings().setJavaScriptEnabled(true);
        
        //webView.getSettings().setPluginState(PluginState.ON);
        webView.setWebChromeClient(new WebChromeClient());
        
        String url = getIntent().getStringExtra("url");  
        
//        webView.loadUrl(url);
        
//        webView.loadUrl("file:///android_asset/source/index.html");
        webView.loadUrl(url);
        
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	if( url.startsWith("http:") || url.startsWith("https:") ) {  
                    return false;  
                }  
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
                startActivity(intent);
    //  下面这一行保留的时候，原网页仍报错，新网页正常.所以注释掉后，也就没问题了
//              view.loadUrl(url); 
                return true; 
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
 
            public void onProgressChanged(WebView view, int progress) {
 
                setTitle("ҳ������У����Ժ�..." + progress + "%");
 
                setProgress(progress * 100);
 
                if (progress == 100) {
 
                    setTitle(R.string.app_name);
 
                    progressBar.setVisibility(4);
                }
 
            }
 
        });
 
    }
 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
}