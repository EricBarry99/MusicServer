package etsmtl.gti785.musicserver;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IP";
    public static final int PORT = 8765;

    private static MyServer server;
    TextView txtIpAddress;
    TextView txtPortNumber;
    OkHttpClient client = new OkHttpClient();
    ArrayList<String> playList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            server = new MyServer(PORT,this);
            this.txtIpAddress = (TextView)this.findViewById(R.id.textView_ip);
            this.txtPortNumber = (TextView)this.findViewById(R.id.textView_port);
            this.playList = listElemRaw();

            server.start();
            this.txtIpAddress.setText(getLocalIpAddress());
//            this.txtPortNumber.setText(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getPlayList() {
        return playList;
    }

    public ArrayList<String> listElemRaw(){
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            Log.d("Raw Asset: ", fields[count].getName());
            String field = fields[count].getName();
            playList.add(field);
        }
        return playList;
    }

    public String getLocalIpAddress() {

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }
}
