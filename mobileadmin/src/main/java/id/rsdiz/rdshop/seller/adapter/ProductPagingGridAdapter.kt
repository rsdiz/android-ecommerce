package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.common.ProductItemUiState
import id.rsdiz.rdshop.seller.databinding.ItemProductGridListBinding
import javax.inject.Inject

class ProductPagingGridAdapter @Inject constructor() :
    PagingDataAdapter<ProductItemUiState, ProductPagingGridAdapter.ProductViewHolder>(Comparator) {
    private var onItemClick: ((ProductItemUiState) -> Unit)? = null

    fun setOnItemClickListener(listener: ((ProductItemUiState) -> Unit)) {
        onItemClick = listener
    }

    inner class ProductViewHolder(
        private val binding: ItemProductGridListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductItemUiState) {
            binding.apply {
                productName.text = product.productName
                productPrice.text = product.productPrice
                productStock.text = product.productStock
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
                getItem(bindingAdapterPosition)?.let { product ->
                    onItemClick?.invoke(product)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { productItemUiState ->
            if (productItemUiState != null) {
                holder.bind(productItemUiState)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ItemProductGridListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    object Comparator : DiffUtil.ItemCallback<ProductItemUiState>() {
        override fun areItemsTheSame(
            oldItem: ProductItemUiState,
            newItem: ProductItemUiState
        ): Boolean = oldItem.productId == newItem.productId

        override fun areContentsTheSame(
            oldItem: ProductItemUiState,
            newItem: ProductItemUiState
        ): Boolean = oldItem == newItem
    }
}
