package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.seller.common.DashboardMenu
import id.rsdiz.rdshop.seller.databinding.ItemDashboardMenuBinding

class DashboardMenuAdapter : RecyclerView.Adapter<DashboardMenuAdapter.ViewHolder>() {
    private var mutableListMenu = mutableListOf<DashboardMenu>()
    private var onItemClick: ((DashboardMenu) -> Unit)? = null

    fun submitData(list: List<DashboardMenu>) {
        mutableListMenu.clear()
        mutableListMenu.addAll(list)
        notifyDataSetChanged()
    }

    fun insertData(dashboardMenu: DashboardMenu) {
        val position = itemCount
        mutableListMenu.add(position, dashboardMenu)
        notifyItemInserted(position)
    }

    fun updateData(index: Int, newDashboardMenu: DashboardMenu) {
        mutableListMenu[index] = newDashboardMenu
        notifyItemChanged(index)
    }

    fun isDataAvailable(menu: DashboardMenu) =
        mutableListMenu.find {
            it.title == menu.title
        } != null

    fun setOnItemClickListener(listener: ((DashboardMenu) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemDashboardMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dashboardMenu: DashboardMenu) {
            binding.apply {
                itemImage.setImageResource(dashboardMenu.imageResId)
                itemTitle.text = dashboardMenu.title
                itemTotal.text = "Total: ${dashboardMenu.count}"
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(mutableListMenu[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardMenuAdapter.ViewHolder =
        ViewHolder(
            ItemDashboardMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: DashboardMenuAdapter.ViewHolder, position: Int) =
        holder.bind(dashboardMenu = mutableListMenu[position])

    override fun getItemCount(): Int = mutableListMenu.size
}
