package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Cart Detail
 */
@Parcelize
data class CartDetail(
    val productId: String,
    var price: Int,
    var quantity: Int,
    var isChecked: Boolean
) : Parcelable
