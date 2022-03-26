package id.rsdiz.rdshop.seller.ui.home.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.adapter.FooterPagingAdapter
import id.rsdiz.rdshop.seller.adapter.OrderPagingAdapter
import id.rsdiz.rdshop.seller.common.OrderItemUIState
import id.rsdiz.rdshop.seller.common.OrderUiState
import id.rsdiz.rdshop.seller.databinding.FragmentOrderBinding
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding as FragmentOrderBinding

    private val viewModel: OrderViewModel by viewModels()

    private val rotateOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_animation
        )
    }
    private val rotateCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_animation
        )
    }
    private val fromBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_animation
        )
    }
    private val toBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_animation
        )
    }

    @Inject
    lateinit var orderPagingAdapter: OrderPagingAdapter

    private var filterButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        lifecycleScope.launch {
            setupFabFilter()
            setupOrderPagingAdapter()
        }
    }

    private suspend fun setupOrderPagingAdapter() {
        orderPagingAdapter.setOnItemClickListener {
            Toast.makeText(
                requireContext(),
                "ORDER #${it.getSimpleOrderId()} CLICKED!",
                Toast.LENGTH_SHORT
            ).show()
        }

        collect(
            flow = orderPagingAdapter.loadStateFlow
                .distinctUntilChangedBy { it.source.refresh }
                .map { it.refresh },
            action = ::setOrderUiState
        )
        binding.rvOrders.adapter =
            orderPagingAdapter.withLoadStateFooter(
                FooterPagingAdapter(orderPagingAdapter::retry)
            )
        collectLast(viewModel.getOrders(), ::setOrders)
    }

    private fun setOrderUiState(loadState: LoadState) {
        val uiState = OrderUiState(loadState)
        binding.apply {
//            loadingIndicator.visibility = uiState.getProgressBarVisibility()
//            contentOrderGroup.visibility = uiState.getListVisibility()
//            errorOrderGroup.visibility = uiState.getErrorVisibility()
            loadingIndicator.visibility = uiState.getProgressBarVisibility()
            contentOrderGroup.visibility = View.VISIBLE
            errorOrderGroup.visibility = uiState.getErrorVisibility()
        }
    }

    private suspend fun setOrders(orderItemsPagingData: PagingData<OrderItemUIState>) {
        orderPagingAdapter.submitData(orderItemsPagingData)
    }

    private fun setupFabFilter() {
        binding.apply {
            fabFilter.setOnClickListener {
                onFabFilterClicked()
            }
            fabFilter1.setOnClickListener {
            }
            fabFilter2.setOnClickListener {
            }
            fabFilter3.setOnClickListener {
            }
            fabFilter4.setOnClickListener {
            }
        }
    }

    private fun onFabFilterClicked() {
        setFabFilterVisibility(filterButtonClicked)
        setFabFilterAnimation(filterButtonClicked)
        fabButtonSetClickable()

        filterButtonClicked = !filterButtonClicked
    }

    private fun setFabFilterVisibility(buttonClicked: Boolean) {
        binding.apply {
            if (!buttonClicked) {
                fabFilter1.visibility = View.VISIBLE
                fabFilter2.visibility = View.VISIBLE
                fabFilter3.visibility = View.VISIBLE
                fabFilter4.visibility = View.VISIBLE
            } else {
                fabFilter1.visibility = View.INVISIBLE
                fabFilter2.visibility = View.INVISIBLE
                fabFilter3.visibility = View.INVISIBLE
                fabFilter4.visibility = View.INVISIBLE
            }
        }
    }

    private fun setFabFilterAnimation(buttonClicked: Boolean) {
        binding.apply {
            if (!buttonClicked) {
                fabFilter1.startAnimation(fromBottomAnimation)
                fabFilter2.startAnimation(fromBottomAnimation)
                fabFilter3.startAnimation(fromBottomAnimation)
                fabFilter4.startAnimation(fromBottomAnimation)
                fabFilter.startAnimation(rotateOpenAnimation)
            } else {
                fabFilter1.startAnimation(toBottomAnimation)
                fabFilter2.startAnimation(toBottomAnimation)
                fabFilter3.startAnimation(toBottomAnimation)
                fabFilter4.startAnimation(toBottomAnimation)
                fabFilter.startAnimation(rotateCloseAnimation)
            }
        }
    }

    private fun fabButtonSetClickable() {
        binding.apply {
            if (!filterButtonClicked) {
                fabFilter1.isClickable = true
                fabFilter2.isClickable = true
                fabFilter3.isClickable = true
                fabFilter4.isClickable = true
            } else {
                fabFilter1.isClickable = false
                fabFilter2.isClickable = false
                fabFilter3.isClickable = false
                fabFilter4.isClickable = false
            }
        }
    }
}
