package com.cjapps.autonomic.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.FragLoginBinding
import com.cjapps.utility.livedata.EventObserver
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class LoginFragment: DaggerFragment(), IHostActivityResultListener {
    companion object {
        private const val SPOTIFY_LOGIN_REQUEST_CODE = 3487
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val loginViewModel by viewModels<LoginViewModel> { viewModelFactory }
    private val viewBinding by viewBindingLifecycle { FragLoginBinding.bind(requireView()) }

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
        viewBinding.loginGetStartedButton.setOnClickListener { loginViewModel.loginButtonClicked() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel.isLoadingLiveData.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
                viewBinding.loginGetStartedButton.visibility = View.GONE
                viewBinding.loginProgressLoadingIndicator.visibility = View.VISIBLE
            } else {
                viewBinding.loginGetStartedButton.visibility = View.VISIBLE
                viewBinding.loginProgressLoadingIndicator.visibility = View.GONE
            }
        })
        loginViewModel.launchAuthenticationLiveData.observe(viewLifecycleOwner, EventObserver {
            AuthorizationClient.openLoginActivity(activity, SPOTIFY_LOGIN_REQUEST_CODE, it)
        })
        loginViewModel.errorEventLiveData.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(viewBinding.loginCoordinator, it, Snackbar.LENGTH_LONG).show()
        })
        loginViewModel.snackBarEventLiveData.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(viewBinding.loginCoordinator, it, Snackbar.LENGTH_LONG).show()
        })
        loginViewModel.loginCompleteEventLiveData.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(viewBinding.loginCoordinator, "Logged In!", Snackbar.LENGTH_LONG).show()
            findNavController().popBackStack()
        })
    }

    override fun onHostActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            loginViewModel.handleAuthenticationResponse(data, resultCode)
        }
    }
}