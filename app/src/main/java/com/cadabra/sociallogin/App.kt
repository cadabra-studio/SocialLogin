package com.cadabra.sociallogin

import android.app.Application

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SocialLogin.initializeFacebook(this, getString(R.string.app_facebook_id))
        SocialLogin.initializeGoogle(getString(R.string.google_server_client_id))
        SocialLogin.initializeTwitter(this, getString(R.string.twitter_key), getString(R.string.twitter_secret))
    }
}