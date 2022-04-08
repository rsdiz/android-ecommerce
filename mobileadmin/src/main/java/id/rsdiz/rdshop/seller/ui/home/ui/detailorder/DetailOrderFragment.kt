package id.rsdiz.rdshop.seller.ui.home.ui.detailorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.OrderDetail
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.adapter.ProductListWithHeaderAdapter
import id.rsdiz.rdshop.seller.common.OrderDetailItemUiState
import id.rsdiz.rdshop.seller.common.OrderItemUIState
import id.rsdiz.rdshop.seller.databinding.FragmentDetailOrderBinding
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DetailOrderFragment : Fragment() {
    private var _binding: FragmentDetailOrderBinding? = null
    private val binding get() = _binding as FragmentDetailOrderBinding

    private val viewModel: DetailOrderViewModel by viewModels()

    private lateinit var productListAdapter: ProductListWithHeaderAdapter

    private val dataArgs get() = DetailOrderFragmentArgs.fromBundle(arguments as Bundle)

    private val timeZone: TimeZone = TimeZone.getDefault()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        lifecycleScope.launch {
            fetchOrderData(dataArgs.orderId ?: dataArgs.order!!.orderId)
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

    private suspend fun setOrderUi(orderItemUiState: OrderItemUIState) {
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
                orderItemUiState.getAddressCountry(),
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
            Toast.makeText(requireContext(), "Clicked ${it.getProductName()}", Toast.LENGTH_SHORT)
                .show()
        }

        binding.rvDetailOrder.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = productListAdapter
        }

        productListAdapter.clear()

        for (orderDetail in list) {
            viewModel.getProduct(orderDetail.productId).observe(viewLifecycleOwner) { resource ->
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
            val materialDialog = MaterialAlertDialogBuilder(requireContext())
            val trackingLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_tracking_number, null, false)
            val trackingCourier =
                trackingLayout.findViewById<TextInputLayout>(R.id.input_tracking_courier)
            val trackingNumber =
                trackingLayout.findViewById<TextInputLayout>(R.id.input_tracking_number)

            val courierList = resources.getStringArray(R.array.courier).toList()
            val adapter =
                ArrayAdapter(requireContext(), R.layout.item_list_courier, courierList)
            (trackingCourier.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            textOrderStatus.text = orderItemUiState.getOrderStatusValue()
            when (orderItemUiState.getOrderStatusKey()) {
                Consts.STATUS_WAITING -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_sync_24)
                    buttonUpdateStatus.text = "Konfirmasi"
                    buttonUpdateStatus.setOnClickListener {
                        materialDialog
                            .setTitle("Konfirmasi Pesanan?")
                            .setMessage("Setelah Anda mengkonfirmasi pesanan ini, segeralah untuk mempacking pesanan ini agar bisa dikirimkan.")
                            .setNegativeButton("Batal") { dialog, _ ->
                                dialog.dismiss()
                            }.setPositiveButton("Ya") { dialog, _ ->
                                lifecycleScope.launch {
                                    updateOrderStatus(Consts.STATUS_PROCESSED)
                                    dialog.dismiss()
                                }
                            }
                            .show()
                    }
                }
                Consts.STATUS_PROCESSED -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_archive_24)
                    buttonUpdateStatus.text = "Selesai Memproses"
                    buttonUpdateStatus.setOnClickListener {
                        materialDialog.setView(trackingLayout)
                            .setTitle("Barang sudah Dikirimkan?")
                            .setMessage("Masukkan Nomor Resinya ya!")
                            .setNegativeButton("Batal") { dialog, _ ->
                                removeParent(trackingLayout)
                                dialog.dismiss()
                            }.setPositiveButton("Ya") { dialog, _ ->
                                lifecycleScope.launch {

                                    updateOrderStatus(
                                        Consts.STATUS_DISPATCH,
                                        StringBuilder(trackingNumber.editText?.text.toString())
                                            .append("|")
                                            .append(trackingCourier.editText?.text.toString())
                                            .toString()
                                    )
                                    removeParent(trackingLayout)
                                    dialog.dismiss()
                                }
                            }.setOnCancelListener {
                                removeParent(trackingLayout)
                            }
                            .show()
                    }
                }
                Consts.STATUS_DISPATCH -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_delivery_dining_24)
                    buttonUpdateStatus.text = "Lacak Pengiriman"
                    buttonUpdateStatus.setOnClickListener {
                        if (orderItemUiState.getTrackingNumber() != null) {
                            val directions =
                                DetailOrderFragmentDirections.actionDetailOrderFragmentToTrackingFragment(
                                    orderItemUiState.getTrackingNumber()!!,
                                    orderItemUiState.getTrackingCourier()!!,
                                    orderItemUiState.getOrderStatusKey().toInt()
                                )
                            view?.findNavController()?.navigate(directions)
                        }
                    }
                }
                Consts.STATUS_ARRIVED -> {
                    iconOrderStatus.setImageResource(R.drawable.ic_baseline_home_work_24)
                    buttonUpdateStatus.text = "Detail Pengiriman"
                    buttonUpdateStatus.setOnClickListener {
                        if (orderItemUiState.getTrackingNumber() != null) {
                            val directions =
                                DetailOrderFragmentDirections.actionDetailOrderFragmentToTrackingFragment(
                                    orderItemUiState.getTrackingNumber()!!,
                                    orderItemUiState.getTrackingCourier()!!,
                                    orderItemUiState.getOrderStatusKey().toInt()
                                )
                            view?.findNavController()?.navigate(directions)
                        }
                    }
                }
            }
        }
    }

    private fun removeParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    private suspend fun updateOrderStatus(status: Short, trackingNumber: String = "") {
        dataArgs.orderId?.let {
            viewModel.updateOrderStatus(
                orderId = it,
                status = status,
                trackingNumber = trackingNumber
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
