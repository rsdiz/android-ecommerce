package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.databinding.ItemUserListBinding
import javax.inject.Inject

class UserPagingAdapter @Inject constructor() :
    PagingDataAdapter<User, UserPagingAdapter.ViewHolder>(Comparator) {
    private var onItemClick: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: ((User) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                username.text = root.context.resources.getString(R.string.string_user_username, user.username)
                fullname.text = root.context.resources.getString(R.string.string_user_fullname, user.username)
            }
        }

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { user ->
                    onItemClick?.invoke(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserPagingAdapter.ViewHolder =
        ViewHolder(
            ItemUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: UserPagingAdapter.ViewHolder, position: Int) =
        getItem(position).let { user ->
            if (user != null) {
                holder.bind(user)
            }
        }

    object Comparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.userId == newItem.userId

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem
    }
}
