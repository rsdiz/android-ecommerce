package id.rsdiz.rdshop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.data.model.ServiceCost
import id.rsdiz.rdshop.databinding.ItemDeliveryServiceListBinding

class DeliveryServiceAdapter(
    private val context: Context,
    private val data: List<ServiceCost>
) : BaseAdapter(), Filterable {
    private var currentData = data

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = data[position].hashCode().toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view =
            ItemDeliveryServiceListBinding.inflate(LayoutInflater.from(context), parent, false)

        view.serviceName.text = data[position].service
        view.servicePrice.text = data[position].cost?.first()?.value?.toRupiah()
        view.serviceEstimation.text =
            StringBuilder("Estimasi ").append(data[position].cost?.first()?.estimationDay)
                .append(" Hari").toString()

        return view.root
    }

    override fun getFilter(): Filter =
        object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<ServiceCost>()
                data.forEach {
                    if (constraint?.let { text -> it.service.contains(text, false) } == true)
                        filteredList.add(it)
                }
                return FilterResults().apply {
                    values = filteredList
                    count = filteredList.size
                }
            }

            override fun publishResults(constraint: CharSequence?, filterResult: FilterResults) {
                if (filterResult.count > 0) {
                    currentData = filterResult.values as List<ServiceCost>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
}
