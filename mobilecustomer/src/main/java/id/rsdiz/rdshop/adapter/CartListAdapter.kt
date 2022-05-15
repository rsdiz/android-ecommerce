package id.rsdiz.rdshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.common.CartDetailUiState
import id.rsdiz.rdshop.data.model.CartDetail
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.databinding.ItemCartListBinding

class CartListAdapter : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    private val data: MutableList<CartDetailUiState> = mutableListOf()

    fun insertData(cartDetailUiState: CartDetailUiState) {
        val position = itemCount
        data.add(position, cartDetailUiState)
        notifyItemInserted(position)
    }

    fun deleteData(index: Int) {
        data.removeAt(index)
        notifyItemRemoved(index)
    }

    fun getData(index: Int) = data[index]

    private var onItemClick: ((Product) -> Unit)? = null

    private var onCheckedChange: ((CartDetail, Boolean) -> Unit)? = null

    private var onQuantityChange: ((CartDetail, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClick = listener
    }

    fun setOnCheckedChangeListener(listener: (CartDetail, Boolean) -> Unit) {
        onCheckedChange = listener
    }

    fun setOnQuantityChangeListener(listener: (CartDetail, Int) -> Unit) {
        onQuantityChange = listener
    }

    inner class ViewHolder(
        private val binding: ItemCartListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uiState: CartDetailUiState) {
            binding.apply {
                productName.text = uiState.product.name
                productPrice.text = uiState.product.price.toRupiah()
                productCategory.text = uiState.category.name
                cartQuantity.text = uiState.cartDetail.quantity.toString()
                cartCheckbox.isChecked = uiState.cartDetail.isChecked

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

                buttonAddQuantity.setOnClickListener {
                    var currentQuantity = cartQuantity.text.toString().toInt()
                    if (currentQuantity < uiState.product.stock) {
                        cartQuantity.text = (++currentQuantity).toString()
                        data[bindingAdapterPosition].let {
                            onQuantityChange?.invoke(it.cartDetail, currentQuantity)
                        }
                    } else {
                        Toast.makeText(
                            root.context,
                            "Kuantitas barang sudah maksimal!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                buttonMinusQuantity.setOnClickListener {
                    var currentQuantity = cartQuantity.text.toString().toInt()
                    if (currentQuantity > 1) {
                        cartQuantity.text = (--currentQuantity).toString()
                        data[bindingAdapterPosition].let {
                            onQuantityChange?.invoke(it.cartDetail, currentQuantity)
                        }
                    } else {
                        Toast.makeText(
                            root.context,
                            "Kuantitas barang minimal 1",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                data[bindingAdapterPosition].let {
                    onItemClick?.invoke(it.product)
                }
            }

            binding.cartCheckbox.setOnCheckedChangeListener { _, isChecked ->
                data[bindingAdapterPosition].let {
                    onCheckedChange?.invoke(it.cartDetail, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemCartListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount(): Int = data.size
}
