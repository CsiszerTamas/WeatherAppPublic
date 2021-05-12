package com.cst.weatherapptest.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.cst.weatherapptest.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.disposables.CompositeDisposable
import kotlin.reflect.KClass

/**
 * Abstract base class of Fragments which contains common methods
 */
abstract class BaseFragment<B : ViewBinding, VM : ViewModel>(
    private var layoutResourceId: Int
) : Fragment() {

    protected lateinit var binding: B
    protected lateinit var viewModel: VM

    abstract fun viewModelClass(): KClass<VM>

    protected lateinit var navController: NavController

    protected val disposables: CompositeDisposable = CompositeDisposable()

    abstract fun showLoader()
    abstract fun hideLoader()

    abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val supportFragmentManager = requireActivity().supportFragmentManager

        try {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
            navController = navHostFragment.navController
        } catch (ex: IllegalStateException) {
            Log.e("DEBUG_", ex.toString())
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (::navController.isInitialized) {
            try {
                val supportFragmentManager = requireActivity().supportFragmentManager
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
                navController = navHostFragment.navController
            } catch (ex: IllegalStateException) {
                Log.e("DEBUG_", ex.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = this.setBinding(inflater, container)
        return binding.root
    }

    protected open fun onBackPressed() {
        navController.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    fun showGeneralErrorToastMessage() {
        Toast.makeText(
            this.requireContext(),
            getString(R.string.error_something_went_wrong),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showToastMessage(message: String?) {
        val toastMessage = message ?: getString(R.string.error_something_went_wrong)
        Toast.makeText(this.requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
    }

    fun showGeneralErrorDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_something_went_wrong)
            .setTitle(getString(R.string.error_dialog_title))
            .setMessage(getString(R.string.error_dialog_something_went_wrong))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.error_dialog_ok_button)) { dialog, _ ->
                dialog.dismiss()
            }.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.cta1, requireActivity().theme))
    }

    fun showErrorDialog(message: String?) {
        val dialogMessage = message ?: getString(R.string.error_dialog_something_went_wrong)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_something_went_wrong)
            .setTitle(getString(R.string.error_dialog_title))
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.error_dialog_ok_button)) { dialog, _ ->
                dialog.dismiss()
            }.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.cta1, requireActivity().theme))
    }
}
