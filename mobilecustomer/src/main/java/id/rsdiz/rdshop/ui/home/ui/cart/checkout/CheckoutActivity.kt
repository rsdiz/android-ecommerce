package id.rsdiz.rdshop.ui.home.ui.cart.checkout

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.PaymentMethod
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.adapter.CheckoutListAdapter
import id.rsdiz.rdshop.adapter.DeliveryServiceAdapter
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.common.CheckoutUiState
import id.rsdiz.rdshop.common.OrderItemUIState
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.*
import id.rsdiz.rdshop.databinding.ActivityCheckoutBinding
import id.rsdiz.rdshop.ui.home.HomeActivity
import id.rsdiz.rdshop.ui.home.ui.profile.order.OrderHistoryActivity
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import java.util.*

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity(), TransactionFinishedCallback {
    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding as ActivityCheckoutBinding

    private lateinit var prefs: SharedPreferences

    private val viewModel: CheckoutViewModel by viewModels()

    private lateinit var checkoutListAdapter: CheckoutListAdapter

    private var ongkirTotal = 0
    private var user: User? = null
    private var selectedCost: Cost? = null
    private var selectedServiceCost: ServiceCost? = null
    private var costs = mutableListOf<Cost>()
    private var cityList = mutableListOf<City>()
    private var provinceList = mutableListOf<Province>()
    private var selectedPaymentMethod = -1
    private var orderTotal: Int = 0
    private var selectedCity: City? = null
    private var selectedProvince: Province? = null
    private var postalCode: String = ""
    private var listOrderDetail = mutableListOf<OrderDetail>()

    private lateinit var currentUUID: String
    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceHelper(this).customPrefs(Consts.PREFERENCE_NAME)
        currentUUID = UUID.randomUUID().toString()

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setupListAndAdapter()
        lifecycleScope.launch {
            initMidtransSdk()
            fetchOrderData()
        }

        lifecycleScope.launch {
            fetchCityList()
            fetchProvinceList()
            fetchUserData()
            setBindingAddress()
            setBindingDelivery()
            setBindingPaymentMethod()
        }
    }

    private fun initMidtransSdk() {
        SdkUIFlowBuilder.init()
            .setContext(this)
            .setClientKey(Consts.MIDTRANS_API_KEY)
            .setMerchantBaseUrl(Consts.RDSHOP_MERCHANT_URL)
            .setTransactionFinishedCallback(this)
            .enableLog(true)
            .setColorTheme(
                CustomColorTheme(
                    "#FF028AC4",
                    "#38B5E2",
                    "#FFEEEEEE"
                )
            ).buildSDK()
    }

    private fun setBindingPaymentMethod() {
        binding.apply {
            val paymentLayout = listPaymentMethod.editText as? AutoCompleteTextView
            val paymentMethodList = resources.getStringArray(R.array.payment_method).toList()
            paymentLayout?.apply {
                setAdapter(
                    ArrayAdapter(
                        listPaymentMethod.context,
                        R.layout.item_list_text,
                        paymentMethodList
                    )
                )

                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    selectedPaymentMethod = position
                    setVisibilityContent(VISIBLE_PAY)
                }
            }
            buttonPay.setOnClickListener {
                resetErrorView()
                var countError = 0

                if (binding.inputOrderName.editText?.text.isNullOrEmpty()) {
                    binding.inputOrderName.apply {
                        isErrorEnabled = true
                        error = "Nama Harus diisi!"
                        countError++
                    }
                }

                if (binding.inputOrderPhone.editText?.text.isNullOrEmpty()) {
                    binding.inputOrderPhone.apply {
                        isErrorEnabled = true
                        error = "Nomor Telepon Harus diisi!"
                        countError++
                    }
                }

                if (selectedCity?.cityName.isNullOrEmpty()) {
                    countError++
                }

                if (selectedProvince?.province.isNullOrEmpty()) {
                    countError++
                }

                if (postalCode.isEmpty()) {
                    binding.inputOrderAddressPostalCode.apply {
                        isErrorEnabled = true
                        error = "Kode Pos harus diisi!"
                        countError++
                    }
                }

                if (countError == 0 && selectedPaymentMethod != -1) {
                    val courier =
                        StringBuilder("|")
                            .append(selectedCost?.code)
                            .append("|")
                            .append(selectedServiceCost?.service)
                            .toString()

                    order = Order(
                        orderId = currentUUID,
                        userId = prefs[Consts.PREF_ID],
                        date = OffsetDateTime.now(),
                        amount = this@CheckoutActivity.orderTotal,
                        shipName = binding.inputOrderName.editText?.text.toString(),
                        shipAddress = getShippingAddress(),
                        shippingCost = ongkirTotal,
                        phone = binding.inputOrderPhone.editText?.text.toString(),
                        status = 0,
                        trackingNumber = courier,
                        orderDetail = listOrderDetail
                    )

                    val paymentMethod = when (selectedPaymentMethod) {
                        0 -> PaymentMethod.CREDIT_CARD
                        1 -> PaymentMethod.BANK_TRANSFER
                        2 -> PaymentMethod.GO_PAY
                        3 -> PaymentMethod.KLIKBCA
                        4 -> PaymentMethod.MANDIRI_CLICKPAY
                        5 -> PaymentMethod.EPAY_BRI
                        6 -> PaymentMethod.INDOMARET
                        7 -> PaymentMethod.ALFAMART
                        8 -> PaymentMethod.SHOPEEPAY
                        else -> null
                    }

                    MidtransSDK.getInstance().transactionRequest =
                        initTransactionRequest(OrderItemUIState(order!!).getSimpleOrderId())
                    MidtransSDK.getInstance()
                        .startPaymentUiFlow(this@CheckoutActivity, paymentMethod)
                } else {
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Form belum lengkap! Silahkan Cek Kembali Data yang Anda masukkan!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getShippingAddress(): String =
        StringBuilder(binding.inputOrderName.editText?.text.toString())
            .append("|")
            .append(binding.inputOrderAddress.editText?.text.toString())
            .append("|")
            .append(selectedCity?.cityName)
            .append("|")
            .append(selectedProvince?.province)
            .append("|")
            .append(postalCode)
            .toString()

    private fun resetErrorView() {
        binding.inputOrderName.apply {
            isErrorEnabled = false
            error = ""
        }

        binding.inputOrderPhone.apply {
            isErrorEnabled = false
            error = ""
        }

        binding.inputOrderAddressPostalCode.apply {
            isErrorEnabled = false
            error = ""
        }
    }

    private fun initTransactionRequest(orderId: String = currentUUID): TransactionRequest {
        val transactionRequest =
            TransactionRequest(orderId, orderTotal.toDouble())
        transactionRequest.customerDetails = getCustomerDetails()
        return transactionRequest
    }

    private fun getCustomerDetails(): CustomerDetails {
        val billingAddress = BillingAddress()
        val shippingAddress = ShippingAddress()

        binding.inputOrderAddress.editText?.text.toString().let {
            billingAddress.address = it
            shippingAddress.address = it
        }
        selectedCity?.cityName.let {
            billingAddress.city = it
            shippingAddress.city = it
        }
        postalCode.let {
            billingAddress.postalCode = it
            shippingAddress.postalCode = it
        }
        binding.inputOrderPhone.editText?.text.toString().let {
            billingAddress.phone = it
            shippingAddress.phone = it
        }
        billingAddress.countryCode = "IDN"
        shippingAddress.countryCode = "IDN"

        val customer = CustomerDetails()
        customer.firstName = prefs[Consts.PREF_NAME]
        customer.email = prefs[Consts.PREF_EMAIL]
        customer.phone = binding.inputOrderPhone.editText?.text.toString()
        customer.customerIdentifier = prefs[Consts.PREF_ID]
        customer.billingAddress = billingAddress
        customer.shippingAddress = shippingAddress

        return customer
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
            else -> {}
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
            else -> {}
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
                        selectedCity = cityList.find { city -> city.cityName == selected }
                        selectedCity?.let {
                            setVisibilityContent(VISIBLE_DELIVERY)

                            val indexProvince =
                                provinceList.mapIndexed { i, s ->
                                    if (s.province == it.province) {
                                        selectedProvince = s
                                        i
                                    } else null
                                }
                                    .filterNotNull().toList()
                            if (!indexProvince.isNullOrEmpty()) {
                                binding.inputOrderAddressProvince.setSelection(
                                    indexProvince.first().plus(1)
                                )
                            }

                            postalCode = it.postalCode.toString()
                            binding.inputOrderAddressPostalCode.editText?.setText(it.postalCode.toString())

                            val origin = cityList.find { city ->
                                city.cityName == Consts.ORIGIN_CITY
                            }?.cityId ?: 0
                            val destination = it.cityId
                            val weight = checkoutListAdapter.getTotalOrderWeight() * 100
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
                            setVisibilityContent(VISIBLE_PAYMENT_METHOD)
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

                                inputOrderName.editText?.setText(user?.name)
                                inputOrderAddress.editText?.setText(addressStreetName)
                                addressCity.let {
                                    val index =
                                        cityList.mapIndexed { index, s ->
                                            if (s.cityName == addressCity) {
                                                selectedCity = s
                                                index
                                            } else null
                                        }
                                            .filterNotNull().toList()
                                    if (index.isNotEmpty()) inputOrderAddressCity.setSelection(index.first().plus(1))
                                    else
                                        Toast.makeText(
                                            rgAddressChoice.context,
                                            "Gagal Mengambil Data Kota! Silahkan ulangi kembali nanti!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }
                                addressProvince.let {
                                    val index =
                                        provinceList.mapIndexed { index, s ->
                                            if (s.province == addressProvince) {
                                                selectedProvince = s
                                                index
                                            } else null
                                        }
                                            .filterNotNull().toList()
                                    if (index.isNotEmpty()) inputOrderAddressProvince.setSelection(
                                        index.first().plus(1)
                                    )
                                    else
                                        Toast.makeText(
                                            rgAddressChoice.context,
                                            "Gagal Mengambil Data Provinsi! Silahkan ulangi kembali nanti!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }

                                addressPostalCode.let {
                                    postalCode = addressPostalCode
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
                                        val orderUiState = CheckoutUiState(
                                            cartDetail = currentCartDetail,
                                            product = product
                                        )

                                        listOrderDetail.add(
                                            OrderDetail(
                                                detailId = UUID.randomUUID().toString(),
                                                productId = product.productId,
                                                price = currentCartDetail.price,
                                                quantity = currentCartDetail.quantity
                                            )
                                        )
                                        checkoutListAdapter.insertData(orderUiState)
                                    }
                                }
                                else -> {}
                            }
                        }
                }
            }
        }
    }

    private fun calculateTotal() {
        binding.labelTotalPrice.text =
            getString(R.string.label_total_price, checkoutListAdapter.getTotalQuantity())

        binding.orderTotalPrice.text = checkoutListAdapter.getTotalPrice().toRupiah()
        binding.orderTotalOngkir.text = ongkirTotal.toRupiah()

        orderTotal = checkoutListAdapter.getTotalPrice() + ongkirTotal
        binding.orderTotal.text = orderTotal.toRupiah()
    }

    private fun setupListAndAdapter() {
        checkoutListAdapter = CheckoutListAdapter()

        binding.apply {
            rvOrderList.setHasFixedSize(true)
            rvOrderList.layoutManager =
                LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
            rvOrderList.adapter = checkoutListAdapter
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
                    layoutPaymentMethod.visibility = View.GONE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_ADDRESS -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.GONE
                    layoutOrderSummary.visibility = View.GONE
                    layoutPaymentMethod.visibility = View.GONE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_DELIVERY -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.VISIBLE
                    layoutOrderSummary.visibility = View.GONE
                    layoutPaymentMethod.visibility = View.GONE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_PAYMENT_METHOD -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.VISIBLE
                    layoutOrderSummary.visibility = View.VISIBLE
                    layoutPaymentMethod.visibility = View.VISIBLE
                    layoutBill.visibility = View.GONE
                }
                VISIBLE_PAY -> {
                    layoutOrderDetail.visibility = View.VISIBLE
                    layoutOrderAddress.visibility = View.VISIBLE
                    layoutOrderDelivery.visibility = View.VISIBLE
                    layoutOrderSummary.visibility = View.VISIBLE
                    layoutPaymentMethod.visibility = View.VISIBLE
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
        private const val VISIBLE_PAYMENT_METHOD = 4
        private const val VISIBLE_PAY = 5
    }

    override fun onTransactionFinished(result: TransactionResult?) {
        setVisibilityContent(View.GONE, isLoading = true)
        val materialDialog = MaterialAlertDialogBuilder(binding.root.context)

        val oldCart: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]

        if (result != null && result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    lifecycleScope.launch {
                        when (val response = viewModel.createOrder(orderRequest = order!!)) {
                            is Resource.Success -> {
                                setVisibilityContent(View.VISIBLE, isLoading = false)
                                prefs[Consts.PREF_CART] = oldCart.mapNotNull { cart ->
                                    val currentCartDetail =
                                        Gson().fromJson(cart, CartDetail::class.java)
                                    if (currentCartDetail.isChecked) null
                                    else cart
                                }.toHashSet()

                                materialDialog.setTitle("Transaksi Berhasil!")
                                    .setMessage("Pesanan anda telah berhasil dibuat! Admin akan segera mengirimkan pesanan Anda!")
                                    .setNegativeButton("Produk Lain") { dialog, _ ->
                                        startActivity(
                                            Intent(
                                                this@CheckoutActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                        dialog.dismiss()
                                    }
                                    .setPositiveButton("Kembali") { dialog, _ ->
                                        onBackPressed()
                                        dialog.dismiss()
                                    }
                                    .setOnDismissListener {
                                    }.show()
                            }
                            else -> {
                                Log.d(
                                    "RDSHOP-DEBUG",
                                    "onTransactionFinished: ERROR INSERT ORDER! Cause: ${response.message}"
                                )
                            }
                        }
                    }
                }
                TransactionResult.STATUS_FAILED -> {
                    setVisibilityContent(View.VISIBLE, isLoading = false)
                    materialDialog.setTitle("Transaksi Gagal!")
                        .setMessage(result.statusMessage.toString())
                        .setPositiveButton("Kembali") { dialog, _ ->
                            (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                            setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                            setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                        }.show()
                }
                TransactionResult.STATUS_PENDING -> {
                    setVisibilityContent(View.VISIBLE, isLoading = false)
                    order = Order(
                        orderId = order?.orderId ?: currentUUID,
                        userId = order?.userId ?: prefs[Consts.PREF_ID],
                        date = order?.date ?: OffsetDateTime.now(),
                        amount = order?.amount ?: orderTotal,
                        shipName = order?.shipName
                            ?: binding.inputOrderName.editText?.text.toString(),
                        shipAddress = order?.shipAddress ?: getShippingAddress(),
                        shippingCost = order?.shippingCost ?: ongkirTotal,
                        phone = order?.phone ?: binding.inputOrderPhone.editText?.text.toString(),
                        status = -1,
                        trackingNumber = order?.trackingNumber,
                        orderDetail = order?.orderDetail ?: listOrderDetail
                    )

                    lifecycleScope.launch {
                        when (val response = viewModel.createOrder(orderRequest = order!!)) {
                            is Resource.Success -> {
                                setVisibilityContent(View.VISIBLE, isLoading = false)
                                prefs[Consts.PREF_CART] = oldCart.mapNotNull { cart ->
                                    val currentCartDetail =
                                        Gson().fromJson(cart, CartDetail::class.java)
                                    if (currentCartDetail.isChecked) null
                                    else cart
                                }.toHashSet()
                                materialDialog.setTitle("Transaksi Belum Selesai!")
                                    .setMessage(result.statusMessage.toString())
                                    .setPositiveButton("Lihat Order") { dialog, _ ->
                                        startActivity(Intent(this@CheckoutActivity, OrderHistoryActivity::class.java))
                                        finish()
                                        dialog.dismiss()
                                    }
                                    .setOnDismissListener {
                                        startActivity(
                                            Intent(
                                                this@CheckoutActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }.show()
                            }
                            else -> {
                                Log.d(
                                    "RDSHOP-DEBUG",
                                    "onTransactionFinished: ERROR INSERT ORDER! Cause: ${response.message}"
                                )
                            }
                        }
                    }
                }
                TransactionResult.STATUS_INVALID -> {
                    setVisibilityContent(View.VISIBLE, isLoading = false)
                    materialDialog.setTitle("Transaksi Gagal!")
                        .setMessage(result.statusMessage.toString())
                        .setPositiveButton("Kembali") { dialog, _ ->
                            (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                            setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                            setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                        }.show()
                }
            }
        } else if (result?.isTransactionCanceled == true) {
            setVisibilityContent(View.VISIBLE, isLoading = false)
            materialDialog.setTitle("Transaksi dibatalkan!")
                .setMessage(result.statusMessage.toString())
                .setPositiveButton("OK") { dialog, _ ->
                    (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                    setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                    setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                }.show()
        } else {
            setVisibilityContent(View.VISIBLE, isLoading = false)
            materialDialog.setTitle("Transaksi Gagal!")
                .setMessage(result?.statusMessage.toString())
                .setPositiveButton("OK") { dialog, _ ->
                    (binding.listPaymentMethod.editText as? AutoCompleteTextView)?.clearListSelection()
                    setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    setVisibilityContent(VISIBLE_PAYMENT_METHOD)
                }.show()
        }
    }
}
