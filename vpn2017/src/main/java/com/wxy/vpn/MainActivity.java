package com.wxy.vpn;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

//import com.meiqia.core.MQManager;
//import com.wxy.contact.util.MQConfig;
//import com.wxy.contact.util.MQIntentBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.VpnStatus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isFirst;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
//        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.fab_options).setOnClickListener(this);
        findViewById(R.id.btn_circle).setOnClickListener(this);

        if (!"com.android.vending".equals(getPackageManager().getInstallerPackageName(getPackageName()))) {
            try {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("market://details?id=com.wxy.vpn2017"));
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            } catch (ActivityNotFoundException ignore) {
            }
        }
        if (!"com.wxy.vpn2017".equals(getPackageName())) {
            throw new RuntimeException();
        }
        sharedPreferences = getSharedPreferences("vpn_2017", MODE_PRIVATE);
        isFirst = sharedPreferences.getBoolean("is_first", true);
        VpnStatus.initLogCache(getApplicationContext().getCacheDir());
    }

//    private void readNewAC() {
//        try {
//            File file = new File(getExternalFilesDir(null).getAbsolutePath(), "client.bin");// /storage/sdcard0/Android/data/com.wxy.vpn2017/files
//            if (file.exists()) {
//                try {
//                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//                    String line;
//                    String config = "";
//                    while ((line = bufferedReader.readLine()) != null) {
//                        config += line + "\n";
//                    }
//                    OpenVpnApi.startVpn(this, config, null, null);
//                } catch (IOException | RemoteException e) {
//                    Toast.makeText(this, "证书文件解析错误", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(this, "新证书文件不存在,请检查路径和文件名", Toast.LENGTH_LONG).show();
//            }
//        } catch (NullPointerException ignore) {
//            Toast.makeText(this, "似乎没有存储，还是使用默认证书吧~", Toast.LENGTH_LONG).show();
//        }
//    }

    private void startVpn() {
        String config = "";
        try {
            InputStream conf = getAssets().open("client.ovpn");// TODO replace your own authentication file in /assets/client.bin
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                config += line + "\n";
            }
        } catch (IOException ignore) {
                Toast.makeText(this, "发生错误,请联系开发者", Toast.LENGTH_LONG).show();
        }
        try {
            OpenVpnApi.startVpn(this, config, null, null);
        } catch (RemoteException e) {
            Toast.makeText(this, "默认证书文件解析错误", Toast.LENGTH_LONG).show();
        }
    }

    private MyBottomSheetDialog bottomSheetDialog;
//    private Button btnNewCA;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_circle:// 点击连接VPN
                if (isFirst) {
                    Snackbar.make(v, R.string.click_again, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.GONE);
                        }
                    }).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_first", false);
                    isFirst = false;
                    editor.apply();
                }
//                if (!sharedPreferences.getBoolean("is_ca_changed", false)) {// 证书没被改变
                    startVpn();
//                } else {// 证书已被更改 读取SD卡
//                    if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
//                    } else {// 读取SD卡文件
//                        readNewAC();
//                    }
//                }
                break;
//            case R.id.fab:
//                if (bottomSheetDialog == null) {
//                    bottomSheetDialog = new MyBottomSheetDialog(this);
//                }
//                bottomSheetDialog.setContentView(R.layout.bottom);
//                if (bottomSheetDialog != null) {
//                    Button btnContact = (Button) bottomSheetDialog.findViewById(R.id.btn_contact);
////                    btnNewCA = (Button) bottomSheetDialog.findViewById(R.id.btn_change_CA);
////                    String btnText = sharedPreferences.getString("btn_text", "更新证书");
////                    btnNewCA.setText(btnText);
//                    if (btnContact != null) btnContact.setOnClickListener(this);
////                    if (btnNewCA != null) btnNewCA.setOnClickListener(this);
//                }
//                bottomSheetDialog.show();
//                break;
//            case R.id.btn_contact:
//                initMeiqiaSDK();
//                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) bottomSheetDialog.dismiss();
//                Intent intent = new MQIntentBuilder(this).build();
//                startActivity(intent);
//                break;
//            case R.id.btn_change_CA:
//                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) bottomSheetDialog.dismiss();
//                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                } else {
//                    newCA();
//                }
//                break;
            case R.id.item_rate_app:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException ignore) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.item_more_apps:
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("market://search?q=pub:wangxingyu"));
                    startActivity(intent1);
                } catch (ActivityNotFoundException ignore) {
                    Toast.makeText(this, "Not found Google Play!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_contact:
//                initMeiqiaSDK();
//                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) bottomSheetDialog.dismiss();
//                Intent intent = new MQIntentBuilder(this).build();
//                startActivity(intent);
                break;
            case R.id.item_private_policy:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://rawgit.com/yuger/app_policy/master/VPN_2017_Policy.html")));
                } catch (android.content.ActivityNotFoundException ignore) {
                    Toast.makeText(this,/*R.string.no_browser*/"No browser found", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

//    private void initMeiqiaSDK() {
//        MQManager.setDebugMode(false);
//        MQConfig.init(this, "0939032e0135200dd550e21734eb58a9", null); // https://app.meiqia.com/setting/sdk
//        MQConfig.ui.titleGravity = MQConfig.ui.MQTitleGravity.LEFT;
//    }

//    @TargetApi(23)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    newCA();
//                } else {
//                    Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case 2:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    readNewAC();
//                } else {
//                    Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }

//    private void newCA() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        if (sharedPreferences.getBoolean("is_ca_changed", false)) {
//            editor.putBoolean("is_ca_changed", false);
//            if (btnNewCA != null) editor.putString("btn_text", "更新证书");
//            Toast.makeText(this, "证书已还原,继续使用默认证书", Toast.LENGTH_SHORT).show();
//        } else {
//            editor.putBoolean("is_ca_changed", true);
//            if (btnNewCA != null) editor.putString("btn_text", "还原证书");
//            Toast.makeText(this, "将使用新证书,请确保你有新证书！", Toast.LENGTH_LONG).show();
//        }
//        editor.apply();
//    }
}
