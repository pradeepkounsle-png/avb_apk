package com.avb.attendance;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.json.JSONArray;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {
    WebView webView;
    private static final String SITE_URL = "https://avb-pu-smart-att-system.netlify.app";
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new SMSInterface(), "AndroidSMS");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        webView.loadUrl(SITE_URL);
    }
    public class SMSInterface {
        @JavascriptInterface
        public void sendBulkSMS(String jsonData) {
            try {
                JSONArray arr = new JSONArray(jsonData);
                SmsManager sms = SmsManager.getDefault();
                for (int i=0;i<arr.length();i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String phone = obj.getString("phone");
                    String msg = obj.getString("msg");
                    sms.sendTextMessage("+91"+phone, null, msg, null, null);
                }
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "✅ "+arr.length()+" SMS Sent!", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
    }
}