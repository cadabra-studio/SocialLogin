package com.cadabra.sociallogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.twitter.sdk.android.core.TwitterAuthToken
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_standard_view.*

class StandardViewRxActivity : AppCompatActivity() {
    private val disposableList = mutableListOf<Disposable>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_view)

        checkIsAuthorized()
        setListeners()
    }

    private fun checkIsAuthorized() {
        if (SocialLogin.isGoogleAuthorized(this))
            authorizeGoogle(SocialLogin.getGoogleToken(this))

        if (SocialLogin.isFacebookAuthorized())
            authorizeFacebook(SocialLogin.getFacebookToken())

        if (SocialLogin.isTwitterAuthorized())
            authorizeTwitter(SocialLogin.getTwitterAuthToken())
    }

    private fun setListeners() {
        val googleDisposable = SocialLogin.setRxGoogleListener(this, googleButton)
            .subscribe(::authorizeGoogle, ::failGoogle)
        disposableList.add(googleDisposable)

        val facebookDisposable = SocialLogin.setRxFacebookListener(facebookButton)
            .subscribe(::authorizeFacebook, ::failFacebook)
        disposableList.add(facebookDisposable)

        val twitterDisposable = SocialLogin.setRxTwitterListener(twitterButton)
            .subscribe(::authorizeTwitter, ::failTwitter)
        disposableList.add(twitterDisposable)
    }

    private fun failTwitter(it: Throwable) {
        Log.d(this.localClassName, it.message)
        twitterStatus.text = getString(R.string.fail)
        twitterToken.text = null
        twitterSecret.text = null
    }

    private fun authorizeTwitter(it: TwitterAuthToken) {
        Log.d(this.localClassName, "twitter token: ${it.token}, secret: ${it.secret}")
        twitterStatus.text = getString(R.string.success)
        twitterToken.text = it.token
        twitterSecret.text = it.secret
    }

    private fun failFacebook(it: Throwable) {
        Log.d(this.localClassName, it.message)
        facebookStatus.text = getString(R.string.fail)
        facebookToken.text = null
    }

    private fun authorizeFacebook(it: String) {
        Log.d(this.localClassName, "facebook token: $it")
        facebookStatus.text = getString(R.string.success)
        facebookToken.text = it
    }

    private fun failGoogle(it: Throwable) {
        Log.d(this.localClassName, it.message)
        googleStatus.text = getString(R.string.fail)
        googleToken.text = null
    }

    private fun authorizeGoogle(it: String) {
        Log.d(this.localClassName, "google token: $it")
        googleStatus.text = getString(R.string.success)
        googleToken.text = it
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SocialLogin.checkResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        disposableList.forEach {
            if (!it.isDisposed)
                it.dispose()
        }
        super.onDestroy()
    }
}
