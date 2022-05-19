package id.rsdiz.rdshop.ui.home.ui.profile.order.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.adapter.ProductListWithHeaderAdapter
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.common.OrderDetailItemUiState
import id.rsdiz.rdshop.common.OrderItemUIState
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.data.model.OrderDetail
import id.rsdiz.rdshop.databinding.ActivityOrderDetailBinding
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class OrderDetailActivity : AppCompatActivity() {
    private var _binding: ActivityOrderDetailBinding? = null
    private val binding get() = _binding as ActivityOrderDetailBinding

    private val viewModel: OrderDetailViewModel by viewModels()

    private lateinit var productListAdapter: ProductListWithHeaderAdapter

    private val timeZone: TimeZone = TimeZone.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.extras != null) {
            lifecycleScope.launch {
                fetchOrderData(
                    intent?.extras?.getString("orderId")
                        ?: intent?.extras?.getParcelable<Order>("order")!!.orderId
                )
            }
        } else {
            onBackPressed()
        }
    }

    private suspend fun fetchOrderData(orderId: String) {
        collectLast(viewModel.getOrder(orderId = orderId)) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        setOrderUi(OrderItemUIState(it))
                        setVisibilityContent(View.VISIBLE)
                    }
                }
                is Resource.Loading -> {
                    setVisibilityContent(View.GONE, true)
                }
                else -> {
                    setVisibilityContent(View.GONE, false)
                    binding.errorText.text = resource.message ?: ""
                }
            }
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

    private fun setOrderUi(orderItemUiState: OrderItemUIState) {
        binding.apply {
            textCountProduct.text =
                getString(R.string.string_count_product, orderItemUiState.getOrderDetail().size)
            textOrderId.text =
                getString(R.string.string_order_id, orderItemUiState.getSimpleOrderId())

            var timeZoneInitials = ""
            for (s in timeZone.displayName.split(" ")) {
                timeZoneInitials += s[0]
            }
            textOrderDate.text = getString(
                R.string.string_order_date,
                orderItemUiState.getOrderTime(),
                // timeZoneInitials,
                "",
                orderItemUiState.getFullDate()
            )
            textShippingCost.text = orderItemUiState.getShippingCost()
            textTotal.text = orderItemUiState.getOrderTotal()
            textOrderAddress.text = getString(
                R.string.string_order_address,
                orderItemUiState.getOrderName(),
                orderItemUiState.getAddressStreetName(),
                orderItemUiState.getAddressCity(),
                orderItemUiState.getAddressProvince(),
                orderItemUiState.getAddressPostalCode()
            )
            textOrderPhone.text =
                getString(R.string.string_phone_number, orderItemUiState.getPhone())

            setRvDetailOrder(orderItemUiState.getOrderDetail())
            setStatusUi(orderItemUiState)
        }
    }

    private fun setRvDetailOrder(list: List<OrderDetail>) {
        productListAdapter = ProductListWithHeaderAdapter {
            Toast.makeText(this, "Clicked ${it.getProductName()}", Toast.LENGTH_SHORT)
                .show()
        }

        binding.rvDetailOrder.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = productListAdapter
        }

        productListAdapter.clear()

        for (orderDetail in list) {
            viewModel.getProduct(orderDetail.productId).observe(this) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let {
                            productListAdapter.insertData(
                                OrderDetailItemUiState(
                                    product = it,
                                    orderDetail = orderDetail
                                )
                            )
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun setStatusUi(orderItemUiState: OrderItemUIState) {
        binding.apply {
            textOrderStatus.text = orderItemUiState.getOrderStatusValue()
            when (orderItemUiState.getOrderStatusKey()) {
                Consts.STATUS_WAITING -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_sync_24)
                    buttonCheckTracking.visibility = View.GONE
                }
                Consts.STATUS_PROCESSED -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_archive_24)
                    buttonCheckTracking.visibility = View.GONE
                }
                Consts.STATUS_DISPATCH -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_delivery_dining_24)
                    buttonCheckTracking.visibility = View.VISIBLE
                    buttonCheckTracking.text = "Lacak Pengiriman"
                    buttonCheckTracking.setOnClickListener {
                        if (orderItemUiState.getTrackingNumber().isNotEmpty()) {
                        }
                    }
                }
                Consts.STATUS_ARRIVED -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_home_work_24)
                    buttonCheckTracking.visibility = View.VISIBLE
                    buttonCheckTracking.text = "Detail Pengiriman"
                    buttonCheckTracking.setOnClickListener {
                        if (orderItemUiState.getTrackingNumber().isNotEmpty()) {
                        }
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
