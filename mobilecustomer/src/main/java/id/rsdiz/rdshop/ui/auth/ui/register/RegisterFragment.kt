package id.rsdiz.rdshop.ui.auth.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding as FragmentRegisterBinding

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            backButton.setOnClickListener {
                requireActivity().onBackPressed()
            }
            registerButton.setOnClickListener {
                errorText.visibility = View.GONE
                registerButton.visibility = View.GONE
                registerIndicator.visibility = View.VISIBLE

                val name = inputName.text.toString()
                val username = inputUsername.text.toString()
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                lifecycleScope.launch {
                    when (val response = viewModel.signUp(name, username, email, password)) {
                        is Resource.Success -> {
                            errorText.visibility = View.GONE

                            Toast.makeText(
                                context,
                                "Registration Successfully, Please Login!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val directions =
                                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                            view.findNavController().navigate(directions)
                        }
                        is Resource.Error -> {
                            registerIndicator.visibility = View.GONE
                            registerButton.visibility = View.VISIBLE

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
