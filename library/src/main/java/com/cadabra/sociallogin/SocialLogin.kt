package com.cadabra.sociallogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

internal const val GOOGLE_SING_IN_CODE = 436
object SocialLogin {
    private var googleSingInListener: GoogleSingInListener? = null
    private var googleSignInOptions: GoogleSignInOptions? = null
    private var facebookCallbackManager: CallbackManager? = null
    private var twitterClient: TwitterAuthClient? = null

    fun initializeTwitter(context: Context, consumerKey: String, consumerSecret: String) {
        val config = TwitterConfig.Builder(context.applicationContext)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(consumerKey, consumerSecret))
            .debug(true)
            .build()
        Twitter.initialize(config)
        twitterClient = TwitterAuthClient()
    }

    fun initializeFacebook(context: Context, applicationId: String) {
        FacebookSdk.setApplicationId(applicationId)
        FacebookSdk.setAutoLogAppEventsEnabled(true)
        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
        FacebookSdk.sdkInitialize(context.applicationContext)
        facebookCallbackManager = CallbackManager.Factory.create()
    }

    fun initializeGoogle(clientId: String) {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
    }

    fun setRxFacebookListener(activity: Activity, view: View) : Observable<String> {
        facebookCallbackManager ?: throw Exception("Facebook API not initialized.")

        val out = Observable.defer { Observable.create<String> {
            LoginManager.getInstance().registerCallback(facebookCallbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    if (result?.accessToken?.token != null) {
                        it.onNext(result.accessToken?.token!!)
                        it.onComplete()
                    } else {
                        it.onError(Exception("Token are null"))
                    }
                }

                override fun onCancel() {
                    it.onError(Exception("Canceled"))
                }

                override fun onError(error: FacebookException?) {
                    it.onError(error ?: Exception("Unknown error"))
                }

            })
        } }

        view.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email"))
        }
        return out
    }

    fun setRxGoogleListener(activity: Activity, view: View) : Observable<String> {
        val option = googleSignInOptions ?: throw Exception("Google API not initialized.")

        val out = Observable.defer { Observable.create<String> {
            googleSingInListener = object : GoogleSingInListener() {
                override fun onSuccess(token: String) {
                    it.onNext(token)
                }

                override fun onError(throwable: Throwable) {
                    it.onError(throwable)
                }

            }
        }}

        view.setOnClickListener {
            val googleSignInClient = GoogleSignIn.getClient(activity, option)
            activity.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SING_IN_CODE)
        }
        return out
    }

    fun setRxTwitterListener(activity: Activity, view: View) : Observable<TwitterAuthToken> {
        val client = twitterClient ?: throw Exception("Twitter API not initialized.")

        var callback: Callback<TwitterSession>? = null
        val out = Observable.defer { Observable.create<TwitterAuthToken> {
            callback = object : Callback<TwitterSession>() {
                override fun success(result: Result<TwitterSession>?) {
                    if (result?.data?.authToken == null) {
                        it.onError(Exception("Token are null"))
                    } else {
                        it.onNext(result.data?.authToken!!)
                    }
                }

                override fun failure(exception: TwitterException?) {
                    it.onError(exception ?: Exception("Unknown error"))
                }
            }
        } }

        view.setOnClickListener { client.authorize(activity, callback) }
        return out
    }

    fun setFacebookListener(activity: Activity, view: View, listener: LoginListener<String>) {
        facebookCallbackManager ?: throw Exception("Facebook API not initialized.")

        LoginManager.getInstance().registerCallback(facebookCallbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result?.accessToken?.token != null) {
                    listener.onSuccess(result.accessToken?.token!!)
                } else {
                    listener.onError(Exception("Token are null"))
                }
            }

            override fun onCancel() {
                listener.onError(Exception("Canceled"))
            }

            override fun onError(error: FacebookException?) {
                listener.onError(error ?: Exception("Unknown error"))
            }

        })

        view.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email"))
        }
    }

    fun setGoogleListener(activity: Activity, view: View, listener: LoginListener<String>) {
        val option = googleSignInOptions ?: throw Exception("Google API not initialized.")

        googleSingInListener = object : GoogleSingInListener() {
            override fun onSuccess(token: String) {
                listener.onSuccess(token)
            }

            override fun onError(throwable: Throwable) {
                listener.onError(throwable)
            }

        }

        view.setOnClickListener {
            val googleSignInClient = GoogleSignIn.getClient(activity, option)
            activity.startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SING_IN_CODE)
        }
    }

    fun setTwitterListener(activity: Activity, view: View, listener: LoginListener<TwitterAuthToken>) {
        val client = twitterClient ?: throw Exception("Twitter API not initialized.")

        val callback: Callback<TwitterSession>? = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                if (result?.data?.authToken == null)
                    listener.onError(Exception("Token are null"))
                else
                    listener.onSuccess(result.data?.authToken!!)
            }

            override fun failure(exception: TwitterException?) {
                listener.onError(exception ?: Exception("Unknown error"))
            }
        }

        view.setOnClickListener { client.authorize(activity, callback) }
    }

    fun setRxFacebookListener(view: LoginButton) : Observable<String> {
        facebookCallbackManager ?: throw Exception("Facebook API not initialized.")

        var emitter: ObservableEmitter<String>? = null
        val out = Observable.defer { Observable.create<String> {
            emitter = it
        } }

        view.registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result?.accessToken?.token != null) {
                    emitter?.onNext(result.accessToken?.token!!)
                } else {
                    emitter?.onError(Exception("Token are null"))
                }
            }

            override fun onCancel() {
                emitter?.onError(Exception("Canceled"))
            }

            override fun onError(error: FacebookException?) {
                emitter?.onError(error ?: Exception("Unknown error"))
            }

        })
        return out
    }

    fun setFacebookListener(view: LoginButton, listener: LoginListener<String>) {
        facebookCallbackManager ?: throw Exception("Facebook API not initialized.")

        view.registerCallback(facebookCallbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result?.accessToken?.token != null) {
                    listener.onSuccess(result.accessToken?.token!!)
                } else {
                    listener.onError(Exception("Token are null"))
                }
            }

            override fun onCancel() {
                listener.onError(Exception("Canceled"))
            }

            override fun onError(error: FacebookException?) {
                listener.onError(error ?: Exception("Unknown error"))
            }

        })
    }

    fun setRxTwitterListener(view: TwitterLoginButton) : Observable<TwitterAuthToken> {
        twitterClient ?: throw Exception("Twitter API not initialized.")

        var emitter: ObservableEmitter<TwitterAuthToken>? = null
        val out = Observable.defer { Observable.create<TwitterAuthToken> {
            emitter = it
        } }
        val callback: Callback<TwitterSession>? = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                if (result?.data?.authToken == null) {
                    emitter?.onError(Exception("Token are null"))
                } else {
                    emitter?.onNext(result.data?.authToken!!)
                }
            }

            override fun failure(exception: TwitterException?) {
                emitter?.onError(exception ?: Exception("Unknown error"))
            }
        }
        view.callback = callback
        return out
    }

    fun setTwitterListener(view: TwitterLoginButton, listener: LoginListener<TwitterAuthToken>) {
        twitterClient ?: throw Exception("Twitter API not initialized.")

        val callback: Callback<TwitterSession>? = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                if (result?.data?.authToken == null)
                    listener.onError(Exception("Token are null"))
                else
                    listener.onSuccess(result.data?.authToken!!)
            }

            override fun failure(exception: TwitterException?) {
                listener.onError(exception ?: Exception("Unknown error"))
            }
        }

        view.callback = callback
    }

    fun isFacebookAuthorized() : Boolean {
        if (!FacebookSdk.isInitialized()) throw Exception("Facebook API not initialized.")
        val token = AccessToken.getCurrentAccessToken()
        return token != null && !token.isExpired
    }

    fun getFacebookToken() : String {
        if (!FacebookSdk.isInitialized()) throw Exception("Facebook API not initialized.")
        val token = AccessToken.getCurrentAccessToken() ?: throw Exception("Facebook didn't login.")
        return token.token
    }

    fun isTwitterAuthorized() : Boolean {
        Twitter.getInstance()
        return TwitterCore.getInstance().sessionManager.activeSession != null
    }

    fun getTwitterAuthToken() : TwitterAuthToken {
        Twitter.getInstance()
        TwitterCore.getInstance().sessionManager.activeSession ?: throw Exception("Twitter didn't login.")
        return TwitterCore.getInstance().sessionManager.activeSession.authToken
    }

    fun isGoogleAuthorized(context: Context) : Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null && !account.isExpired
    }

    fun getGoogleToken(context: Context) : String {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: throw Exception("Google didn't login.")
        if (account.isExpired) throw Exception("Google token expired.")
        return account.idToken!!
    }

    fun checkResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        twitterClient?.onActivityResult(requestCode, resultCode, data)
        googleSingInListener?.onActivityResult(requestCode, resultCode, data)
    }


}