//package com.cjapps.autonomic.login
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import com.cjapps.autonomic.R
//import com.cjapps.autonomic.livedata.Event
//import com.cjapps.autonomic.livedata.EventObserver
//import com.google.android.material.snackbar.Snackbar
//import com.spotify.sdk.android.authentication.AuthenticationClient
//import dagger.android.AndroidInjection
//import kotlinx.android.synthetic.main.activity_login.*
//import javax.inject.Inject
//
///**
// * Created by cjgonz on 2019-09-14.
// */
//class LoginActivity : AppCompatActivity() {
//    companion object {
//        private const val SPOTIFY_LOGIN_REQUEST_CODE = 3487
//    }
//    @Inject lateinit var viewModel: LoginViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        AndroidInjection.inject(this)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        login_get_started_button.setOnClickListener { viewModel.loginButtonClicked() }
//
//        viewModel.isLoadingLiveData.observe(this, Observer { isLoading ->
//            if (isLoading) {
//                login_get_started_button.visibility = View.GONE
//                login_progress_loading_indicator.visibility = View.VISIBLE
//            } else {
//                login_get_started_button.visibility = View.VISIBLE
//                login_progress_loading_indicator.visibility = View.GONE
//            }
//        })
//        viewModel.launchAuthenticationLiveData.observe(this, EventObserver {
//            AuthenticationClient.openLoginActivity(this@LoginActivity, SPOTIFY_LOGIN_REQUEST_CODE, it)
//        })
//        viewModel.errorEventLiveData.observe(this, EventObserver {
//            Snackbar.make(login_coordinator, it, Snackbar.LENGTH_LONG).show()
//        })
////        viewModel.snackBarEventLiveData.observe(this, EventObserver {
////            Snackbar.make(login_coordinator, it, Snackbar.LENGTH_LONG).show()
////        })
////        viewModel.snackBarEventLiveData.postValue(Event("test"))
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
//            viewModel.handleAuthenticationResponse(data, resultCode)
//        }
//    }
//}