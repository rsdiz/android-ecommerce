package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.seller.common.OrderItemUIState
import id.rsdiz.rdshop.seller.databinding.ItemOrderBinding

class NewestOrderAdapter : RecyclerView.Adapter<NewestOrderAdapter.ViewHolder>() {
    private var mutableOrderList = mutableListOf<OrderItemUIState>()
    private var onItemClick: ((OrderItemUIState) -> Unit)? = null

    fun setOrders(orders: List<Order>) {
        with(mutableOrderList) {
            clear()
            orders.map {
                add(OrderItemUIState(it))
            }
        }
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ((OrderItemUIState) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
                onItemClick?.invoke(mutableOrderList[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(orderItemUIState = mutableOrderList[position])

    override fun getItemCount(): Int = mutableOrderList.size
}
