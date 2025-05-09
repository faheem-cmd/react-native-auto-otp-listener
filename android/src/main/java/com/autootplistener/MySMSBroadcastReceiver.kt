package com.autootplistener

import android.content.*
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class MySMSBroadcastReceiver : BroadcastReceiver() {
    private var listener: Listener? = null

    fun initListener(listener: Listener) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
            if (status?.statusCode == CommonStatusCodes.SUCCESS) {
                val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                val otp = Regex("\\d{6,}").find(message ?: "")?.value
                Log.d("AutoOtpListener", "Extracted OTP: $otp")
                listener?.onOtpReceived(otp)
            } else {
                Log.e("AutoOtpListener", "SMS retrieval failed with status: $status")
            }
        }
    }

    interface Listener {
        fun onOtpReceived(value: String?)
    }
}
