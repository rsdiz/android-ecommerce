package id.rsdiz.rdshop.seller.ui.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.flush
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.seller.databinding.FragmentLoginBinding
import id.rsdiz.rdshop.seller.ui.home.HomeActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding as FragmentLoginBinding
    private val prefs get() = PreferenceHelper(requireContext()).customPrefs(Consts.PREFERENCE_NAME)

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            backButton.setOnClickListener {
                requireActivity().onBackPressed()
            }
            loginButton.setOnClickListener {
                errorText.visibility = View.GONE
                loginButton.visibility = View.GONE
                loginIndicator.visibility = View.VISIBLE

                val login = inputUsername.text.toString()
                val password = inputPassword.text.toString()

                lifecycleScope.launch {
                    when (val response = viewModel.signIn(login, password)) {
                        is Resource.Success -> {
                            errorText.visibility = View.GONE

                            if (Role.valueOf(prefs[Consts.PREF_ROLE]) == Role.ADMIN) {
                                Toast.makeText(
                                    context,
                                    "Login Successfully! Welcome ${
                                    prefs[
                                        Consts.PREF_NAME,
                                        prefs[Consts.PREF_USERNAME, "Admin"]
                                    ]
                                    }!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                startActivity(
                                    Intent(
                                        requireActivity().applicationContext,
                                        HomeActivity::class.java
                                    ).addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    ).addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    )
                                )
                                requireActivity().finishAffinity()
                            } else {
                                errorText.text = "Error: You're Not Admin!"
                                errorText.visibility = View.VISIBLE
                                loginIndicator.visibility = View.GONE
                                loginButton.visibility = View.VISIBLE

                                prefs.flush()
                            }
                        }
                        is Resource.Error -> {
                            loginIndicator.visibility = View.GONE
                            loginButton.visibility = View.VISIBLE

                            errorText.text = "Error: ${response.message}"
                            errorText.visibility = View.VISIBLE
                        }
                        else -> {}
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
