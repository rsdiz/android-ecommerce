package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.databinding.ItemImageListBinding

class ImageListAdapter constructor(private val productId: String) :
    RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    private var mutableImageList = mutableListOf<String>()
    private var mutableImageIdList = mutableListOf<String>()
    private var onItemClick: ((Int, String, String) -> Unit)? = null

    fun insertData(imageId: String, urlPath: String) {
        val position = itemCount
        mutableImageList.add(position, urlPath)
        mutableImageIdList.add(position, imageId)
        notifyItemInserted(position)
    }

    fun deleteData(index: Int) {
        mutableImageList.removeAt(index)
        mutableImageIdList.removeAt(index)
        notifyItemRemoved(index)
    }

    fun setOnDeleteListener(listener: (position: Int, productId: String, imageId: String) -> Unit) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemImageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(urlPath: String) {
            binding.apply {
                Glide.with(root.context)
                    .load(urlPath)
                    .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                    .error(R.drawable.bg_image_error)
                    .centerCrop()
                    .into(imagePreview)

                buttonRemove.setOnClickListener {
                    onItemClick?.invoke(
                        bindingAdapterPosition,
                        productId,
                        mutableImageIdList[bindingAdapterPosition]
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemImageListBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(urlPath = mutableImageList[position])

    override fun getItemCount(): Int = mutableImageList.size
}
