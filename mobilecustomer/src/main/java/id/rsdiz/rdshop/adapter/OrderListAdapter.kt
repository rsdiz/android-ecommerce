package id.rsdiz.rdshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.common.OrderItemUIState
import id.rsdiz.rdshop.databinding.ItemOrderBinding

class OrderListAdapter(private val data: List<OrderItemUIState>) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    private var onItemClick: ((OrderItemUIState) -> Unit)? = null

    fun setOnItemClickListener(listener: ((OrderItemUIState) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItemUIState: OrderItemUIState) {
            binding.apply {
                itemTotal.text = orderItemUIState.getOrderTotal()
                itemOrderId.text = StringBuilder("Order: #").append(orderItemUIState.getSimpleOrderId()).toString()
                itemOrderYear.text = orderItemUIState.getOrderYear()
                itemOrderDate.text = orderItemUIState.getOrderDate()
                itemOrderMonth.text = orderItemUIState.getOrderMonth()
                itemName.text = orderItemUIState.getOrderName()
                itemStatus.text = orderItemUIState.getOrderStatusValue()
            }
        }

        init {
            binding.root.setOnClickListener {
                data[bindingAdapterPosition].let { orderItem ->
                    onItemClick?.invoke(orderItem)
                }
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
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}
