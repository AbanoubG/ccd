package org.ccd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import org.ccd.connection.RestAdapter;
import org.ccd.connection.callbacks.CallbackDevice;
import org.ccd.data.SharedPref;
import org.ccd.model.DeviceInfo;
import org.ccd.utils.PermissionUtil;
import org.ccd.utils.Tools;
import retrofit2.Call;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    private SharedPref sharedPref;
    private View parent_view;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        parent_view = findViewById(R.id.parent_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        sharedPref = new SharedPref(this);
        Tools.initImageLoader(getApplicationContext());
        parent_view.setBackgroundColor(sharedPref.getThemeColorInt());

        // permission checker for android M or higher
        if (Tools.needRequestPermission()) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            } else {
                initGcmData();
            }
        } else {
            initGcmData();
        }

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void startActivityMainDelay() {
        // Show splash screen for 2 seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
                startActivity(i);
                finish(); // kill current activity
            }
        };
        new Timer().schedule(task, 2000);
    }

    private void initGcmData() {
        if (sharedPref.isGcmRegIdEmpty() && Tools.cekConnection(this)) {
            prepareDeviceInfo();
        } else if (sharedPref.isOpenAppCounterReach() && Tools.cekConnection(this)) {
            registerDeviceToServer(Tools.getDeviceInfo(this));
        } else {
            startActivityMainDelay();
        }
    }

    private void prepareDeviceInfo() {
        progressBar.setVisibility(View.VISIBLE);
        Tools.obtainGcmRegId(this, new Tools.CallbackRegId() {
            @Override
            public void onSuccess(DeviceInfo result) {
                // start registration to server
                if (Tools.cekConnection(getApplicationContext())) {
                    registerDeviceToServer(result);
                } else {
                    startActivityMainDelay();
                }
            }

            @Override
            public void onError() {
                startActivityMainDelay();
            }
        });
    }

    private void registerDeviceToServer(DeviceInfo deviceInfo) {
        progressBar.setVisibility(View.VISIBLE);
        // register gcm
        Call<CallbackDevice> callback = RestAdapter.createShortAPI().registerDevice(deviceInfo);
        callback.enqueue(new retrofit2.Callback<CallbackDevice>() {
            @Override
            public void onResponse(Call<CallbackDevice> call, Response<CallbackDevice> response) {
                CallbackDevice resp = response.body();
                if (resp.status.equals("success")) {
                    sharedPref.setOpenAppCounter(0);
                }
                startActivityMainDelay();
            }

            @Override
            public void onFailure(Call<CallbackDevice> call, Throwable t) {
                startActivityMainDelay();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            for (String perm : permissions) {
                boolean rationale = shouldShowRequestPermissionRationale(perm);
                sharedPref.setNeverAskAgain(perm, !rationale);
            }
            if (PermissionUtil.isAccountGranted(this)) {
                initGcmData();
            } else {
                startActivityMainDelay();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
