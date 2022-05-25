package id.rsdiz.rdshop.seller.ui.home.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.adapter.FooterPagingAdapter
import id.rsdiz.rdshop.seller.adapter.OrderPagingAdapter
import id.rsdiz.rdshop.seller.common.LoadStateUi
import id.rsdiz.rdshop.seller.common.OrderItemUIState
import id.rsdiz.rdshop.seller.databinding.FragmentOrderBinding
import kotlinx.coroutines.flow.cancellable
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
    private val fromBottomAnimationClear: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_animation
        )
    }
    private val toBottomAnimationClear: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_animation
        )
    }

    @Inject
    lateinit var orderPagingAdapter: OrderPagingAdapter

    private var filterButtonClicked = false
    private var isFilterActive: Boolean? = false

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
        setVisibilityContent(View.GONE, true)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.content.setOnRefreshListener {
            lifecycleScope.launch {
                collectOrders()
                binding.content.isRefreshing = false
            }
        }

        lifecycleScope.launch {
            setupFabFilter()
            setupOrderPagingAdapter()
            updateNotification()
        }
    }

    private fun updateNotification() {
        if (orderPagingAdapter.countProductUnprocessed() < 1) {
            binding.notification.visibility = View.GONE
        } else {
            binding.notification.visibility = View.VISIBLE
            binding.notification.text = getString(
                R.string.notif_unprocessed,
                orderPagingAdapter.countProductUnprocessed().toString()
            )
        }
    }

    private suspend fun setupOrderPagingAdapter() {
        orderPagingAdapter.setOnItemClickListener {
            val directions = OrderFragmentDirections.actionOrderFragmentToDetailOrderFragment(
                it.getOriginalOrderId()
            )
            view?.findNavController()?.navigate(directions)
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
        collectOrders()
    }

    private suspend fun collectOrders(status: Short? = null) {
        collectLast(viewModel.getOrders(status).cancellable(), ::setOrders)
    }

    private fun setOrderUiState(loadState: LoadState) {
        val ui = LoadStateUi(loadState)
        binding.apply {
            val isVisible = ui.getListVisibility()

            setVisibilityContent(isVisible, ui.getProgressBarVisibility() == View.VISIBLE)

            if (ui.getErrorVisibility() == View.VISIBLE) {
                errorText.text = ui.getErrorMessage()
            }

            updateNotification()
        }
    }

    private fun setVisibilityContent(visibility: Int, isLoading: Boolean = false) {
        binding.apply {
            layoutContent.visibility = visibility
            when (visibility) {
                View.VISIBLE -> {
                    layoutLoading.visibility = View.GONE
                    layoutError.visibility = View.GONE
                }
                View.GONE -> {
                    fabClearFilter.visibility = View.GONE
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

    private suspend fun setOrders(orderItemsPagingData: PagingData<OrderItemUIState>) {
        orderPagingAdapter.submitData(orderItemsPagingData)
        if (isFilterActive == null) isFilterActive = true
        setFabClearFilter()
    }

    private fun setupFabFilter() {
        binding.apply {
            fabFilter.setOnClickListener {
                onFabFilterClicked()
            }
            fabFilter1.setOnClickListener {
                lifecycleScope.launch {
                    collectOrders(Consts.STATUS_WAITING)
                    onFabFilterClicked()
                    isFilterActive = null
                }
            }
            fabFilter2.setOnClickListener {
                lifecycleScope.launch {
                    collectOrders(Consts.STATUS_PROCESSED)
                    onFabFilterClicked()
                    isFilterActive = null
                }
            }
            fabFilter3.setOnClickListener {
                lifecycleScope.launch {
                    collectOrders(Consts.STATUS_DISPATCH)
                    onFabFilterClicked()
                    isFilterActive = null
                }
            }
            fabFilter4.setOnClickListener {
                lifecycleScope.launch {
                    collectOrders(Consts.STATUS_ARRIVED)
                    onFabFilterClicked()
                    isFilterActive = null
                }
            }
            fabClearFilter.setOnClickListener {
                lifecycleScope.launch {
                    collectOrders()
                    isFilterActive = false
                }
            }
        }
    }

    private fun onFabFilterClicked() {
        setFabFilterVisibility(filterButtonClicked)
        setFabFilterAnimation(filterButtonClicked)
        fabButtonSetClickable()

        filterButtonClicked = !filterButtonClicked
    }

    private fun setFabClearFilter() {
        binding.apply {
            if (isFilterActive == true) {
                fabClearFilter.visibility = View.VISIBLE
                fabClearFilter.startAnimation(fromBottomAnimationClear)
            } else {
                fabClearFilter.visibility = View.INVISIBLE
                fabClearFilter.startAnimation(toBottomAnimationClear)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
