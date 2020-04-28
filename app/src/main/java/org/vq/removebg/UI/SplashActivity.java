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

        permissionHelper = new PermissionHelper(this);
        checkAndRequestPermission();
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
