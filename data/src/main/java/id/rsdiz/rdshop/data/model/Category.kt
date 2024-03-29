package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Product Category
 */
@Parcelize
data class Category(
    val categoryId: String,
    val name: String
) : Parcelable
