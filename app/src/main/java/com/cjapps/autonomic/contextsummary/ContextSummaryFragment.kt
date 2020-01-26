package com.cjapps.autonomic.contextsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cjapps.autonomic.R
import kotlinx.android.synthetic.main.frag_context_summary.*
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-12-30.
 */
class ContextSummaryFragment
    @Inject constructor(
    ): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_context_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testButton.setOnClickListener { findNavController().navigate(R.id.action_contextSummaryFragment_to_loginFragment) }
    }
}