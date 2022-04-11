package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.seller.databinding.ItemCategoryListBinding

class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private var mutableCategories = mutableListOf<Category>()
    private var onItemClick: ((Int, Category) -> Unit)? = null

    fun submitData(categories: List<Category>) {
        mutableCategories.clear()
        mutableCategories.addAll(categories)
        notifyDataSetChanged()
    }

    fun updateData(index: Int, category: Category) {
        mutableCategories[index] = category
        notifyItemChanged(index)
    }

    fun deleteData(index: Int) {
        mutableCategories.removeAt(index)
        notifyItemRemoved(index)
    }

    fun setOnItemClickListener(listener: ((Int, Category) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.apply {
                categoryName.text = category.name
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(bindingAdapterPosition, mutableCategories[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListAdapter.ViewHolder =
        ViewHolder(
            ItemCategoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CategoryListAdapter.ViewHolder, position: Int) =
        holder.bind(category = mutableCategories[position])

    override fun getItemCount(): Int = mutableCategories.size
}
