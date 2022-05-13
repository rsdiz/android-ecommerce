package id.rsdiz.rdshop.ui.home.ui.favorite

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
import androidx.paging.filter
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.adapter.FooterPagingAdapter
import id.rsdiz.rdshop.adapter.ProductPagingGridAdapter
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.common.LoadStateUi
import id.rsdiz.rdshop.common.ProductItemUiState
import id.rsdiz.rdshop.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding as FragmentFavoriteBinding

    private val viewModel: FavoriteViewModel by viewModels()

    @Inject
    lateinit var favoriteProductPagingAdapter: ProductPagingGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteProductPagingAdapter.setOnItemClickListener {
            val direction =
                FavoriteFragmentDirections.actionNavigationFavoriteToDetailFragment(it.productId)
            view.findNavController().navigate(direction)
        }

        binding.layoutContent.setOnRefreshListener {
            lifecycleScope.launch {
                favoriteProductPagingAdapter.refresh()
                collectProducts()
                binding.layoutContent.isRefreshing = false
            }
        }

        lifecycleScope.launch {
            fetchProducts()
        }
    }

    private suspend fun fetchProducts() {
        binding.rvProductList.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter =
                favoriteProductPagingAdapter.withLoadStateFooter(
                    FooterPagingAdapter(favoriteProductPagingAdapter::retry)
                )
        }

        collect(
            flow = favoriteProductPagingAdapter.loadStateFlow
                .distinctUntilChangedBy { it.source.refresh }
                .map { it.refresh },
            action = ::setProductUiState
        )
        collectProducts()
    }

    private suspend fun collectProducts() {
        collectLast(
            viewModel.getProducts().map { pagingData ->
                pagingData.filter { product -> product.isFavorite }
            }.cancellable(),
            ::setProducts
        )
    }

    private suspend fun setProducts(productItemsPagingData: PagingData<ProductItemUiState>) {
        favoriteProductPagingAdapter.submitData(productItemsPagingData)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
