package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Product Image
 */
@Parcelize
data class ProductImage(
    val imageId: String,
    val path: String
) : Parcelable
