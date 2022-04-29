package id.rsdiz.rdshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.rsdiz.rdshop.common.FooterUiState
import id.rsdiz.rdshop.databinding.ItemPagingFooterBinding

class FooterPagingAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<FooterPagingAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = ViewHolder(
        ItemPagingFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        retry
    )

    inner class ViewHolder(
        private val binding: ItemPagingFooterBinding,
        retry: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.refreshButton.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            val uiState = FooterUiState(loadState)
            binding.apply {
                loadingIndicator.visibility = uiState.getLoadingVisibility()
                refreshButton.visibility = uiState.getErrorVisibility()
                errorText.text = uiState.getErrorMessage()
                errorText.visibility = uiState.getErrorVisibility()
            }
        }
    }
}
