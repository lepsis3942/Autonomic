package com.cjapps.autonomic.contextdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import coil.request.LoadRequestBuilder
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.FragContextDetailBinding
import com.cjapps.autonomic.playback.PlaybackSelectionFragment
import com.cjapps.autonomic.trigger.TriggerSelectionFragment
import com.cjapps.autonomic.view.ViewConstants
import com.cjapps.domain.Trigger
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.utility.extensions.observeValueFromNavBackStack
import com.cjapps.utility.livedata.EventObserver
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ContextDetailFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ContextDetailViewModel> { viewModelFactory }
    private val args by navArgs<ContextDetailFragmentArgs>()
    private val viewBinding by viewBindingLifecycle { FragContextDetailBinding.bind(requireView()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_context_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val executeChooseTrigger = fun() {
            viewModel.executeAction(ContextDetailAction.ChooseTrigger)
        }
        val executeChooseMusic = fun() {
            viewModel.executeAction(ContextDetailAction.ChooseMusic)
        }
        viewBinding.contextDetailTriggerChooseButton.setOnClickListener { executeChooseTrigger() }
        viewBinding.contextDetailEditTrigger.setOnClickListener { executeChooseTrigger() }
        viewBinding.contextDetailTriggerName.setOnClickListener { executeChooseTrigger() }
        viewBinding.contextDetailMusicChooseButton.setOnClickListener { executeChooseMusic() }
        viewBinding.contextDetailMusicName.setOnClickListener { executeChooseMusic() }
        viewBinding.contextDetailEditMusic.setOnClickListener { executeChooseMusic() }
        viewBinding.contextDetailSaveButton.setOnClickListener {
            viewModel.executeAction(ContextDetailAction.Save)
        }

        viewModel.viewState.apply {
            playbackUiState.observe(viewLifecycleOwner, Observer {
                updatePlaybackUi(it)
            })
            triggerUiState.observe(viewLifecycleOwner, Observer {
                updateTriggerUi(it)
            })
            navEvent.observe(viewLifecycleOwner, EventObserver {
                navigate(it)
            })
        }

        observeValueFromNavBackStack(TriggerSelectionFragment.KEY_SELECTED_TENTATIVE_TRIGGER, Observer<Trigger> { selection ->
            if (selection == null) return@Observer
            viewModel.executeAction(ContextDetailAction.TriggerUpdated(selection))
        })
        observeValueFromNavBackStack(PlaybackSelectionFragment.KEY_SELECTED_TENTATIVE_PLAYBACK, Observer<PlaylistSimple> { selection ->
            if (selection == null) return@Observer
            viewModel.executeAction(ContextDetailAction.PlaybackUpdated(selection))
        })

        viewModel.executeAction(ContextDetailAction.Init(args.contextId))
    }

    private fun updatePlaybackUi(uiState: PlaybackUiState) {
        val requestBuilder = fun (requestBuilder: LoadRequestBuilder) {
            requestBuilder.apply {
                crossfade(enable = true)
                transformations(*ViewConstants.IMAGE_VIEW_TRANSFORMATIONS)
            }
        }

        when (uiState) {
            is PlaybackUiState.Unset -> {
                viewBinding.apply {
                    contextDetailEditMusicGroup.visibility = View.GONE
                    contextDetailMusicChooseButton.visibility = View.VISIBLE
                    contextDetailMusicArt.load(drawableResId = R.drawable.ic_album_black, builder = requestBuilder)
                }
            }
            is PlaybackUiState.CanEdit -> {
                viewBinding.apply {
                    contextDetailEditMusicGroup.visibility = View.VISIBLE
                    contextDetailMusicChooseButton.visibility = View.GONE
                    contextDetailMusicName.text = uiState.musicTitle
                    contextDetailMusicArt.load(uri = uiState.albumArtUrl, builder = requestBuilder)
                }
            }
        }
    }

    private fun updateTriggerUi(uiState: TriggerUiState) {
        when (uiState) {
            is TriggerUiState.Unset -> {
                viewBinding.apply {
                    contextDetailEditTriggerGroup.visibility = View.GONE
                    contextDetailTriggerChooseButton.visibility = View.VISIBLE
                }
            }
            is TriggerUiState.CanEdit -> {
                viewBinding.apply {
                    contextDetailEditTriggerGroup.visibility = View.VISIBLE
                    contextDetailTriggerChooseButton.visibility = View.GONE
                    contextDetailTriggerName.text = uiState.triggerName
                }
            }
        }
    }

    private fun navigate(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.SetTrigger -> {
                val triggerDirections = ContextDetailFragmentDirections.actionContextDetailFragmentToTriggerSelectionFragment()
                findNavController().navigate(triggerDirections)
            }
            is NavEvent.SetMusic -> {
                val playbackDirections = ContextDetailFragmentDirections.actionContextDetailFragmentToPlaybackSelectionFragment()
                findNavController().navigate(playbackDirections)
            }
        }
    }
}