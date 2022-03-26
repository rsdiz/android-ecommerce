package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Order Detail
 */
@Parcelize
data class OrderDetail(
    val detailId: String,
    val productId: String,
    val price: Int,
    val quantity: Int
) : Parcelable
