package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.seller.databinding.ItemOrderBinding

class NewestOrderAdapter : RecyclerView.Adapter<NewestOrderAdapter.ViewHolder>() {
    private var mutableOrderList = mutableListOf<Order>()
    private var onItemClick: ((Order) -> Unit)? = null

    fun setOrders(orders: List<Order>) {
        with(mutableOrderList) {
            clear()
            addAll(orders)
        }
        notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: ((Order) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                itemTotal.text = "${order.amount.toRupiah()},-"
                itemOrderId.text = "Order: #${order.orderId.split('-')[4]}"
                itemOrderYear.text = order.date.year.toString()
                itemOrderDate.text = order.date.dayOfMonth.toString()
                itemOrderMonth.text = order.date.month.toString().slice(0..2)
                itemName.text = order.shipName
                itemStatus.text = when (order.status) {
                    0.toShort() -> "Menunggu\nKonfirmasi"
                    1.toShort() -> "Pesanan\nDiproses"
                    2.toShort() -> "Sedang\nDikirim"
                    3.toShort() -> "Sampai\nTujuan"
                    else -> "Tidak\nDiketahui"
                }
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
        holder.bind(order = mutableOrderList[position])

    override fun getItemCount(): Int = mutableOrderList.size
}
