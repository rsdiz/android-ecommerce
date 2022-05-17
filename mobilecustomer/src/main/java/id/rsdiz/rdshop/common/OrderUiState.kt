package id.rsdiz.rdshop.common

import id.rsdiz.rdshop.common.base.BaseUiState
import id.rsdiz.rdshop.data.model.CartDetail
import id.rsdiz.rdshop.data.model.Product

class OrderUiState(
    val cartDetail: CartDetail,
    val product: Product
) : BaseUiState()
