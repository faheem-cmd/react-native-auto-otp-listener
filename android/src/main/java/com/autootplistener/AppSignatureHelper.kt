package com.autootplistener

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.SigningInfo
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

class AppSignatureHelper(private val context: Context) {
    private val TAG = AppSignatureHelper::class.java.simpleName

    fun getAppSignatures(): List<String> {
        val appCodes = mutableListOf<String>()
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )

            val signatures: Array<android.content.pm.Signature>? =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    val signingInfo: SigningInfo? = packageInfo.signingInfo
                    signingInfo?.apkContentsSigners
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.signatures
                }

            if (signatures == null || signatures.isEmpty()) {
                Log.e(TAG, "No signatures found")
                return appCodes
            }

            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(hash)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting app signatures", e)
        }
        return appCodes
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(appInfo.toByteArray())
            val hashSignature = messageDigest.digest()
            val truncatedHash = hashSignature.copyOfRange(0, 9)
            val base64Hash = Base64.encodeToString(truncatedHash, Base64.NO_PADDING or Base64.NO_WRAP)
            return base64Hash.substring(0, 11)
        } catch (e: Exception) {
            Log.e(TAG, "Hash generation failed", e)
        }
        return null
    }
}
