package id.rsdiz.rdshop.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import id.rsdiz.rdshop.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding as FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            loginButton.setOnClickListener {
                val direction = AuthFragmentDirections.actionAuthFragmentToLoginFragment()
                view.findNavController().navigate(direction)
            }
            registrationButton.setOnClickListener {
                val direction = AuthFragmentDirections.actionAuthFragmentToRegisterFragment()
                view.findNavController().navigate(direction)
            }
            catalogButton.setOnClickListener {
                val direction = AuthFragmentDirections.actionAuthFragmentToCatalogFragment()
                view.findNavController().navigate(direction)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
