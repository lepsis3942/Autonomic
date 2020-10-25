package com.cjapps.autonomic.contextsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.FragContextSummaryBinding
import com.cjapps.autonomic.serialization.ISerializer
import com.cjapps.autonomic.view.SpaceItemDecoration
import com.cjapps.domain.PlaybackContext
import com.cjapps.utility.livedata.EventObserver
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ContextSummaryFragment: DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var serializer: ISerializer

    private val listAdapter = ContextSummaryListAdapter(::itemSelected)
    private val viewModel by viewModels<ContextSummaryViewModel> { viewModelFactory }
    private val viewBinding by viewBindingLifecycle { FragContextSummaryBinding.bind(requireView()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_context_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.contextSummaryCreateButton.setOnClickListener {
            viewModel.executeAction(ContextSummaryAction.CreateButtonTapped)
        }
        viewBinding.contextSummaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
            if (itemDecorationCount > 0) {
                for (i in 0 until itemDecorationCount) {
                    removeItemDecorationAt(i)
                }
            }
            addItemDecoration(SpaceItemDecoration(context, spaceHeight = 8.0F))
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val createButton = viewBinding.contextSummaryCreateButton
                    viewModel.executeAction(ContextSummaryAction.ContextListScrolled(dy, createButton.isShown))
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.navigationEventLiveData.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                NavDestination.Login -> findNavController().navigate(R.id.action_contextSummaryFragment_to_loginFragment)
                NavDestination.ContextCreation -> {
                    val action = ContextSummaryFragmentDirections.actionContextSummaryFragmentToContextDetailFragment()
                    findNavController().navigate(action)
                }
                is NavDestination.ContextEdit -> {
                    val action = ContextSummaryFragmentDirections.actionContextSummaryFragmentToContextDetailFragment(it.contextToEdit)
                    findNavController().navigate(action)
                }
            }
        })
        viewModel.uiState.contextItemsStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is ContextItemsUiState.Loading -> {
                    viewBinding.contextSummaryLoadingIndicator.visibility = View.VISIBLE
                    viewBinding.contextSummaryRecyclerView.visibility = View.GONE
                }
                is ContextItemsUiState.Empty -> {
                    viewBinding.contextSummaryLoadingIndicator.visibility = View.GONE
                    viewBinding.contextSummaryRecyclerView.visibility = View.GONE
                }
                is ContextItemsUiState.ItemsRetrieved -> {
                    listAdapter.submitList(it.items)
                    viewBinding.contextSummaryLoadingIndicator.visibility = View.GONE
                    viewBinding.contextSummaryRecyclerView.visibility = View.VISIBLE
                }
            }
        })
        viewModel.uiState.createContextButtonUiState.observe(viewLifecycleOwner, {
            when (it) {
                CreateContextButtonUiState.Show -> viewBinding.contextSummaryCreateButton.show()
                CreateContextButtonUiState.Hide -> viewBinding.contextSummaryCreateButton.hide()
            }
        })

        viewModel.executeAction(ContextSummaryAction.Initialize)
    }

    private fun itemSelected(playbackContext: PlaybackContext) {
        viewModel.executeAction(ContextSummaryAction.ContextSelectedToEdit(playbackContext))
    }
}