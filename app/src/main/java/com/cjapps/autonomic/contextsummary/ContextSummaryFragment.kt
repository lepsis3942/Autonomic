package com.cjapps.autonomic.contextsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.cjapps.autonomic.R
import com.cjapps.autonomic.bridge.PlaybackInfo
import com.cjapps.autonomic.bridge.PlaybackOptions
import com.cjapps.autonomic.bridge.SpotifyPlaybackCommandWorker
import com.cjapps.autonomic.livedata.EventObserver
import com.cjapps.autonomic.serialization.ISerializer
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.frag_context_summary.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-12-30.
 */
class ContextSummaryFragment: DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var serializer: ISerializer

    private val viewModel by viewModels<ContextSummaryViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_context_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        testButton.setOnClickListener { findNavController().navigate(R.id.action_contextSummaryFragment_to_loginFragment) }
        test_button.setOnClickListener {
            val playbackInfo = PlaybackInfo(
                "spotify:playlist:37i9dQZF1DX8CopunbDxgW",
                PlaybackOptions(shuffle = true)
            )
            val request = OneTimeWorkRequestBuilder<SpotifyPlaybackCommandWorker>()
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setTriggerContentMaxDelay(10, TimeUnit.SECONDS)
                    .build())
                .setInputData(SpotifyPlaybackCommandWorker.buildInputData(playbackInfo, serializer))
                .build()
            context?.let { WorkManager.getInstance(it).enqueue(request) }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.navigationEventLiveData.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                Login -> findNavController().navigate(R.id.action_contextSummaryFragment_to_loginFragment)
            }
        })

        viewModel.initialize()
    }
}