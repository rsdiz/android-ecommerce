package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.seller.common.OrderDetailItemUiState
import id.rsdiz.rdshop.seller.databinding.ItemProductBinding
import id.rsdiz.rdshop.seller.databinding.ItemProductHeaderBinding

class ProductListWithHeaderAdapter(
    private val onItemClickListener: ((OrderDetailItemUiState) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data: MutableList<OrderDetailItemUiState> = mutableListOf()

    fun addData(orderDetailItemUiState: OrderDetailItemUiState) {
        val position = data.size
        data.add(position, orderDetailItemUiState)
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> {
                VHHeader(
                    ItemProductHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_ITEM -> {
                VHProduct(
                    ItemProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw RuntimeException("Item Type is Not Defined in Adapter!")
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VHProduct) {
            holder.bind(getItem(position))
        }
    }

    private fun getItem(position: Int): OrderDetailItemUiState {
        return data[position - 1]
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position))
            return TYPE_HEADER
        return TYPE_ITEM
    }

    override fun getItemCount(): Int = data.size + 1

    inner class VHProduct(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderDetail: OrderDetailItemUiState) {
            binding.apply {
                textNumber.text = bindingAdapterPosition.toString()
                textProductName.text = orderDetail.getProductName()
                textProductQty.text = orderDetail.getQuantity().toString()
                textProductPrice.text = orderDetail.getCurrentProductPrice().toRupiah()
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClickListener.invoke(getItem(bindingAdapterPosition))
            }
        }
    }

    inner class VHHeader(binding: ItemProductHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
}
