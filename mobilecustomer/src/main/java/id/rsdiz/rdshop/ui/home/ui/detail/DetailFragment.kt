package id.rsdiz.rdshop.ui.home.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.adapter.ProductImageSliderAdapter
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.base.utils.toRupiah
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // TODO: Set Action for Button

        lifecycleScope.launch {
            fetchProductData(dataArgs.productId)
        }
    }

    private suspend fun fetchProductData(productId: String) {
        collectLast(viewModel.getProduct(productId = productId)) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        productImageSliderAdapter = ProductImageSliderAdapter(it.image)
                        setProductUi(it)
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

            viewModel.getCategories().observe(viewLifecycleOwner) { resource ->
                if (resource is Resource.Success) {
                    resource.data?.firstOrNull { category ->
                        category.categoryId == product.categoryId
                    }?.let {
                        productCategory.text = it.name
                        Log.d("RDSHOP-DEBUG", "setProductUi: Category = ${it.name}")
                    }
                }
            }

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
