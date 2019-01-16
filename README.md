# SocialLogin

Android library for simple login with Google, Facebook, Twitter.

## Library Features

* Get Facebook token
* Get Twitter token and secret
* Get Google token 

## Install using Bintray JCenter

Add library dependency to your build.gradle:
```groovy
repositories {
  jcenter()
}
...
dependencies {
  implementation 'com.cadabrastudio:sociallogin:0.1.0@aar'
  implementation 'com.facebook.android:facebook-login:[4,5)'
  implementation('com.twitter.sdk.android:twitter:3.3.0@aar') {
      transitive = true
  }
  implementation 'com.google.android.gms:play-services-auth:16.0.1'
  api 'io.reactivex.rxjava2:rxjava:2.2.2'
}

```

## Usage

#### Initialize API

``` kotlin
class App: Application() {

    override fun onCreate() {
       SocialLogin.initializeFacebook(this, getString(R.string.app_facebook_id))
       SocialLogin.initializeGoogle(getString(R.string.google_server_client_id))
       SocialLogin.initializeTwitter(this, getString(R.string.twitter_key), getString(R.string.twitter_secret))
   }

}
```
#### Event subscription(custom view)
``` kotlin
    SocialLogin.setGoogleListener(activity, googleButton, object : LoginListener<String> {
        override fun onSuccess(token: String) {
            /**do something with success result */
        }

        override fun onError(throwable: Throwable) {
            /**do something with success result */
        }
    })

    SocialLogin.setFacebookListener(activity, facebookButton, object : LoginListener<String> {
        override fun onSuccess(token: String) {
            /**do something with success result */
        }

        override fun onError(throwable: Throwable) {
            /**do something with success result */
        }
    })

    SocialLogin.setTwitterListener(activity, twitterButton, object : LoginListener<TwitterAuthToken> {
        override fun onSuccess(token: TwitterAuthToken) {
            /**do something with success result */
        }

            override fun onError(throwable: Throwable) {
                /**do something with success result */
            }
        })
```
#### Pass the Activity's Result Back to the library

```kotlin
...
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SocialLogin.checkResult(requestCode, resultCode, data)
    }
...
```
#### RxJava 

``` kotlin 
SocialLogin.setRxGoogleListener(this, googleButton).subscribe({
        /**do something with success result */
      }, { 
        /** do something with error result */
      })
SocialLogin.setRxFacebookListener(this, facebookButton).subscribe({
        /**do something with success result */
      }, { 
        /** do something with error result */
      })
SocialLogin.setRxTwitterListener(this, twitterButton).subscribe({
        /**do something with success result */
      }, { 
        /** do something with error result */
      })
```
#### With Facebook LoginButton
``` kotlin
//Listener
SocialLogin.setFacebookListener(facebookButton, object : LoginListener<String> {
    override fun onSuccess(token: TwitterAuthToken) {
        authorizeTwitter(token)
    }

    override fun onError(throwable: Throwable) {
        failTwitter(throwable)
    }
})
//RxJava
SocialLogin.setRxFacebookListener(facebookButton).subscribe(::authorizeFacebook, ::failFacebook)
```
#### With TwitterLoginButton
``` kotlin
//Listener
SocialLogin.setTwitterListener(twitterButton, object : LoginListener<TwitterAuthToken> {
    override fun onSuccess(token: TwitterAuthToken) {
        authorizeTwitter(token)
    }

    override fun onError(throwable: Throwable) {
        failTwitter(throwable)
    }
})
//RxJava
SocialLogin.setRxTwitterListener(twitterButton).subscribe(::authorizeTwitter, ::failTwitter)
```

#### Check authorization and get token
``` kotlin 
if (SocialLogin.isGoogleAuthorized(this))
    authorizeGoogle(SocialLogin.getGoogleToken(this))

if (SocialLogin.isFacebookAuthorized())
    authorizeFacebook(SocialLogin.getFacebookToken())

if (SocialLogin.isTwitterAuthorized())
    authorizeTwitter(SocialLogin.getTwitterAuthToken())
```
## License

```
Copyright 2019 Cadabra Studio

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
