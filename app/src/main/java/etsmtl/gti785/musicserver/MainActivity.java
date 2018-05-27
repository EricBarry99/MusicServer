package etsmtl.gti785.musicserver;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IP";
    private static MyServer server;
    TextView txtIpAddress;
    StreamController musicController;
    StreamService musicService;
    OkHttpClient client = new OkHttpClient();

    /*
    Volley + OKhttp est recommandé
    Utiliser okHttp dans asynctask
    // http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicController = new StreamController();

        try {
            server = new MyServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.txtIpAddress = (TextView)this.findViewById(R.id.textView_ip);
            server.start();
            this.txtIpAddress.setText(getLocalIpAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }


  //      server.stop();
    }

    public String getLocalIpAddress() {

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }

}
