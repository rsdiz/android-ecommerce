package id.rsdiz.rdshop.seller.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.seller.databinding.AuthActivityBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private var _binding: AuthActivityBinding? = null
    private val binding get() = _binding as AuthActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AuthActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
