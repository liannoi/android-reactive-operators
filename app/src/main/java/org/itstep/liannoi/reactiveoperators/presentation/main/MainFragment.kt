package org.itstep.liannoi.reactiveoperators.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.itstep.liannoi.reactiveoperators.R
import org.itstep.liannoi.reactiveoperators.databinding.FragmentMainBinding
import org.itstep.liannoi.reactiveoperators.presentation.common.extensions.getViewModelFactory
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels { getViewModelFactory() }
    private lateinit var viewDataBinding: FragmentMainBinding
    private val disposable: CompositeDisposable = CompositeDisposable()
    private var clicks: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setupDoubleClick()
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private fun setupDoubleClick() {
        requireView().findViewById<Button>(R.id.double_click_button)
            .clicks()
            .doOnNext { ++clicks }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (clicks > 1)
                        Toast.makeText(requireContext(), "Hi!", Toast.LENGTH_SHORT).show()

                    clicks = 0
                },
                { Log.d(TAG, "setupDoubleClick: ${it.message}") }
            ).addTo(disposable)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Companion object
    ///////////////////////////////////////////////////////////////////////////

    companion object {
        private const val TAG: String =
            "org.itstep.liannoi.reactiveoperators.presentation.main.MainFragment"
    }
}
