package id.rsdiz.rdshop.seller.ui.home.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.databinding.ActivityProfileBinding
import id.rsdiz.rdshop.seller.ui.home.ui.profile.edit.EditProfileActivity
import id.rsdiz.rdshop.seller.ui.splash.SplashActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding as ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        prefs = PreferenceHelper(this).customPrefs(Consts.PREFERENCE_NAME)
        setContentView(binding.root)

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
            val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
            startActivity(intent)
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
                            this@ProfileActivity,
                            layoutContent,
                            response.data.toString(),
                            Snackbar.LENGTH_SHORT
                        ).addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(
                                transientBottomBar: Snackbar?,
                                event: Int
                            ) {
                                super.onDismissed(transientBottomBar, event)
                                this@ProfileActivity.applicationContext.startActivity(
                                    Intent(
                                        this@ProfileActivity.applicationContext,
                                        SplashActivity::class.java
                                    ).addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    ).addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    )
                                )
                                prefs[Consts.PREF_TOKEN] = Consts.RDSHOP_API_KEY
                                this@ProfileActivity.finishAffinity()
                            }
                        }).show()
                    }
                    is Resource.Error -> {
                        progressBarLogout.visibility = View.GONE
                        Snackbar.make(
                            this@ProfileActivity,
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

        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            viewModel.refreshUser()
            return@setOnMenuItemClickListener true
        }

        binding.buttonLogout.setOnClickListener { logout() }

        binding.buttonEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
