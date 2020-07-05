package com.cjapps.autonomic.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.FragPlaybackSelectionBinding
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.utility.extensions.saveValueToNavBackStack
import com.cjapps.utility.livedata.EventObserver
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class PlaybackSelectionFragment : DaggerFragment() {
    companion object {
        const val KEY_SELECTED_TENTATIVE_PLAYBACK = "key_selected_tentative_playback"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val listAdapter = PlaybackSelectionListAdapter(::adapterItemSelected, ::endOfAdapterReached)
    private val viewModel by viewModels<PlaybackSelectionViewModel> { viewModelFactory }
    private val viewBinding by viewBindingLifecycle { FragPlaybackSelectionBinding.bind(requireView()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_playback_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.playbackSelectList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewSate.apply {
            playlistUi.observe(viewLifecycleOwner, Observer { handlePlaylistUpdate(it) })
            loadingUiState.observe(viewLifecycleOwner, Observer { handleLoadingStates(it) })
            navEvent.observe(viewLifecycleOwner, EventObserver { navigate(it) })
        }

        viewModel.executeAction(PlaybackSelectionAction.Init)
    }

    private fun navigate(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.NavigateBack -> {
                saveValueToNavBackStack(KEY_SELECTED_TENTATIVE_PLAYBACK, navEvent.playlist)
                findNavController().popBackStack()
            }
        }
    }

    private fun adapterItemSelected(selectedItem: PlaylistSimple) {
        viewModel.executeAction(PlaybackSelectionAction.PlaylistSelected(selectedItem))
    }

    private fun endOfAdapterReached() {
        viewModel.executeAction(PlaybackSelectionAction.LoadMorePlaylists)
    }

    private fun handlePlaylistUpdate(playlistUiState: PlaybackSelectionViewModel.PlaylistUiState) {
        when (playlistUiState) {
            is PlaybackSelectionViewModel.PlaylistUiState.PlaylistsRetrieved -> {
                listAdapter.submitList(playlistUiState.allPlaylists)
            }
        }
    }

    private fun handleLoadingStates(loadingUiState: PlaybackSelectionViewModel.LoadingUiState) {
        viewBinding.apply {
            when (loadingUiState) {
                is PlaybackSelectionViewModel.LoadingUiState.LoadingInitial -> {
                    playbackSelectList.visibility = View.GONE
                    playbackSelectCenterLoadingIndicator.visibility = View.VISIBLE
                    playbackSelectBottomLoadingIndicator.visibility = View.GONE
                }
                is PlaybackSelectionViewModel.LoadingUiState.LoadingMorePlaylists -> {
                    playbackSelectList.visibility = View.VISIBLE
                    playbackSelectCenterLoadingIndicator.visibility = View.GONE
                    playbackSelectBottomLoadingIndicator.visibility = View.VISIBLE
                }
                is PlaybackSelectionViewModel.LoadingUiState.None -> {
                    playbackSelectList.visibility = View.VISIBLE
                    playbackSelectCenterLoadingIndicator.visibility = View.GONE
                    playbackSelectBottomLoadingIndicator.visibility = View.GONE
                }
            }
        }
    }
}