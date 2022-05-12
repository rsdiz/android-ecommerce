package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Service Cost
 */
@Parcelize
data class ServiceCost(
    val service: String,
    val description: String,
    val cost: List<DetailCost>?
) : Parcelable
