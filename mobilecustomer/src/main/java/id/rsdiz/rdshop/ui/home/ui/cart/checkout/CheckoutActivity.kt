package id.rsdiz.rdshop.ui.home.ui.cart.checkout

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.adapter.DeliveryServiceAdapter
import id.rsdiz.rdshop.adapter.OrderListAdapter
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.common.OrderUiState
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.*
import id.rsdiz.rdshop.databinding.ActivityCheckoutBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding as ActivityCheckoutBinding

    private lateinit var prefs: SharedPreferences

    private val viewModel: CheckoutViewModel by viewModels()

    private lateinit var orderListAdapter: OrderListAdapter

    private var ongkirTotal = 0
    private var user: User? = null
    private var selectedServiceCost: ServiceCost? = null
    private var costs = mutableListOf<Cost>()
    private var cityList = mutableListOf<City>()
    private var provinceList = mutableListOf<Province>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceHelper(this).customPrefs(Consts.PREFERENCE_NAME)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setupListAndAdapter()
        lifecycleScope.launch {
            fetchOrderData()
        }
        lifecycleScope.launch {
            fetchCityList()
            fetchProvinceList()
            fetchUserData()
            setBindingAddress()
            setBindingDelivery()
        }
    }

    private suspend fun fetchUserData() {
        collectLast(viewModel.getUser(prefs[Consts.PREF_ID])) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        user = it
                        setVisibilityContent(View.VISIBLE, isLoading = false, isEmpty = false)
                        setVisibilityContent(VISIBLE_ADDRESS)
                    }
                }
                is Resource.Loading -> setVisibilityContent(View.GONE, true)
                is Resource.Error -> {
                    setVisibilityContent(View.GONE, false)
                    binding.errorText.text = resource.message
                }
            }
        }
    }

    private suspend fun fetchProvinceList() {
        when (val response = viewModel.getProvinces()) {
            is Resource.Success -> {
                response.data?.let {
                    it.forEach { province ->
                        provinceList.add(province)
                    }
                }

                val provinceNameList = mutableListOf("")
                provinceNameList.addAll(
                    provinceList.map {
                        it.province
                    }
                )

                val arrayAdapter = ArrayAdapter(
                    this,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    provinceNameList
                )
                binding.inputOrderAddressProvince.adapter = arrayAdapter
                binding.inputOrderAddressProvince.setSelection(0)
            }
            else -> {
                Log.d("RDSHOP-DEBUG", "fetchProvinceList: Error: ${response.message}")
            }
        }
    }

    private suspend fun fetchCityList() {
        when (val response = viewModel.getCities()) {
            is Resource.Success -> {
                response.data?.let {
                    it.forEach { city ->
                        cityList.add(city)
                    }
                }

                val cityNameList = mutableListOf("")
                cityNameList.addAll(
                    cityList.map {
                        it.cityName
                    }
                )

                val arrayAdapter = ArrayAdapter(
                    this,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    cityNameList
                )
                binding.inputOrderAddressCity.adapter = arrayAdapter
                binding.inputOrderAddressCity.setSelection(0)
            }
            else -> {
                Log.d("RDSHOP-DEBUG", "fetchCityList: Error: ${response.message}")
            }
        }
    }

    private fun setBindingDelivery() {
        binding.apply {
            inputOrderAddressCity.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selected =
                            binding.inputOrderAddressCity.adapter.getItem(position).toString()
                        val selectedCity = cityList.find { city -> city.cityName == selected }
                        selectedCity?.let {
                            setVisibilityContent(VISIBLE_DELIVERY)

                            val indexProvince =
                                provinceList.mapIndexed { i, s -> if (s.province == it.province) i else null }
                                    .filterNotNull().toList()
                            if (!indexProvince.isNullOrEmpty()) {
                                binding.inputOrderAddressProvince.setSelection(
                                    indexProvince.first()
                                )
                            }
                            binding.inputOrderAddressPostalCode.editText?.setText(it.postalCode.toString())

                            val origin = cityList.find { city ->
                                city.cityName == Consts.ORIGIN_CITY
                            }?.cityId ?: 0
                            val destination = it.cityId
                            val weight = orderListAdapter.getTotalOrderWeight() * 100
                            val couriers = arrayOf("jne", "tiki", "pos")

                            lifecycleScope.launch {
                                couriers.forEachIndexed { index, courier ->
                                    when (
                                        val response = viewModel.getShippingCost(
                                            origin,
                                            destination,
                                            weight,
                                            courier
                                        )
                                    ) {
                                        is Resource.Success -> {
                                            response.data?.let { cost ->
                                                costs.addAll(cost)

                                                if (index == couriers.size - 1) {
                                                    setVisibilityContent(
                                                        View.VISIBLE,
                                                        isLoading = false,
                                                        isEmpty = false
                                                    )
                                                    setVisibilityContent(VISIBLE_DELIVERY)
                                                }
                                            }
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

            buttonSelectCourier.setOnClickListener {
                val materialDialog = MaterialAlertDialogBuilder(buttonSelectCourier.context)
                val layout = LayoutInflater.from(buttonSelectCourier.context)
                    .inflate(R.layout.dialog_select_courier, null, false)
                val layoutCourier = layout.findViewById<AppCompatSpinner>(R.id.input_courier)
                val layoutDelivery =
                    layout.findViewById<TextInputLayout>(R.id.list_delivery_option)
                layoutDelivery.visibility = View.GONE
                var selectedCost: Cost? = null

                val courierList = arrayOf("", "jne", "pos", "tiki")
                val adapterCourier =
                    ArrayAdapter(
                        layout.context, R.layout.item_list_text,
                        courierList.map {
                            it.uppercase()
                        }
                    )
                layoutCourier.adapter = adapterCourier

                layoutCourier.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedCost = costs.firstOrNull { cost ->
                                cost.code == layoutCourier.adapter.getItem(position).toString()
                                    .lowercase()
                            }
                            selectedCost?.let {
                                it.costs?.let { list ->
                                    val adapter =
                                        DeliveryServiceAdapter(layout.context, list)
                                    (layoutDelivery.editText as? AutoCompleteTextView)?.setAdapter(
                                        adapter
                                    )
                                    layoutDelivery.editText?.setText("")
                                    layoutDelivery.visibility = View.VISIBLE
                                }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }

                (layoutDelivery.editText as? AutoCompleteTextView)?.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        Log.d("RDSHOP-DEBUG", "onItemSelected: SELECTED DELIVERY: $position")
                        selectedCost?.let {
                            selectedServiceCost = it.costs?.get(position)
                            layoutDelivery.editText?.setText(selectedServiceCost?.service)
                        }
                    }

                materialDialog.setView(layout)
                    .setTitle("Pilih Pengiriman")
                    .setMessage("Silahkan Pilih Jasa Pengiriman yang Anda inginkan")
                    .setPositiveButton("Simpan") { dialog, _ ->
                        if (selectedServiceCost != null) {
                            ongkirTotal = selectedServiceCost?.cost?.first()?.value!!
                            ongkirCost.text = ongkirTotal.toRupiah()
                            ongkirEstimation.text =
                                StringBuilder("Estimasi ").append(selectedServiceCost?.cost?.first()?.estimationDay)
                                    .append(" Hari").toString()

                            setVisibilityContent(View.VISIBLE, isLoading = false, isEmpty = false)
                            setVisibilityContent(VISIBLE_PAY)
                            calculateTotal()
                            removeParent(layout)
                            dialog.dismiss()
                        }
                    }
                    .setOnCancelListener {
                        removeParent(layout)
                    }
                    .show()
            }
        }
    }

    private fun setBindingAddress() {
        binding.apply {
            inputOrderName.editText?.setText(user?.name)

            rgAddressChoice.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    rbAddressMy.id -> {
                        user?.address?.let {
                            if (it.split('|').size == 4) {
                                val addressStreetName = it.split('|')[0]
                                val addressCity = it.split('|')[1]
                                val addressProvince = it.split('|')[2]
                                val addressPostalCode = it.split('|')[3]

                                inputOrderAddress.editText?.setText(addressStreetName)
                                addressCity.let {
                                    val index =
                                        cityList.mapIndexed { index, s -> if (s.cityName == addressCity) index else null }
                                            .filterNotNull().toList()
                                    inputOrderAddressCity.setSelection(index.first())
                                }
                                addressProvince.let {
                                    val index =
                                        provinceList.mapIndexed { index, s -> if (s.province == addressProvince) index else null }
                                            .filterNotNull().toList()
                                    inputOrderAddressProvince.setSelection(index.first())
                                }
                                addressPostalCode.let {
                                    inputOrderAddressPostalCode.editText?.setText(
                                        addressPostalCode
                                    )
                                }
                            }
                        }
                        setVisibilityContent(View.VISIBLE, isLoading = false, isEmpty = false)
                        setVisibilityContent(VISIBLE_DELIVERY)
                    }
                    rbAddressCustom.id -> {
                        inputOrderAddress.editText?.setText("")
                        inputOrderAddressCity.setSelection(0)
                        inputOrderAddressProvince.setSelection(0)
                        inputOrderAddressPostalCode.editText?.setText("")
                        setVisibilityContent(View.VISIBLE, isLoading = false, isEmpty = false)
                        setVisibilityContent(VISIBLE_ADDRESS)
                    }
                }
            }
        }
    }

    private fun fetchOrderData() {
        setVisibilityContent(View.GONE, isLoading = true, isEmpty = false)
        val set: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]

        if (!set.isNullOrEmpty()) {
            set.forEach { cart ->
                val currentCartDetail = Gson().fromJson(cart, CartDetail::class.java)
                if (currentCartDetail.isChecked) {
                    viewModel.getProduct(currentCartDetail.productId)
                        .observe(this) { response ->
                            when (response) {
                                is Resource.Success -> {
                                    val product: Product? = response.data
                                    product?.let {
                                        val orderUiState = OrderUiState(
                                            cartDetail = currentCartDetail,
                                            product = product
                                        )

                                        orderListAdapter.insertData(orderUiState)
                                    }
                                }
                                else -> {}
                            }
                        }
                }
            }
            calculateTotal()
        }
    }

    private fun calculateTotal() {
        binding.labelTotalPrice.text = getString(R.string.label_total_price, orderListAdapter.getTotalQuantity())

        binding.orderTotalPrice.text = orderListAdapter.getTotalPrice().toRupiah()
        binding.orderTotalOngkir.text = ongkirTotal.toRupiah()

        val orderTotal = orderListAdapter.getTotalPrice() + ongkirTotal
        binding.orderTotal.text = orderTotal.toRupiah()
    }

    private fun setupListAndAdapter() {
        orderListAdapter = OrderListAdapter()

        binding.apply {
            rvOrderList.setHasFixedSize(true)
            rvOrderList.layoutManager =
                LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
            rvOrderList.adapter = orderListAdapter
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

    private fun setVisibilityContent(visibleType: Int) {
        binding.apply {
            when (visibleType) {
                VISIBLE_LIST -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.GONE
                    layoutOrderDelivery.visibility = View.GONE
                    layoutOrderSummary.visibility = View.GONE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_ADDRESS -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.GONE
                    layoutOrderSummary.visibility = View.GONE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_DELIVERY -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.VISIBLE
                    layoutOrderSummary.visibility = View.GONE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_PAY -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.VISIBLE
                    layoutOrderSummary.visibility = View.VISIBLE
                    layoutBill.visibility = View.VISIBLE
                }
                else -> {
                    throw NotImplementedError("Type $visibleType Not Available!")
                }
            }
        }
    }

    private fun removeParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val VISIBLE_LIST = 1
        private const val VISIBLE_ADDRESS = 2
        private const val VISIBLE_DELIVERY = 3
        private const val VISIBLE_PAY = 4
    }
}
