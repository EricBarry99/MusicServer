package etsmtl.gti785.musicserver;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IP";
    public static final int PORT = 8765;

    private static MyServer server;
    TextView txtIpAddress;
    TextView txtPortNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            server = new MyServer(PORT,this);
            this.txtIpAddress = (TextView)this.findViewById(R.id.textView_ip);
            this.txtPortNumber = (TextView)this.findViewById(R.id.textView_port);

            server.start();
            this.txtIpAddress.setText(getLocalIpAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocalIpAddress() {

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }
}
