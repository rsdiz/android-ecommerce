package id.rsdiz.rdshop.common

import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.common.base.BaseUiState
import id.rsdiz.rdshop.data.model.Product

data class ProductItemUiState(
    private val product: Product
) : BaseUiState() {
    val productId = product.productId
    val productName = product.name
    val productStock = StringBuilder("Stok: ").append(product.stock).toString()
    val productDescription = product.description
    val categoryId = product.categoryId
    val productPrice = product.price.toRupiah()
    val productWeight = StringBuilder(product.weight.toString()).append(' ').append("Kg").toString()
    val productImages = product.image
    val isFavorite = product.isFavorite

    val data get() = product
}
