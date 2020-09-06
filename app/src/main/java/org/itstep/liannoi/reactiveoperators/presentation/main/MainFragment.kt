package org.itstep.liannoi.reactiveoperators.presentation.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import org.itstep.liannoi.reactiveoperators.R
import org.itstep.liannoi.reactiveoperators.databinding.FragmentMainBinding
import org.itstep.liannoi.reactiveoperators.presentation.common.extensions.getViewModelFactory
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels { getViewModelFactory() }
    private lateinit var viewDataBinding: FragmentMainBinding
    private val disposable: CompositeDisposable = CompositeDisposable()

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
        setupBackgroundChange()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Dispose
    ///////////////////////////////////////////////////////////////////////////

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

    private fun setupBackgroundChange() {
        val colors: MutableList<Int> = mutableListOf()

        Observable.interval(500, TimeUnit.MILLISECONDS)
            .flatMap { Observable.fromCallable { (0..255).random() } }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    colors.add(it)
                    if (colors.size < 3) {
                        return@subscribe
                    }

                    requireView().findViewById<LinearLayout>(R.id.linear_layout)
                        .setBackgroundColor(Color.rgb(colors[0], colors[1], colors[2]))

                    colors.clear()
                },
                { Log.d(TAG, "setupBackgroundChange: ${it.message}") }
            ).addTo(disposable)
    }

    private fun setupDoubleClick() {
        var clicks = 0

        requireView().findViewById<Button>(R.id.double_click_button)
            .clicks()
            .doOnNext { ++clicks }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (clicks > 1) {
                        Toast.makeText(requireContext(), "Hi!", Toast.LENGTH_SHORT).show()
                    }

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
