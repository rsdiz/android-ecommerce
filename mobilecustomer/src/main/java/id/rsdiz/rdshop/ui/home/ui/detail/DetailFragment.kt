package id.rsdiz.rdshop.ui.home.ui.detail

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.adapter.ProductImageSliderAdapter
import id.rsdiz.rdshop.base.utils.*
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding as FragmentDetailBinding

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var productImageSliderAdapter: ProductImageSliderAdapter

    private val dataArgs get() = DetailFragmentArgs.fromBundle(arguments as Bundle)

    private lateinit var prefs: SharedPreferences

    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefs = PreferenceHelper(requireContext()).customPrefs(Consts.PREFERENCE_NAME)
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonAddToCart.setOnClickListener {
            val set: MutableSet<String> = prefs[Consts.PREF_CART, mutableSetOf()]
            if (set.firstOrNull { productId -> productId == product.productId } == null) {
                set.add(product.productId)
                prefs[Consts.PREF_CART] = set
                Toast.makeText(
                    context,
                    "Produk ini berhasil ditambahkan ke keranjang!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Produk ini sudah ditambahkan ke keranjang!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launch {
            fetchProductData(dataArgs.productId)
            fetchCategories()
        }
    }

    private suspend fun fetchCategories() {
        viewModel.countCategories()
        collectLast(viewModel.getCategories()) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.firstOrNull { category -> category.categoryId == product.categoryId }
                        ?.let {
                            binding.productCategory.text = it.name
                            Log.d("RDSHOP-DEBUG", "setProductUi: Category = ${it.name}")
                        }
                }
                is Resource.Error -> {
                    Log.d("RDSHOP-DEBUG", "setProductUi: ERROR, cause: ${response.message}")
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private suspend fun fetchProductData(productId: String) {
        collectLast(viewModel.getProduct(productId = productId)) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        product = it
                        productImageSliderAdapter = ProductImageSliderAdapter(it.image)
                        setProductUi(product)
                        setVisibilityContent(View.VISIBLE)
                    }
                }
                is Resource.Loading -> {
                    setVisibilityContent(View.GONE, true)
                }
                else -> {
                    setVisibilityContent(View.GONE, false)
                    binding.errorText.text = response.message ?: ""
                }
            }
        }
    }

    private fun setFavoriteState(isFavorite: Boolean) {
        binding.toolbar.menu[0].let {
            if (isFavorite) {
                it.setIcon(R.drawable.ic_baseline_favorite_24)
                it.title = "Remove from Favorite"
            } else {
                it.setIcon(R.drawable.ic_baseline_favorite_border_24)
                it.title = "Add To Favorite"
            }
        }
    }

    private suspend fun setProductUi(product: Product) {
        binding.apply {
            imageList.adapter = productImageSliderAdapter
            TabLayoutMediator(indicatorImageSlider, imageList) { _, _ ->
            }.attach()

            setFavoriteState(product.isFavorite)
            binding.toolbar.menu[0].setOnMenuItemClickListener { _ ->
                lifecycleScope.launch {
                    viewModel.switchFavorite(productId = product.productId)
                }
                true
            }

            productTitle.text = product.name
            productPrice.text = product.price.toRupiah()
            productStock.text = StringBuilder("Stok: ").append(product.stock).toString()

            productDescription.text = product.description
        }
    }

    private fun setVisibilityContent(visibility: Int, isLoading: Boolean = false) {
        binding.apply {
            layoutContent.visibility = visibility
            layoutContentAction.visibility = visibility

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
