package id.rsdiz.rdshop.common

import androidx.paging.LoadState
import id.rsdiz.rdshop.seller.common.base.BaseUiState

data class FooterUiState(private val loadState: LoadState) : BaseUiState() {

    fun getLoadingVisibility() = getViewVisibility(isVisible = loadState is LoadState.Loading)

    fun getErrorVisibility() = getViewVisibility(isVisible = loadState is LoadState.Error)

    fun getErrorMessage() = if (loadState is LoadState.Error) {
        loadState.error.localizedMessage ?: "Terjadi Kesalahan!"
    } else ""
}
