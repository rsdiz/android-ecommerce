package id.rsdiz.rdshop.seller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.seller.databinding.ItemDashboardMenuBinding
import id.rsdiz.rdshop.seller.model.DashboardMenu

class DashboardMenuAdapter : RecyclerView.Adapter<DashboardMenuAdapter.ViewHolder>() {
    private val mutableListMenu = mutableListOf<DashboardMenu>()
    private var onItemClick: ((DashboardMenu) -> Unit)? = null

    fun refreshMenu(list: List<DashboardMenu>) {
        with(mutableListMenu) {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun addMenu(index: Int, dashboardMenu: DashboardMenu) {
        with(mutableListMenu) {
            add(index, dashboardMenu)
            notifyItemInserted(index)
        }
    }

    fun updateMenu(index: Int, newDashboardMenu: DashboardMenu) {
        with(mutableListMenu) {
            set(index, newDashboardMenu)
            notifyItemChanged(index)
        }
    }

    fun isMenuAvailable(menu: DashboardMenu) =
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
