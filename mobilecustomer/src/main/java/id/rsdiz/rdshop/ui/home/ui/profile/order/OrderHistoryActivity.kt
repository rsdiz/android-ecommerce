package id.rsdiz.rdshop.ui.home.ui.profile.order

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.adapter.OrderListAdapter
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.databinding.ActivityOrderHistoryBinding
import id.rsdiz.rdshop.ui.home.ui.profile.order.detail.OrderDetailActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderHistoryActivity : AppCompatActivity() {
    private var _binding: ActivityOrderHistoryBinding? = null
    private val binding get() = _binding as ActivityOrderHistoryBinding

    private val viewModel: OrderViewModel by viewModels()

    private lateinit var orderListAdapter: OrderListAdapter

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceHelper(this).customPrefs(Consts.PREFERENCE_NAME)
        setVisibilityContent(View.GONE, isLoading = true, isEmpty = false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.layoutContent.setOnRefreshListener {
            viewModel.refresh()
            if (binding.layoutContent.isRefreshing) binding.layoutContent.isRefreshing = false
        }

        lifecycleScope.launch {
            fetchOrderData()
        }

        viewModel.orderData.observeForever {
            if (it.isEmpty()) {
                setVisibilityContent(View.GONE, isLoading = false, isEmpty = true)
            } else {
                setVisibilityContent(View.VISIBLE, isLoading = false, isEmpty = false)
            }

            orderListAdapter = OrderListAdapter(it.sortedBy { data -> data.getOffsetDate() })

            binding.rvOrders.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = orderListAdapter
            }

            orderListAdapter.setOnItemClickListener { orderItem ->
                val orderDetailIntent = Intent(this, OrderDetailActivity::class.java)
                orderDetailIntent.putExtra("orderId", orderItem.getOriginalOrderId())
                startActivity(orderDetailIntent)
            }
        }
    }

    private fun setVisibilityContent(
        visibility: Int,
        isLoading: Boolean = false,
        isEmpty: Boolean = false
    ) {
        binding.apply {
            layoutContent.visibility = visibility

            if (isEmpty) {
                layoutEmpty.visibility = View.VISIBLE
                layoutLoading.visibility = View.GONE
                layoutError.visibility = View.GONE
            } else {
                when (visibility) {
                    View.VISIBLE -> {
                        layoutLoading.visibility = View.GONE
                        layoutError.visibility = View.GONE
                    }
                    View.GONE -> {
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
    }

    private fun fetchOrderData() {
        viewModel.observerOrder(this, prefs[Consts.PREF_ID])
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
