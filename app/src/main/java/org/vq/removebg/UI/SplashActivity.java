package org.vq.removebg.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

import org.vq.removebg.R;
import org.vq.removebg.helper.PermissionHelper;

public class SplashActivity extends AppCompatActivity {

    PermissionHelper permissionHelper;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                instantiatePrivacyPolicy();
            }
        }, 1000);

        permissionHelper = new PermissionHelper(this);
        checkAndRequestPermission();
    }

    private void instantiatePrivacyPolicy() {
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                "https://localhost/terms",
                "https://localhost/privacy");

        dialog.addPoliceLine("This application uses a unique user identifier for advertising purposes, it is shared with third-party companies.");
        dialog.addPoliceLine("This application sends error reports, installation and send it to a server of the Fabric.io company to analyze and process it.");
        dialog.addPoliceLine("This application requires internet access and must collect the following information: Installed applications and history of installed applications, ip address, unique installation id, token to send notifications, version of the application, time zone and information about the language of the device.");
        dialog.addPoliceLine("All details about the use of data are available in our Privacy Policies, as well as all Terms of Service links below.");

        final Intent intent = new Intent(this, MainActivity.class);

        dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
            @Override
            public void onAccept(boolean isFirstTime) {
                Log.e("MainActivity", "Policies accepted");
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.e("MainActivity", "Policies not accepted");
                finish();
            }
        });

        //  Title
        dialog.setTitle("Terms of Service");

        dialog.show();
    }

    private boolean checkAndRequestPermission() {
        permissionHelper.permissionListener(new PermissionHelper.PermissionListener(){
            @Override
            public void onPermissionCheckDone(){
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        permissionHelper.checkAndRequestPermissions();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);;
        permissionHelper.onRequestCallBack(requestCode, permissions, grantResult);
    }
}
