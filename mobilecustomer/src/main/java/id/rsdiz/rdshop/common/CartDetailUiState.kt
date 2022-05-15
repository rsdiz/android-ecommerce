package id.rsdiz.rdshop.common

import id.rsdiz.rdshop.common.base.BaseUiState
import id.rsdiz.rdshop.data.model.CartDetail
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.data.model.Product

data class CartDetailUiState(
    val cartDetail: CartDetail,
    val product: Product,
    val category: Category
) : BaseUiState()
