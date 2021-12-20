package com.theworld.androidtemplatewithnavcomponents.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.theworld.androidtemplatewithnavcomponents.R
import com.theworld.androidtemplatewithnavcomponents.databinding.FragmentProfileBinding
import com.theworld.androidtemplatewithnavcomponents.ui.auth.AuthViewModel
import com.theworld.androidtemplatewithnavcomponents.utils.SharedPrefManager
import com.theworld.androidtemplatewithnavcomponents.utils.imageUrl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {


    companion object {
        private const val TAG = "ProfileFragment"
    }


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    /*----------------------------------------- On Create View -------------------------------*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    /*----------------------------------------- On ViewCreated -------------------------------*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()
        clickListeners()

    }

    /*----------------------------------------- Init -------------------------------*/


    private fun init() {
        binding.includedProfile.tvName.text = sharedPrefManager.getString("operator_name")
        binding.includedProfile.tvDescription.text = sharedPrefManager.getString("company_name")
        binding.includedProfile.ratingBar.rating = sharedPrefManager.getInt("rating").toFloat()

        binding.includedProfile.image.load(imageUrl + sharedPrefManager.getString("image")) {
            crossfade(true)
            crossfade(100)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
            transformations(
                CircleCropTransformation()
            )
        }
    }

    /*----------------------------------------- Click Listeners -------------------------------*/

    private fun clickListeners() {

        binding.includedProfileItems.changePasswordContainer.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }


        binding.includedProfileItems.accountContainer.setOnClickListener {

            /*val action =
                SellerProfileFragmentDirections.actionSellerProfileToSellerEditProfileFragment()
            findNavController().navigate(action)*/
        }





        binding.includedProfileItems.logoutContainer.setOnClickListener {

            doLogout()
            binding.includedProfileItems.loadingSpinner.isVisible = true

        }
    }


    private fun doLogout() {

        /*lifecycleScope.launchWhenStarted {
            viewModel.logout(RoleEnum.SELLER).collect { resource ->

                binding.includedProfileItems.loadingSpinner.isVisible = resource is Resource.Loading

                when (resource) {

                    is Resource.Success -> {

                        clearAuthData()
                        findNavController().redirectToDestination(
                            popUpTo = R.id.seller_graph_xml,
                            destination = R.id.sellerProfile
                        )

                    }
                    is Resource.Failure -> {
                        handleApiError(resource)
                    }
                }

            }
        }*/
    }

    /*---------------- ---------- On DestroyView -------------------------------*/

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}