package com.cadabra.sociallogin

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

abstract class GoogleSingInListener: LoginListener<String> {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GOOGLE_SING_IN_CODE) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val token = task?.getResult(ApiException::class.java)?.idToken
                if (token == null) {
                    onError(Exception("Token are null"))
                } else {
                    onSuccess(token)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}