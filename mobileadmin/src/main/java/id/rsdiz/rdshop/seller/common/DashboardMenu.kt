package id.rsdiz.rdshop.seller.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DashboardMenu(
    val title: String,
    val imageResId: Int,
    var count: Int = 0,
    var isError: Boolean = false
) : Parcelable
