package com.example.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag

class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNfcAdapter()
    }

    override fun onResume() {
        super.onResume()

        enableNfcForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        resolveIntent(intent)
    }

    override fun onPause() {
        super.onPause()

        disableNfcForegroundDispatch()
    }

    private fun initNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun enableNfcForegroundDispatch() {
        val nfcAdapterRefCopy = nfcAdapter
        val intent = Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        if (nfcAdapterRefCopy != null) {
            if (!nfcAdapterRefCopy.isEnabled()) {
                showNFCSettings()
            }
            nfcAdapterRefCopy.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    private fun showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC!", Toast.LENGTH_SHORT).show()
        val intent = Intent(ACTION_NFC_SETTINGS)
        startActivity(intent)
    }

    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (e: IllegalStateException) {
            // Log.e(getTag(), "Error disabling NFC foreground dispatch", e)
        }
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
            || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                // Toast.makeText(this, "rawMsgs is not null", Toast.LENGTH_SHORT).show()
            } else {
                // Toast.makeText(this, "rawMsgs is null", Toast.LENGTH_SHORT).show()
                // 不知為何，讀卡後會進到這個地方⋯⋯
                
            }
        }
    }


}