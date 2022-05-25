package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.seller.common.OrderItemUIState
import id.rsdiz.rdshop.seller.databinding.ItemOrderBinding
import javax.inject.Inject

class OrderPagingAdapter @Inject constructor() :
    PagingDataAdapter<OrderItemUIState, OrderPagingAdapter.OrderViewHolder>(Comparator) {
    private var onItemClick: ((OrderItemUIState) -> Unit)? = null

    fun setOnItemClickListener(listener: ((OrderItemUIState) -> Unit)) {
        onItemClick = listener
    }

    fun countProductUnprocessed(): Int {
        var total = 0
        repeat(itemCount) {
            val order = getItem(it)
            if (order?.getOrderStatusKey() == 0.toShort()) {
                total++
            }
        }
        return total
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItemUIState: OrderItemUIState) {
            binding.apply {
                itemTotal.text = orderItemUIState.getOrderTotal()
                itemOrderId.text = "Order: #${orderItemUIState.getSimpleOrderId()}"
                itemOrderYear.text = orderItemUIState.getOrderYear()
                itemOrderDate.text = orderItemUIState.getOrderDate()
                itemOrderMonth.text = orderItemUIState.getOrderMonth()
                itemName.text = orderItemUIState.getOrderName()
                itemStatus.text = orderItemUIState.getOrderStatusValue()
            }
        }

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { orderItem ->
                    onItemClick?.invoke(orderItem)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        getItem(position)?.let { orderItemUIState -> holder.bind(orderItemUIState) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    object Comparator : DiffUtil.ItemCallback<OrderItemUIState>() {
        override fun areItemsTheSame(
            oldItem: OrderItemUIState,
            newItem: OrderItemUIState
        ): Boolean = oldItem.getSimpleOrderId() == newItem.getSimpleOrderId()

        override fun areContentsTheSame(
            oldItem: OrderItemUIState,
            newItem: OrderItemUIState
        ): Boolean = oldItem == newItem
    }
}
