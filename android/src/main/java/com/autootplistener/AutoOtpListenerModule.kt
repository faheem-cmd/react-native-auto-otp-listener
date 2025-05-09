package com.autootplistener

import android.content.*
import android.os.Build
import android.util.Log
import android.content.pm.PackageManager
import android.content.pm.SigningInfo
import android.util.Base64
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.security.MessageDigest

class AutoOtpListenerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private var receiver: MySMSBroadcastReceiver? = null
    private var isReceiverRegistered = false

    override fun getName(): String = "AutoOtpListener"

    private fun sendEvent(eventName: String, data: String?) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, data)
    }

    private fun registerReceiver(context: Context) {
        receiver = MySMSBroadcastReceiver().apply {
            initListener(object : MySMSBroadcastReceiver.Listener {
                override fun onOtpReceived(value: String?) {
                    sendEvent("onOTPReceived", value)
                    unregisterReceiver(context)
                    startSmsRetriever(context) // Auto-restart
                }
            })
        }

        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        isReceiverRegistered = true
    }

    private fun unregisterReceiver(context: Context) {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                Log.e("AutoOtpListener", "Unregister failed: ${e.message}")
            }
            isReceiverRegistered = false
            receiver = null
        }
    }

    private fun startSmsRetriever(context: Context) {
        SmsRetriever.getClient(context).startSmsRetriever()
            .addOnSuccessListener { registerReceiver(context) }
            .addOnFailureListener { sendEvent("onOTPError", "Failed to start SMS retriever") }
    }

    @ReactMethod
    fun startListeningForOTP() {
        startSmsRetriever(reactApplicationContext)
    }

    @ReactMethod
    fun stopListeningForOTP() {
        unregisterReceiver(reactApplicationContext)
    }

    // 🔥 NEW: Get app hash for SMS
    @ReactMethod
fun getAppHash(promise: Promise) {
    try {
        val packageName = reactApplicationContext.packageName
        val packageManager = reactApplicationContext.packageManager
        val packageInfo = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )

        val signatures: Array<android.content.pm.Signature>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

        if (signatures == null || signatures.isEmpty()) {
            promise.reject("NO_SIGNATURES", "No app signatures found")
            return
        }

        val appCodes = mutableListOf<String>()

        for (signature in signatures) {
            val appInfo = "$packageName ${signature.toCharsString()}"
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(appInfo.toByteArray())
            val hashSignature = messageDigest.digest()
            val truncatedHash = hashSignature.copyOfRange(0, 9)
            val base64Hash = Base64.encodeToString(truncatedHash, Base64.NO_PADDING or Base64.NO_WRAP)
            val hash = base64Hash.substring(0, 11)
            appCodes.add(hash)
        }

        if (appCodes.isNotEmpty()) {
            promise.resolve(appCodes[0]) // return first hash
        } else {
            promise.reject("NO_HASH", "No app hash generated")
        }

    } catch (e: Exception) {
        promise.reject("HASH_ERROR", "Failed to get app hash", e)
    }
}

}
