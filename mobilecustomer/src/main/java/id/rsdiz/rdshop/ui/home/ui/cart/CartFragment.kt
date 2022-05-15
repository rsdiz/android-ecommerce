package id.rsdiz.rdshop.ui.home.ui.cart

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.adapter.CartListAdapter
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.common.CartDetailUiState
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.CartDetail
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding as FragmentCartBinding

    private val viewModel: CartViewModel by viewModels()

    private lateinit var prefs: SharedPreferences

    private lateinit var cartListAdapter: CartListAdapter

    private var deletedCart: CartDetailUiState? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefs = PreferenceHelper(requireContext()).customPrefs(Consts.PREFERENCE_NAME)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        lifecycleScope.launch { viewModel.countCategory() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutRefresh.setOnRefreshListener {
            fetchData()
        }

        setupListAndAdapter()

        fetchData()
    }

    private fun fetchData() {
        setVisibilityContent(View.GONE, isLoading = true, isEmpty = false)
        val set: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]

        if (!set.isNullOrEmpty()) {
            val total = set.size
            set.forEachIndexed { index, cart ->
                val currentCartDetail = Gson().fromJson(cart, CartDetail::class.java)

                viewModel.getProduct(currentCartDetail.productId)
                    .observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is Resource.Success -> {
                                val product: Product? = response.data
                                product?.let {
                                    viewModel.getCategory()
                                        .observe(viewLifecycleOwner) { response ->
                                            when (response) {
                                                is Resource.Success -> {
                                                    val category: Category? =
                                                        response.data?.firstOrNull { category ->
                                                            category.categoryId == product.categoryId
                                                        }

                                                    category?.let {
                                                        val cartDetailUiState = CartDetailUiState(
                                                            cartDetail = currentCartDetail,
                                                            product = product,
                                                            category = category
                                                        )

                                                        cartListAdapter.insertData(cartDetailUiState)
                                                    }

                                                    if (index == total - 1)
                                                        setVisibilityContent(View.VISIBLE)
                                                }
                                                else -> {}
                                            }
                                        }
                                }
                            }
                            else -> {}
                        }
                    }
            }
            calculateTotal()
        } else {
            setVisibilityContent(View.GONE, isEmpty = true)
        }

        if (binding.layoutRefresh.isRefreshing) binding.layoutRefresh.isRefreshing = false
    }

    private fun calculateTotal() {
        val set: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]
        var total = 0

        if (!set.isNullOrEmpty()) {
            set.forEach { cart ->
                val currentCartDetail = Gson().fromJson(cart, CartDetail::class.java)
                if (currentCartDetail.isChecked) total += (currentCartDetail.price * currentCartDetail.quantity)
            }
        }

        binding.cartTotal.text = total.toRupiah()
    }

    private fun setupListAndAdapter() {
        cartListAdapter = CartListAdapter()

        binding.apply {
            rvCartList.setHasFixedSize(true)
            rvCartList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvCartList.adapter = cartListAdapter

            val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition

                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            deletedCart = cartListAdapter.getData(position)
                            cartListAdapter.deleteData(position)

                            Snackbar.make(
                                requireContext(),
                                layoutContent,
                                "1 Barang telah dihapus",
                                Snackbar.LENGTH_LONG
                            ).setAction("Batalkan") {
                                deletedCart?.let {
                                    cartListAdapter.insertData(it)
                                    deletedCart = null
                                }
                            }.addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    val set: MutableSet<String> =
                                        prefs[Consts.PREF_CART, mutableSetOf()]

                                    if (!set.isNullOrEmpty()) {
                                        val newSet = set.map { cart ->
                                            val currentCartDetail =
                                                Gson().fromJson(cart, CartDetail::class.java)
                                            deletedCart?.product?.let {
                                                if (currentCartDetail.productId == it.productId) {
                                                    return@map null
                                                }
                                            }
                                            cart
                                        }.filterNotNull().toHashSet()

                                        prefs[Consts.PREF_CART] = newSet
                                        deletedCart = null
                                    }

                                    super.onDismissed(transientBottomBar, event)
                                }
                            }).show()
                        }
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(rvCartList)
        }

        cartListAdapter.setOnItemClickListener {
            val direction = CartFragmentDirections.actionCartFragmentToDetailFragment(it.productId)
            view?.findNavController()?.navigate(direction)
        }

        cartListAdapter.setOnCheckedChangeListener { cartDetail, isChecked ->
            val set: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]
            cartDetail.isChecked = isChecked

            prefs[Consts.PREF_CART] = set.map { cart ->
                val currentCartDetail = Gson().fromJson(cart, CartDetail::class.java)
                if (currentCartDetail.productId == cartDetail.productId) {
                    Gson().toJson(cartDetail)
                } else {
                    Gson().toJson(currentCartDetail)
                }
            }.toHashSet()

            calculateTotal()
        }

        cartListAdapter.setOnQuantityChangeListener { cartDetail, quantity ->
            val set: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]
            cartDetail.quantity = quantity

            prefs[Consts.PREF_CART] = set.map { cart ->
                val currentCartDetail = Gson().fromJson(cart, CartDetail::class.java)
                if (currentCartDetail.productId == cartDetail.productId) {
                    Gson().toJson(cartDetail)
                } else {
                    cart
                }
            }.toHashSet()

            calculateTotal()
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
                layoutEmpty.visibility = View.VISIBLE
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
