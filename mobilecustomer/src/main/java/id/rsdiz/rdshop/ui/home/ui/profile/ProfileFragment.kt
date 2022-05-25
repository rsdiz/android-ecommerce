package id.rsdiz.rdshop.ui.home.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.databinding.FragmentProfileBinding
import id.rsdiz.rdshop.ui.splash.SplashActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding as FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefs = PreferenceHelper(requireContext()).customPrefs(Consts.PREFERENCE_NAME)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            collectLast(viewModel.getUser(prefs[Consts.PREF_ID])) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        setVisibilityContent(View.VISIBLE)

                        resource.data?.let { user ->

                            prefs[Consts.PREF_ID] = user.userId
                            prefs[Consts.PREF_USERNAME] = user.username
                            prefs[Consts.PREF_NAME] = user.name
                            prefs[Consts.PREF_ADDRESS] = user.address
                            prefs[Consts.PREF_GENDER] = user.gender?.name
                            prefs[Consts.PREF_PHOTO] = user.photo

                            binding.apply {
                                Glide.with(root.context)
                                    .load(user.photo)
                                    .error(R.drawable.bg_image_error)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .circleCrop()
                                    .into(profileImage)

                                profileName.text = user.name
                            }
                        }
                    }
                    is Resource.Loading -> setVisibilityContent(View.GONE, true)
                    is Resource.Error -> {
//                        setVisibilityContent(View.GONE, false)
//                        binding.errorText.text = resource.message

                        binding.apply {
                            Glide.with(root.context)
                                .load(prefs[Consts.PREF_PHOTO, ""])
                                .error(R.drawable.bg_image_error)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(profileImage)

                            profileName.text = prefs[Consts.PREF_NAME, ""]
                        }
                    }
                }
            }
        }

        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            viewModel.refreshUser()
            return@setOnMenuItemClickListener true
        }

        binding.buttonLogout.setOnClickListener { logout() }

        binding.buttonEditProfile.setOnClickListener {
            val direction = ProfileFragmentDirections.actionNavigationProfileToEditProfileActivity()
            view.findNavController().navigate(direction)
        }

        binding.buttonLastOrder.setOnClickListener {
            val direction = ProfileFragmentDirections.actionNavigationProfileToOrderHistoryActivity()
            view.findNavController().navigate(direction)
        }
    }

    private fun logout() {
        binding.apply {
            lifecycleScope.launch {
                progressBarLogout.visibility = View.VISIBLE
                when (val response = viewModel.signOut(prefs[Consts.PREF_TOKEN, ""])) {
                    is Resource.Success -> {
                        progressBarLogout.visibility = View.GONE
                        Snackbar.make(
                            requireContext(),
                            layoutContent,
                            response.data.toString(),
                            Snackbar.LENGTH_SHORT
                        ).addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                requireActivity().applicationContext.startActivity(
                                    Intent(
                                        requireActivity().applicationContext,
                                        SplashActivity::class.java
                                    ).addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    ).addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    )
                                )
                                prefs[Consts.PREF_TOKEN] = Consts.RDSHOP_API_KEY
                                requireActivity().finishAffinity()
                            }
                        }).show()
                    }
                    is Resource.Error -> {
                        progressBarLogout.visibility = View.GONE
                        Snackbar.make(
                            requireContext(),
                            layoutContent,
                            "Gagal Logout!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun setVisibilityContent(visibility: Int, isLoading: Boolean = false) {
        binding.apply {
            when (visibility) {
                View.VISIBLE -> {
                    layoutContent.visibility = visibility
                    layoutLoading.visibility = View.GONE
                    layoutError.visibility = View.GONE
                }
                View.GONE -> {
                    layoutContent.visibility = visibility
                    if (isLoading) {
                        layoutLoading.visibility = View.VISIBLE
                        layoutError.visibility = View.GONE
                    } else {
                        layoutLoading.visibility = View.GONE
                        layoutError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
