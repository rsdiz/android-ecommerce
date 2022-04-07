package id.rsdiz.rdshop.seller.common

import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.seller.common.base.BaseUiState

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
}
