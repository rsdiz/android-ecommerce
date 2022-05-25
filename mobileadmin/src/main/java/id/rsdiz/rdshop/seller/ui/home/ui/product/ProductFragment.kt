package id.rsdiz.rdshop.seller.ui.home.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.seller.adapter.FooterPagingAdapter
import id.rsdiz.rdshop.seller.adapter.ProductPagingGridAdapter
import id.rsdiz.rdshop.seller.common.LoadStateUi
import id.rsdiz.rdshop.seller.common.ProductItemUiState
import id.rsdiz.rdshop.seller.databinding.FragmentProductBinding
import id.rsdiz.rdshop.seller.ui.home.ui.product.manage.ManageProductActivity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding as FragmentProductBinding

    private val viewModel: ProductViewModel by viewModels()

    @Inject
    lateinit var productPagingAdapter: ProductPagingGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        productPagingAdapter.setOnItemClickListener {
            val directions = ProductFragmentDirections.actionProductFragmentToManageProductActivity(
                ManageProductActivity.TYPE_EDIT,
                it.data
            )
            view.findNavController().navigate(directions)
        }

        binding.refreshProducts.setOnRefreshListener {
            lifecycleScope.launch {
                productPagingAdapter.refresh()
                collectProducts()
                observeProductCount()
                observeProductSoldCount()
                binding.refreshProducts.isRefreshing = false
            }
        }

        setupFabButton()

        lifecycleScope.launch {
            observeProductCount()
            observeProductSoldCount()
        }

        lifecycleScope.launch {
            fetchProducts()
        }
    }

    private suspend fun observeProductCount() {
        binding.textProductCount.text = "Loading..."

        binding.textProductCount.text = when (val resource = viewModel.getProductCount("all")) {
            is Resource.Success -> {
                resource.data?.toString()
            }
            is Resource.Error -> {
                resource.message
            }
            else -> {
                "Loading..."
            }
        }
    }

    private suspend fun observeProductSoldCount() {
        binding.textSoldCount.text = "Loading..."

        binding.textSoldCount.text = when (val resource = viewModel.getProductCount("sold")) {
            is Resource.Success -> {
                resource.data?.toString()
            }
            is Resource.Error -> {
                resource.message
            }
            else -> {
                "Loading..."
            }
        }
    }

    private suspend fun fetchProducts() {
        binding.rvProductList.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter =
                productPagingAdapter.withLoadStateFooter(
                    FooterPagingAdapter(productPagingAdapter::retry)
                )
        }

        collect(
            flow = productPagingAdapter.loadStateFlow
                .distinctUntilChangedBy { it.source.refresh }
                .map { it.refresh },
            action = ::setProductUiState
        )
        collectProducts()
    }

    private suspend fun collectProducts() {
        collectLast(viewModel.getProducts().cancellable(), ::setProducts)
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

    private fun setProductUiState(loadState: LoadState) {
        val ui = LoadStateUi(loadState)
        binding.apply {
            val isVisible = ui.getListVisibility()

            setVisibilityContent(isVisible, ui.getProgressBarVisibility() == View.VISIBLE)

            if (ui.getErrorVisibility() == View.VISIBLE) {
                errorText.text = ui.getErrorMessage()
            }
        }
    }

    private suspend fun setProducts(productItemsPagingData: PagingData<ProductItemUiState>) {
        productPagingAdapter.submitData(productItemsPagingData)
    }

    private fun setupFabButton() {
        binding.apply {
            buttonAddProduct.setOnClickListener {
                val directions =
                    ProductFragmentDirections.actionProductFragmentToManageProductActivity(
                        ManageProductActivity.TYPE_ADD,
                        null
                    )
                view?.findNavController()?.navigate(directions)
            }
        }
    }

    private fun removeParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
