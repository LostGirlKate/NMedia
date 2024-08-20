package ru.netology.nmedia.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.SignUpViewModel


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignUp()
    }

    private fun initSignUp() {
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
                        val name = binding.teName.editText?.text.toString()
                        viewModel.registry(login, password, name)
                    }
                    .setDuration(8000)
                    .show()
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.btnPickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.btnTakePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.btnClearPhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.imAvatar.setImageResource(R.drawable.ic_face)
                binding.btnClearPhoto.visibility = View.GONE
                return@observe
            }
            binding.btnClearPhoto.visibility = View.VISIBLE
            binding.imAvatar.setImageURI(it.uri)
        }

        binding.btnSignUp.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            binding.teConfirmPassword.error = null
            binding.teLogin.error = null
            binding.tePassword.error = null
            binding.teName.error = null
            val login = binding.teLogin.editText?.text.toString()
            val password = binding.tePassword.editText?.text.toString()
            val name = binding.teName.editText?.text.toString()
            if (checkAllFields()) {
                viewModel.registry(login, password, name)
            }

        }

    }

    private fun checkAllFields(): Boolean {
        val password = binding.tePassword.editText?.text.toString()
        val confirmPassword = binding.teConfirmPassword.editText?.text.toString()
        binding.teLogin.error =
            if (binding.teLogin.editText?.text?.isEmpty() == true) getString(R.string.error_empty_field) else null
        binding.tePassword.error =
            if (binding.tePassword.editText?.text?.isEmpty() == true) getString(R.string.error_empty_field) else null
        binding.teName.error =
            if (binding.teName.editText?.text?.isEmpty() == true) getString(R.string.error_empty_field) else null
        binding.teConfirmPassword.error =
            if (binding.teConfirmPassword.editText?.text?.isEmpty() == true) getString(R.string.error_empty_field) else null
        binding.teConfirmPassword.error =
            if (confirmPassword != password) getString(R.string.error_password_confirm) else null
        return binding.teName.error == null &&
                binding.teLogin.error == null &&
                binding.tePassword.error == null &&
                binding.teConfirmPassword.error == null
    }


}