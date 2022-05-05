package id.rsdiz.rdshop.ui.home.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import id.rsdiz.rdshop.adapter.ProductImageSliderAdapter
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

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
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        productImageSliderAdapter = ProductImageSliderAdapter(it.image)
                        setProductUi(it)
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

    private fun setProductUi(product: Product) {
        binding.apply {
            imageList.adapter = productImageSliderAdapter
            TabLayoutMediator(indicatorImageSlider, imageList) { _, _ ->
            }.attach()

            productTitle.text = product.name
            productPrice.text = product.price.toRupiah()
            product.categoryId.let { categoryId ->
                viewModel.getCategories(categoryId).observe(viewLifecycleOwner) { category ->
                    category?.let { productCategory.text = it.name }
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
