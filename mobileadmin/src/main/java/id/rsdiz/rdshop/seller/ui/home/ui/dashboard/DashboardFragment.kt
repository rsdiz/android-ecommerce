package id.rsdiz.rdshop.seller.ui.home.ui.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.adapter.DashboardMenuAdapter
import id.rsdiz.rdshop.seller.adapter.NewestOrderAdapter
import id.rsdiz.rdshop.seller.common.DashboardMenu
import id.rsdiz.rdshop.seller.databinding.FragmentDashboardBinding
import id.rsdiz.rdshop.seller.ui.splash.SplashActivity
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding as FragmentDashboardBinding

    private val viewModel: DashboardViewModel by viewModels()

    private val newestOrderAdapter = NewestOrderAdapter()
    private val dashboardMenuAdapter = DashboardMenuAdapter()

    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefs = PreferenceHelper(requireContext()).customPrefs(Consts.PREFERENCE_NAME)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            observeMenu(viewModel.countData())
            observeOrder(viewModel.newestOrder)
        }

        newestOrderAdapter.setOnItemClickListener {
            val directions =
                DashboardFragmentDirections.actionDashboardFragmentToDetailOrderFragment(
                    it.getOriginalOrderId()
                )
            view.findNavController().navigate(directions)
        }

        dashboardMenuAdapter.setOnItemClickListener {
            when (it.title) {
                "Order" -> {
                    val directions =
                        DashboardFragmentDirections.actionDashboardFragmentToOrderFragment()
                    view.findNavController().navigate(directions)
                }
                else ->
                    Toast.makeText(requireContext(), "MENU ${it.title} DI KLIK", Toast.LENGTH_SHORT)
                        .show()
            }
        }

        binding.apply {
            toolbar.title = when (Calendar.getInstance().get(Calendar.HOUR)) {
                in 0..10 -> {
                    "Selamat Pagi "
                }
                in 11..14 -> {
                    "Selamat Siang "
                }
                in 15..18 -> {
                    "Selamat Sore "
                }
                in 19..24 -> {
                    "Selamat Malam "
                }
                else -> {
                    "Halo!"
                }
            }.plus(prefs[Consts.PREF_NAME, prefs[Consts.PREF_USERNAME, ""]])

            toolbar.menu.forEach {
                it.setOnMenuItemClickListener {
                    lifecycleScope.launch {
                        when (val response = viewModel.signOut(prefs[Consts.PREF_TOKEN, ""])) {
                            is Resource.Success -> {
                                Snackbar.make(
                                    requireContext(),
                                    content,
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
                                Snackbar.make(
                                    requireContext(),
                                    content,
                                    "Gagal Logout!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                            }
                        }
                    }
                    true
                }
            }

            rvDashboardMenu.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                adapter = dashboardMenuAdapter
            }

            rvNewestOrder.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                adapter = newestOrderAdapter
            }

            labelErrorButton.setOnClickListener {
                content.isRefreshing = true
            }

            content.setOnRefreshListener {
                Log.d("RDSHOP-DEBUG", "onViewCreated: REFRESHING")
                lifecycleScope.launch {
                    observeMenu(viewModel.countData())
                    viewModel.refreshOrder()
                    content.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroyView() {
        viewModel.newestOrder.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private fun observeMenu(list: List<Resource<Int>>) {
        val titleList = listOf("Akun", "Kategori", "Produk", "Order")
        val imageResIdList = listOf(
            R.drawable.ic_baseline_group_24,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_baseline_layers_24,
            R.drawable.ic_baseline_receipt_24
        )

        var index = 0
        var isFetchedAll = false
        var errorMessage = ""
        val listMenu = mutableListOf<DashboardMenu>()
        list.forEach { resources ->
            when (resources) {
                is Resource.Success -> {
                    resources.data?.let { count ->
                        val dashboardMenu = DashboardMenu(
                            title = titleList[index],
                            imageResId = imageResIdList[index],
                            count = count
                        )
                        listMenu.add(dashboardMenu)
                        isFetchedAll = true
                    }
                }
                is Resource.Error -> {
                    isFetchedAll = false
                    errorMessage = resources.message.toString()
                }
                else -> {
                    isFetchedAll = false
                }
            }
            index++
        }
        dashboardMenuAdapter.refreshMenu(listMenu)
        updateUiLoadingVisibility(isFetchedAll, errorMessage)
    }

    private fun observeOrder(livedata: LiveData<Resource<List<Order>>>) {
        livedata.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resource.Success -> {
                    resources.data?.let {
                        newestOrderAdapter.setOrders(it)
                        updateUiRvNewestOrderVisibility(true)
                        return@observe
                    }
                    updateUiRvNewestOrderVisibility(null)
                }
                is Resource.Loading -> updateUiRvNewestOrderVisibility(false)
                is Resource.Error -> {
                    updateUiRvNewestOrderVisibility(null)
                    binding.errorOrderGroup.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUiLoadingVisibility(dataIsReady: Boolean?, errorMessage: String = "") {
        binding.apply {
            when (dataIsReady) {
                true -> {
                    layerLoading.visibility = View.GONE
                    content.visibility = View.VISIBLE
                }
                false -> {
                    layerLoading.visibility = View.VISIBLE
                    content.visibility = View.GONE
                }
                else -> {
                    layerLoading.visibility = View.GONE
                    content.visibility = View.GONE
                    layerError.visibility = View.VISIBLE
                    errorText.text = errorMessage
                }
            }
        }
    }

    private fun updateUiRvNewestOrderVisibility(dataIsReady: Boolean?) {
        binding.apply {
            when (dataIsReady) {
                true -> {
                    loadingIndicator.visibility = View.GONE
                    errorOrderGroup.visibility = View.GONE
                    rvNewestOrder.visibility = View.VISIBLE
                }
                false -> {
                    rvNewestOrder.visibility = View.GONE
                    errorOrderGroup.visibility = View.GONE
                    loadingIndicator.visibility = View.VISIBLE
                }
                else -> {
                    loadingIndicator.visibility = View.GONE
                    errorOrderGroup.visibility = View.GONE
                    emptyOrderGroup.visibility = View.VISIBLE
                    rvNewestOrder.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
