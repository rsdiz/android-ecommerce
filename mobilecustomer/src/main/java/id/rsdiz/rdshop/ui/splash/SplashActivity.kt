package id.rsdiz.rdshop.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.BuildConfig
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.databinding.ActivitySplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding as ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = PreferenceHelper(applicationContext).customPrefs(Consts.PREFERENCE_NAME)

        val token: String? = prefs[Consts.PREF_TOKEN, BuildConfig.API_KEY]
        val homeIntent: Intent = if (token.equals(BuildConfig.API_KEY) || token.isNullOrEmpty()) {
            Intent(this, /* TODO: Redirect to Un-auth Navigation Graph */)
        } else {
            Toast.makeText(applicationContext, "Sudah Login!", Toast.LENGTH_SHORT).show()
            Intent(this, /* TODO: Redirect to Auth Navigation Graph */)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            delay(TIMEOUT)
            withContext(Dispatchers.Main) {
                startActivity(homeIntent)
                finishAffinity()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TIMEOUT = 2500L
    }
}
