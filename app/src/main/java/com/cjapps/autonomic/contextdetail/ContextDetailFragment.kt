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
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.FragContextDetailBinding
import com.cjapps.autonomic.trigger.TriggerSelectionFragment
import com.cjapps.autonomic.trigger.model.TentativeBluetoothSelection
import com.cjapps.utility.extensions.observeValueFromNavBackStack
import com.cjapps.utility.livedata.EventObserver
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ContextDetailFragment: DaggerFragment() {

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

        viewBinding.contextDetailTriggerChooseButton.setOnClickListener {
            viewModel.executeAction(ContextDetailAction.ChooseTrigger)
        }

        viewModel.viewState.apply {
            triggerUiState.observe(viewLifecycleOwner, Observer {
                updateTriggerUi(it)
            })
            navEvent.observe(viewLifecycleOwner, EventObserver {
                navigate(it)
            })
        }

        observeValueFromNavBackStack(TriggerSelectionFragment.KEY_SELECTED_TENTATIVE_TRIGGER, Observer<TentativeBluetoothSelection> { selection ->
            if (selection == null) return@Observer
            viewModel.executeAction(ContextDetailAction.TriggerUpdated(selection))
        })

        viewModel.executeAction(ContextDetailAction.Init(args.contextId))
    }

    private fun updateTriggerUi(uiState: TriggerUiState) {
        when (uiState) {
            is TriggerUiState.Unset -> {
                viewBinding.contextDetailEditTriggerGroup.visibility = View.GONE
                viewBinding.contextDetailTriggerChooseButton.visibility = View.VISIBLE
            }
        }
    }

    private fun navigate(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.SetTrigger -> {
                val triggerDirections = ContextDetailFragmentDirections.actionContextDetailFragmentToTriggerSelectionFragment()
                findNavController().navigate(triggerDirections)
            }
        }
    }
}