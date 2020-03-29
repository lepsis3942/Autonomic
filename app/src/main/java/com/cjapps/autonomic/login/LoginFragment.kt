package com.cjapps.autonomic.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cjapps.autonomic.R
import com.cjapps.autonomic.livedata.EventObserver
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.authentication.AuthenticationClient
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.frag_login.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-12-30.
 */
class LoginFragment: DaggerFragment(), IHostActivityResultListener {
    companion object {
        private const val SPOTIFY_LOGIN_REQUEST_CODE = 3487
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val loginViewModel by viewModels<LoginViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Timber.d("back pressed")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contextWrapper = ContextThemeWrapper(activity, R.style.AppTheme_DarkBackground)
        return inflater.cloneInContext(contextWrapper).inflate(R.layout.frag_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_get_started_button.setOnClickListener { loginViewModel.loginButtonClicked() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel.isLoadingLiveData.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                login_get_started_button.visibility = View.GONE
                login_progress_loading_indicator.visibility = View.VISIBLE
            } else {
                login_get_started_button.visibility = View.VISIBLE
                login_progress_loading_indicator.visibility = View.GONE
            }
        })
        loginViewModel.launchAuthenticationLiveData.observe(viewLifecycleOwner, EventObserver {
            AuthenticationClient.openLoginActivity(activity, SPOTIFY_LOGIN_REQUEST_CODE, it)
        })
        loginViewModel.errorEventLiveData.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(login_coordinator, it, Snackbar.LENGTH_LONG).show()
        })
        loginViewModel.snackBarEventLiveData.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(login_coordinator, it, Snackbar.LENGTH_LONG).show()
        })
        loginViewModel.loginCompleteEventLiveData.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(login_coordinator, "Logged In!", Snackbar.LENGTH_LONG).show()
            findNavController().popBackStack()
        })
    }

    override fun onHostActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            loginViewModel.handleAuthenticationResponse(data, resultCode)
        }
    }
}