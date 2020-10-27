package ru.raider.date

import android.R
import android.app.Application
import android.util.Log
import android.widget.TextView
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin


class AmplifyClass : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())

            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")


            /*Amplify.Auth.signIn(
                    "raiderr",
                    "LYtq2sT6@",
                    { result -> Log.i("MyAmplifyApp", if (result.isSignInComplete) "Sign in succeeded" else "Sign in not complete") },
                    { error -> Log.e("MyAmplifyApp", error.toString()) }
            )*/

        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }
}