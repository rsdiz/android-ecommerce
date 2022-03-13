package id.rsdiz.rdshop.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Product
 */
@Parcelize
data class Product(
    val productId: String,
    val categoryId: String,
    val name: String,
    val weight: Float,
    val stock: Int,
    val description: String,
    val price: Int,
    val image: List<ProductImage>
) : Parcelable
