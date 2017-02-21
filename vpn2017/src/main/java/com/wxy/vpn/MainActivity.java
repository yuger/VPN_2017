package com.wxy.vpn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.VpnStatus;

public class MainActivity extends AppCompatActivity {
    boolean isFirst;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = getSharedPreferences("is_first", MODE_PRIVATE);
        isFirst = sharedPreferences.getBoolean("is_first", true);
        VpnStatus.initLogCache(getApplicationContext().getCacheDir());
    }

    public void connectVPN(View view) {
        if (isFirst) {
            Toast.makeText(this, "首次使用请在授权后再次点击该按钮", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_first", false);
            editor.apply();
        }
        startVpn();
    }

    private void startVpn() {
        try {
            InputStream conf = getAssets().open("client.bin");// your own file in /assets/client.bin
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }
            br.readLine();
            OpenVpnApi.startVpn(this, config, null, null);
        } catch (IOException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
