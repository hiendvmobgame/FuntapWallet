package com.funtap.wallet.activity;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.funtap.wallet.R;
import com.funtap.wallet.fragment.FingerPrintVerifyFragment;
import com.funtap.wallet.util.Constant;

public class CheckAuthActivity extends AppCompatActivity implements FingerPrintVerifyFragment.OnFragmentInteractionListener {

    private static final String TAG = "CheckAuthActivity";
    EditText edt_passowrd;
    Button btn_login_with_fingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_passowrd = findViewById(R.id.edt_password);
        btn_login_with_fingerprint = findViewById(R.id.btn_login_with_fingerprint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyWithFingerPrint();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verifyWithFingerPrint() {
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        // Check whether the device has a Fingerprint sensor.
        assert fingerprintManager != null;
        if (!fingerprintManager.isHardwareDetected()) {
            Log.d(TAG, "Your Device does not have a Fingerprint Sensor");
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Fingerprint authentication permission not enabled");
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(CheckAuthActivity.this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
                } else {
                    // Checks whether lock screen security is enabled or not
                    assert keyguardManager != null;
                    if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(CheckAuthActivity.this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                    } else {
                        showFragmentVerifyFingerPrint();
                        btn_login_with_fingerprint.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void showFragmentVerifyFingerPrint() {
        try {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            DialogFragment dialogFragment = new FingerPrintVerifyFragment();
            dialogFragment.show(ft, getString(R.string.dialog_tag));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_with_fingerprint:
                showFragmentVerifyFingerPrint();
                break;
        }
    }


    public void onNumberPadClick(View view) {
        try {
            String text = ((Button) view).getText().toString();
            edt_passowrd.setText(String.format("%s%s", edt_passowrd.getText().toString(), text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constant.VERIFIED));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.VERIFIED)) {
                Intent intent1 = new Intent(CheckAuthActivity.this, HomeActivity.class);
                startActivity(intent1);
                finish();
            }
        }
    };
}
