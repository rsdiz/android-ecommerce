package id.rsdiz.rdshop.seller.ui.home.ui.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import id.rsdiz.rdshop.seller.ui.home.ui.profile.ProfileActivity
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_DATA_READY, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUiLoadingVisibility(savedInstanceState?.getBoolean(IS_DATA_READY) ?: false)

        newestOrderAdapter.setOnItemClickListener {
            val directions =
                DashboardFragmentDirections.actionDashboardFragmentToDetailOrderFragment(
                    it.getOriginalOrderId()
                )
            view.findNavController().navigate(directions)
        }

        lifecycleScope.launch {
            observeOrder(viewModel.newestOrder)
        }

        dashboardMenuAdapter.setOnItemClickListener {
            when (it.title) {
                "Order" -> {
                    val directions =
                        DashboardFragmentDirections.actionDashboardFragmentToOrderFragment()
                    view.findNavController().navigate(directions)
                }
                "Produk" -> {
                    val directions =
                        DashboardFragmentDirections.actionDashboardFragmentToProductFragment()
                    view.findNavController().navigate(directions)
                }
                "Kategori" -> {
                    val directions =
                        DashboardFragmentDirections.actionDashboardFragmentToCategoryFragment()
                    view.findNavController().navigate(directions)
                }
                "Akun" -> {
                    val directions =
                        DashboardFragmentDirections.actionDashboardFragmentToUserFragment()
                    view.findNavController().navigate(directions)
                }
                else ->
                    Toast.makeText(requireContext(), "Not Implemented Yet!", Toast.LENGTH_SHORT)
                        .show()
            }
        }

        binding.apply {
            toolbar.title = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 0..10 -> "Selamat Pagi "
                in 11..14 -> "Selamat Siang "
                in 15..18 -> "Selamat Sore "
                in 19..24 -> "Selamat Malam "
                else -> "Halo!"
            }.plus(prefs[Consts.PREF_NAME, prefs[Consts.PREF_USERNAME, ""]])

            toolbar.menu.forEach {
                it.setOnMenuItemClickListener {
                    val intent = Intent(requireContext(), ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
            }

            rvDashboardMenu.apply {
                layoutManager =
                    GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
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
                observeMenu()
                lifecycleScope.launch {
                    viewModel.refreshOrder()
                    content.isRefreshing = false
                }
            }
        }

        observeMenu()
    }

    override fun onPause() {
        super.onPause()
        viewModel.newestOrder.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        viewModel.newestOrder.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private fun observeMenu() {
        val listMenu: List<DashboardMenu> = listOf(
            DashboardMenu("Akun", R.drawable.ic_baseline_group_24),
            DashboardMenu("Kategori", R.drawable.ic_dashboard_black_24dp),
            DashboardMenu("Produk", R.drawable.ic_baseline_layers_24),
            DashboardMenu("Order", R.drawable.ic_baseline_receipt_24)
        )
        dashboardMenuAdapter.submitData(listMenu)

        lifecycleScope.launch {
            observeMenu(listMenu[0], viewModel.countUser())
        }

        lifecycleScope.launch {
            observeMenu(listMenu[1], viewModel.countCategory())
        }

        lifecycleScope.launch {
            observeMenu(listMenu[2], viewModel.countProduct("all"))
        }

        lifecycleScope.launch {
            observeMenu(listMenu[3], viewModel.countOrder())
        }

        updateUiLoadingVisibility(true)
    }

    private fun observeMenu(menu: DashboardMenu, resource: Resource<Int>) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { count ->
                    menu.count = count
                    menu.isError = false
                    dashboardMenuAdapter.apply {
                        updateData(
                            getIndex(menu),
                            menu
                        )
                    }
                }
            }
            is Resource.Error -> {
                menu.isError = true
                dashboardMenuAdapter.apply {
                    updateData(
                        getIndex(menu),
                        menu
                    )
                }
            }
            else -> {}
        }
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

    companion object {
        private const val IS_DATA_READY = "isDataReady"
    }
}
