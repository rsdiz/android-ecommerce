package id.rsdiz.rdshop.ui.home

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.databinding.ActivityHomeBinding
import java.io.File

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding as ActivityHomeBinding

    private var _navHostFragment: NavHostFragment? = null
    private val navHostFragment get() = _navHostFragment as NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        _navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
            as NavHostFragment

        NavigationUI.setupWithNavController(
            navView,
            navHostFragment.navController
        )
    }

    override fun onBackPressed() {
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        if (fragment is IOnBackPressed) {
            (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
                super.onBackPressed()
            }
        } else super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            trimCache(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _binding = null
        _navHostFragment = null
    }

    private fun trimCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            if (dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: Exception) {
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }

        // The directory is now empty so delete it
        return dir?.delete() ?: false
    }
}
