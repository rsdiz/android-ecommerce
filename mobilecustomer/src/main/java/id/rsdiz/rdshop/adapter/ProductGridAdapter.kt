package id.rsdiz.rdshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.common.ProductItemUiState
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.databinding.ItemProductGridListBinding

class ProductGridAdapter(private val data: List<ProductItemUiState>) : RecyclerView.Adapter<ProductGridAdapter.ViewHolder>() {
    private var onItemClick: ((ProductItemUiState) -> Unit)? = null

    fun setOnItemClickListener(listener: ((ProductItemUiState) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(
        private val binding: ItemProductGridListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductItemUiState) {
            binding.apply {
                productName.text = product.productName
                productPrice.text = product.productPrice
                if (product.productImages.isNotEmpty()) {
                    Glide.with(root.context)
                        .load(product.productImages[0].path)
                        .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                        .error(R.drawable.bg_image_error)
                        .centerCrop()
                        .into(productImage)
                } else {
                    Glide.with(root.context)
                        .load(R.drawable.bg_image_error)
                        .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                        .centerCrop()
                        .into(productImage)
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                data[bindingAdapterPosition - 1].let { product ->
                    onItemClick?.invoke(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemProductGridListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}
