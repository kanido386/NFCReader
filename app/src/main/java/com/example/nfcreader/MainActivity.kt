package com.example.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.widget.Toast

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
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
}