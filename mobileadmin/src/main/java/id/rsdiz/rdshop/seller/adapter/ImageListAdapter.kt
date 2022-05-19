package id.rsdiz.rdshop.seller.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.rsdiz.rdshop.seller.databinding.ItemProductImageBinding

class ImageListAdapter(private val context: Context) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    private var mutableImageList = mutableListOf<Uri>()
    private var onItemClick: ((Int, Uri) -> Unit)? = null

    fun insertData(uri: Uri) {
        val position = itemCount
        mutableImageList.add(position, uri)
        notifyItemInserted(position)
    }

    fun deleteData(index: Int) {
        mutableImageList.removeAt(index)
        notifyItemRemoved(index)
    }

    fun setOnItemClickListener(listener: (Int, Uri) -> Unit) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            binding.apply {
                Glide.with(root.context)
                    .load(uri)
                    .apply(RequestOptions().override(120, 120))
//                    .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
//                    .error(R.drawable.bg_image_error)
                    .centerCrop()
                    .into(image)

                buttonRemove.setOnClickListener {
                    deleteData(bindingAdapterPosition)
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(
                    bindingAdapterPosition,
                    mutableImageList[bindingAdapterPosition]
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            ItemProductImageBinding.inflate(LayoutInflater.from(context))
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(uri = mutableImageList[position])

    override fun getItemCount(): Int = mutableImageList.size
}
