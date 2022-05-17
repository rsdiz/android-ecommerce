package id.rsdiz.rdshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.common.OrderUiState
import id.rsdiz.rdshop.databinding.ItemOrderListBinding
import kotlin.math.roundToInt

class OrderListAdapter : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    private val data: MutableList<OrderUiState> = mutableListOf()

    fun insertData(cartDetailUiState: OrderUiState) {
        val position = itemCount
        data.add(position, cartDetailUiState)
        notifyItemInserted(position)
    }

    fun getTotalOrderWeight(): Int {
        var weight = 0
        data.forEach {
            weight += (it.product.weight * it.cartDetail.quantity).roundToInt()
        }
        return weight
    }

    fun getTotalQuantity(): Int {
        var qty = 0
        data.forEach {
            qty += (it.cartDetail.quantity)
        }
        return qty
    }

    fun getTotalPrice(): Int {
        var price = 0
        data.forEach {
            price += (it.product.price * it.cartDetail.quantity)
        }
        return price
    }

    inner class ViewHolder(
        private val binding: ItemOrderListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uiState: OrderUiState) {
            binding.apply {
                productName.text = uiState.product.name
                productWeight.text =
                    StringBuilder("Berat: ").append(uiState.product.weight).toString()
                productPrice.text = uiState.product.price.toRupiah()
                orderQuantity.text = StringBuilder("QTY: ").append(uiState.cartDetail.quantity)

                if (uiState.product.image.isNotEmpty())
                    Glide.with(binding.root)
                        .load(uiState.product.image[0].path)
                        .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                        .error(R.drawable.bg_image_error)
                        .centerCrop()
                        .into(productImage)
                else
                    Glide.with(root.context)
                        .load(R.drawable.bg_image_error)
                        .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                        .centerCrop()
                        .into(productImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemOrderListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}
