package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Cost
 */
@Parcelize
data class Cost(
    val code: String,
    val name: String,
    val costs: List<ServiceCost>?
) : Parcelable
