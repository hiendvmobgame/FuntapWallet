package com.funtap.wallet.handler

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.funtap.wallet.util.Constant

/**
 * Created by hiendv on 14/06/18.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintHandler// Constructor
(private val context: Context) : FingerprintManager.AuthenticationCallback() {

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        val cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        this.update("Fingerprint Authentication error\n$errString")
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        this.update("Fingerprint Authentication help\n$helpString")
    }

    override fun onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.")
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        //        ((Activity) context).finish();
        //        Intent intent = new Intent(context, HomeActivity.class);
        //        context.startActivity(intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(Constant.VERIFIED))
    }

    private fun update(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
    }

}
