package com.cjapps.autonomic.contextdetail

import android.os.Bundle
import android.view.*
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
import com.cjapps.domain.Playlist
import com.cjapps.domain.Repeat
import com.cjapps.domain.Trigger
import com.cjapps.utility.extensions.observeValueFromNavBackStack
import com.cjapps.utility.livedata.EventObserver
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import com.google.android.material.snackbar.Snackbar
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
        viewBinding.contextDetailShuffle.setOnClickListener {
            viewModel.executeAction(ContextDetailAction.ShuffleTapped)
        }
        viewBinding.contextDetailRepeat.setOnClickListener {
            viewModel.executeAction(ContextDetailAction.RepeatTapped)
        }

        viewModel.viewState.apply {
            errorEvent.observe(viewLifecycleOwner, EventObserver {
                Snackbar.make(viewBinding.contextDetailSaveButton, it, Snackbar.LENGTH_LONG).show()
            })
            playbackUiState.observe(viewLifecycleOwner, {
                updatePlaybackUi(it)
            })
            toolbarUiState.observe(viewLifecycleOwner, {
                setHasOptionsMenu(it == ToolbarUiState.ShowDelete)
            })
            triggerUiState.observe(viewLifecycleOwner, {
                updateTriggerUi(it)
            })
            navEvent.observe(viewLifecycleOwner, EventObserver {
                navigate(it)
            })
            shuffleIsSelectedUiState.observe(viewLifecycleOwner, {
                viewBinding.contextDetailShuffle.isSelected = it
            })
            repeatIsSelectedUiState.observe(viewLifecycleOwner, {
                updateRepeatUi(it)
            })
        }

        observeValueFromNavBackStack(TriggerSelectionFragment.KEY_SELECTED_TENTATIVE_TRIGGER, Observer<Trigger> { selection ->
            if (selection == null) return@Observer
            viewModel.executeAction(ContextDetailAction.TriggerUpdated(selection))
        })
        observeValueFromNavBackStack(PlaybackSelectionFragment.KEY_SELECTED_TENTATIVE_PLAYBACK, Observer<Playlist> { selection ->
            if (selection == null) return@Observer
            viewModel.executeAction(ContextDetailAction.PlaybackUpdated(selection))
        })

        viewModel.executeAction(ContextDetailAction.Init(args.contextToEdit))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.context_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.context_detail_menu_delete -> {
                viewModel.executeAction(ContextDetailAction.DeleteContext)
                true
            }
            else -> false
        }
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
                    contextDetailMusicArt.load(
                        drawableResId = R.drawable.ic_album_black,
                        builder = requestBuilder
                    )
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

    private fun updateRepeatUi(repeat: Repeat) {
        val repeatImageView = viewBinding.contextDetailRepeat

        when (repeat) {
            Repeat.NONE,
            Repeat.ALL -> repeatImageView.setImageResource(R.drawable.ic_repeat_black)
            Repeat.ONCE -> repeatImageView.setImageResource(R.drawable.ic_repeat_one_black)
        }

        // Only change selection state if previous state requires change
        if (repeat == Repeat.NONE && repeatImageView.isSelected) {
            repeatImageView.isSelected = false
        } else if ((repeat == Repeat.ALL || repeat == Repeat.ONCE) && !repeatImageView.isSelected) {
            repeatImageView.isSelected = true
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
            is NavEvent.Finish -> {
                findNavController().popBackStack()
            }
        }
    }
}