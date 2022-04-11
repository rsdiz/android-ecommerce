package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.View
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

    fun insertData(index: Int, dashboardMenu: DashboardMenu) {
        mutableListMenu.add(index, dashboardMenu)
        notifyItemInserted(index)
    }

    fun updateData(index: Int, newDashboardMenu: DashboardMenu) {
        if (index == -1) return
        mutableListMenu[index] = newDashboardMenu
        notifyItemChanged(index)
    }

    fun getIndex(menu: DashboardMenu): Int {
        mutableListMenu.onEachIndexed { i, m ->
            if (menu.title == m.title) {
                return i
            }
        }
        return -1
    }

    fun setOnItemClickListener(listener: ((DashboardMenu) -> Unit)) {
        onItemClick = listener
    }

    inner class ViewHolder(private val binding: ItemDashboardMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dashboardMenu: DashboardMenu) {
            binding.apply {
                itemImage.setImageResource(dashboardMenu.imageResId)
                itemTitle.text = dashboardMenu.title
                if (dashboardMenu.isError) {
                    itemError.visibility = View.VISIBLE
                    itemTotal.visibility = View.GONE
                } else {
                    itemError.visibility = View.GONE
                    itemTotal.visibility = View.VISIBLE
                    itemTotal.text = "Total: ${dashboardMenu.count}"
                }
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
