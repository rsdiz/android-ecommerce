package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Detail Cost
 */
@Parcelize
data class DetailCost(
    val value: Int,
    val estimationDay: String?,
    val note: String?
) : Parcelable
