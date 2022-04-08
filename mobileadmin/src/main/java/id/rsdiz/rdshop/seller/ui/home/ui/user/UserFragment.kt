package id.rsdiz.rdshop.seller.ui.home.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.collect
import id.rsdiz.rdshop.base.utils.collectLast
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.seller.adapter.FooterPagingAdapter
import id.rsdiz.rdshop.seller.adapter.UserPagingAdapter
import id.rsdiz.rdshop.seller.common.LoadStateUi
import id.rsdiz.rdshop.seller.databinding.FragmentUserBinding
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding as FragmentUserBinding

    private val viewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userPagingAdapter: UserPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisibilityContent(View.GONE, true)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.refreshUser.setOnRefreshListener {
            userPagingAdapter.refresh()
            binding.refreshUser.isRefreshing = false
        }

        lifecycleScope.launch {
            userPagingAdapter.setOnItemClickListener {
                Toast.makeText(context, "User ${it.username} Clicked!", Toast.LENGTH_SHORT).show()
            }

            collect(
                flow = userPagingAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.source.refresh }
                    .map { it.refresh },
                action = ::setUserUiState
            )

            binding.rvUserList.adapter =
                userPagingAdapter.withLoadStateFooter(
                    FooterPagingAdapter(userPagingAdapter::retry)
                )

            collectUsers()
        }
    }

    private suspend fun collectUsers() {
        collectLast(viewModel.getUsers().cancellable(), ::setUsers)
    }

    private fun setUserUiState(loadState: LoadState) {
        val ui = LoadStateUi(loadState)
        binding.apply {
            val isVisible = ui.getListVisibility()

            setVisibilityContent(isVisible, ui.getProgressBarVisibility() == View.VISIBLE)

            if (ui.getErrorVisibility() == View.VISIBLE) {
                errorText.text = ui.getErrorMessage()
            }
        }
    }

    private suspend fun setUsers(userItemsPagingData: PagingData<User>) {
        userPagingAdapter.submitData(userItemsPagingData)
    }

    private fun setVisibilityContent(visibility: Int, isLoading: Boolean = false) {
        binding.apply {
            when (visibility) {
                View.VISIBLE -> {
                    layoutContent.visibility = visibility
                    layoutLoading.visibility = View.GONE
                    layoutError.visibility = View.GONE
                }
                View.GONE -> {
                    layoutContent.visibility = visibility
                    if (isLoading) {
                        layoutLoading.visibility = View.VISIBLE
                        layoutError.visibility = View.GONE
                    } else {
                        layoutLoading.visibility = View.GONE
                        layoutError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
