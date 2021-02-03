package com.example.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import com.example.nfcreader.utils.Utils

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
                // 不知為何，讀卡後會進到這個地方⋯⋯（也許是因為它 immutable 吧？不確定）
                // val empty = ByteArray(0)
                // val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag
                // val payload = dumpTagData(tag).toByteArray()
                // val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                // val emptyMsg = NdefMessage(arrayOf(record))
                // val emptyNdefMessages: Array<NdefMessage> = arrayOf(emptyMsg)
                Toast.makeText(this, dumpTagData(tag), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Tag data is converted to string to display
    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.getId()

        sb.append("ID: ").append(Utils.toHex(id)).append('\n')
        sb.append("Have fun!")

        return sb.toString()
    }


}