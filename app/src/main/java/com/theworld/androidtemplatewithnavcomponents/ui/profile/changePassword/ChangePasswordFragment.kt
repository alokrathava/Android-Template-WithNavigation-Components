package com.theworld.androidtemplatewithnavcomponents.ui.profile.changePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.theworld.androidtemplatewithnavcomponents.data.user.changePassword.ChangePasswordRequestData
import com.theworld.androidtemplatewithnavcomponents.databinding.FragmentChangePasswordBinding
import com.theworld.androidtemplatewithnavcomponents.ui.auth.AuthViewModel
import com.theworld.androidtemplatewithnavcomponents.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!


    private lateinit var currentPassword: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    private val viewModel: AuthViewModel by viewModels()


    /*----------------------------------------- On Create View -------------------------------*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChangePasswordBinding.inflate(inflater)
        return binding.root
    }

    /*----------------------------------------- On ViewCreated -------------------------------*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()
        clickListeners()
        observe()

    }


    /*----------------------------------------- Init -------------------------------*/


    private fun init() {


    }

    private fun observe() {
        collectLatestFlow(viewModel.snackbar) {
            requireView().snackbar(it)
        }
    }

    /*----------------------------------------- Click Listeners -------------------------------*/

    private fun clickListeners() {

        binding.apply {


            btnSubmit.setOnClickListener {

                if (!validateCurrentPassword() or !validatePassword() or !validateConfirmPassword()) {
                    return@setOnClickListener
                }

                val data = ChangePasswordRequestData(
                    oldPassword = currentPassword,
                    newPassword = password,
                    role = getRole()
                )

                doChange(data)

            }


        }
    }

    /*----------------------------------------- Reset Password -------------------------------*/


    private fun doChange(data: ChangePasswordRequestData) {

        binding.loadingSpinner.isVisible = true

        collectLatestFlow(viewModel.changePassword(data)) { resource ->

            binding.loadingSpinner.isVisible = resource is Resource.Loading


            when (resource) {
                is Resource.Success -> {

                    withContext(Dispatchers.Main) {
                        viewModel.displaySnackBar("Password Changed Successfully")
                        findNavController().navigateUp()
                    }

                }
                is Resource.Failure -> {
                    handleApiError(resource)
                }

                Resource.Loading -> Unit
            }

        }

    }

/*----------------------------------------- do Validations -------------------------------*/


    private fun validateCurrentPassword(): Boolean {

        currentPassword = binding.edtCurrentPassword.editText!!.text.toString().trim()
        return when {
            currentPassword.isEmpty() -> {
                binding.edtCurrentPassword.error = "Field can't be empty"
                false
            }
            else -> {
                binding.edtCurrentPassword.error = null
                true
            }
        }
    }


    private fun validatePassword(): Boolean {
        password = binding.edtNewPassword.editText!!.text.toString().trim()
        return when {
            password.isEmpty() -> {
                binding.edtNewPassword.error = "Field can't be empty"
                false
            }
            else -> {
                binding.edtNewPassword.error = null
                true
            }
        }
    }


    private fun validateConfirmPassword(): Boolean {
        confirmPassword = binding.edtConfirmPassword.editText!!.text.toString().trim()
        return when {
            confirmPassword.isEmpty() -> {
                binding.edtConfirmPassword.error = "Field can't be empty"
                false
            }
            confirmPassword != password -> {
                binding.edtConfirmPassword.error = "Please Confirm Password"
                false
            }
            else -> {
                binding.edtConfirmPassword.error = null
                true
            }
        }
    }


/*----------------------------------------- On DestroyView -------------------------------*/

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}
