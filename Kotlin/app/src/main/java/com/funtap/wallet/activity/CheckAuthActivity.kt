package com.funtap.wallet.activity

import android.Manifest
import android.app.DialogFragment
import android.app.Fragment
import android.app.FragmentTransaction
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.funtap.wallet.R
import com.funtap.wallet.fragment.FingerPrintVerifyFragment
import com.funtap.wallet.util.Constant

class CheckAuthActivity : AppCompatActivity(), FingerPrintVerifyFragment.OnFragmentInteractionListener {
    internal lateinit var edt_passowrd: EditText
    internal lateinit var btn_login_with_fingerprint: Button

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, inent: Intent) {
            if (intent.action == Constant.VERIFIED) {
                val intent1 = Intent(this@CheckAuthActivity, HomeActivity::class.java)
                startActivity(intent1)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edt_passowrd = findViewById(R.id.edt_password)
        btn_login_with_fingerprint = findViewById(R.id.btn_login_with_fingerprint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyWithFingerPrint()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun verifyWithFingerPrint() {
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        // Check whether the device has a Fingerprint sensor.
        assert(fingerprintManager != null)
        if (!fingerprintManager.isHardwareDetected) {
            Log.d(TAG, "Your Device does not have a Fingerprint Sensor")
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Fingerprint authentication permission not enabled")
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(this@CheckAuthActivity, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show()
                } else {
                    // Checks whether lock screen security is enabled or not
                    assert(keyguardManager != null)
                    if (!keyguardManager.isKeyguardSecure) {
                        Toast.makeText(this@CheckAuthActivity, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show()
                    } else {
                        showFragmentVerifyFingerPrint()
                        btn_login_with_fingerprint.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showFragmentVerifyFingerPrint() {
        try {
            val ft = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            val dialogFragment = FingerPrintVerifyFragment()
            dialogFragment.show(ft, getString(R.string.dialog_tag))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_login_with_fingerprint -> showFragmentVerifyFingerPrint()
        }
    }


    fun onNumberPadClick(view: View) {
        try {
            val text = (view as Button).text.toString()
            edt_passowrd.setText(String.format("%s%s", edt_passowrd.text.toString(), text))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(Constant.VERIFIED))
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()
    }

    companion object {

        private val TAG = "CheckAuthActivity"
    }
}
