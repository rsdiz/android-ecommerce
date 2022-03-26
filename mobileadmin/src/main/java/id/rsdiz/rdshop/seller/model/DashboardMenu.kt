package id.rsdiz.rdshop.seller.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DashboardMenu(
    val title: String,
    val imageResId: Int,
    var count: Int
) : Parcelable
