package id.rsdiz.rdshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.rsdiz.rdshop.R
import id.rsdiz.rdshop.data.model.ProductImage
import id.rsdiz.rdshop.databinding.ItemProductImageBinding

class ProductImageSliderAdapter(private val listImageUrl: List<ProductImage>) : RecyclerView.Adapter<ProductImageSliderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductImageSliderAdapter.ViewHolder =
        ViewHolder(
            ItemProductImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProductImageSliderAdapter.ViewHolder, position: Int) =
        holder.bind(listImageUrl[position].path)

    override fun getItemCount(): Int = listImageUrl.size

    inner class ViewHolder(private val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .error(R.drawable.bg_image_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivImage)
        }
    }
}
