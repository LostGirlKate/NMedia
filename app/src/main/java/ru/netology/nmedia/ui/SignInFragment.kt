package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding

    private val viewModel: SignInViewModel by activityViewModels()

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignIn()
    }

    private fun initSignIn() {
        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authenticated)
                findNavController().navigateUp()
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_auth, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_loading) {
                        val login = binding.teLogin.editText?.text.toString()
                        val password = binding.tePassword.editText?.text.toString()
                        viewModel.auth(login, password)
                    }
                    .setDuration(8000)
                    .show()
            }
        }


        binding.btnSignIn.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            binding.teLogin.error = null
            binding.tePassword.error = null
            val login = binding.teLogin.editText?.text.toString()
            val password = binding.tePassword.editText?.text.toString()
            if (checkAllFields()) {
                viewModel.auth(login, password)
            }
        }
    }

    private fun checkAllFields(): Boolean {
        binding.teLogin.error =
            if (binding.teLogin.editText?.text?.isEmpty() == true) getString(R.string.error_empty_field) else null
        binding.tePassword.error =
            if (binding.tePassword.editText?.text?.isEmpty() == true) getString(R.string.error_empty_field) else null
        return binding.teLogin.error == null &&
                binding.tePassword.error == null
    }

}