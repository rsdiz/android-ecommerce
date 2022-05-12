package id.rsdiz.rdshop.ui.home.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.adapter.FooterPagingAdapter
import id.rsdiz.rdshop.adapter.ProductPagingGridAdapter
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.common.LoadStateUi
import id.rsdiz.rdshop.common.ProductItemUiState
import id.rsdiz.rdshop.databinding.FragmentHomeBinding
import id.rsdiz.rdshop.ui.auth.AuthFragmentDirections
import id.rsdiz.rdshop.ui.home.IOnBackPressed
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), IOnBackPressed {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var productPagingAdapter: ProductPagingGridAdapter

    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productPagingAdapter.setOnItemClickListener {
            val direction = HomeFragmentDirections.actionNavigationHomeToDetailFragment(it.productId)
            view.findNavController().navigate(direction)
        }

        binding.fabToTop.visibility = View.GONE

        binding.content.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY + 12) binding.fabToTop.hide()
            if (scrollY < oldScrollY - 12) binding.fabToTop.show()
            if (scrollY == 0) binding.fabToTop.hide()
        }

        binding.layoutContent.setOnRefreshListener {
            lifecycleScope.launch {
                productPagingAdapter.refresh()
                collectProducts()
                binding.layoutContent.isRefreshing = false
            }
        }

        lifecycleScope.launch {
            setupFabButton()
            setupSearchFeature()
            fetchProducts()
        }
    }

    private fun setupSearchFeature() {
        binding.searchProduct.apply {
            val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            setIconifiedByDefault(false)
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        lifecycleScope.launch {
                            isSearching = true
                        }
                    }

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean = false
            })

            setOnCloseListener {
                onActionViewCollapsed()
                isSearching = false
                true
            }
        }
    }

    private fun setupFabButton() {
        binding.apply {
            fabToTop.setOnClickListener {
                val positionY = binding.rvProductList.getChildAt(0).y
                binding.content.smoothScrollTo(0, positionY.toInt(), 1000)
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
        binding.fabToTop.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onBackPressed(): Boolean {
        binding.apply {
            return when {
                searchProduct.query.isNotEmpty() -> {
                    searchProduct.setQuery("", false)
                    searchProduct.clearFocus()
                    true
                }
                isSearching -> {
                    lifecycleScope.launch {
                        fetchProducts()
                        isSearching = false
                    }.isCompleted
                }
                else -> false
            }
        }
    }
}
