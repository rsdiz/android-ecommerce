package id.rsdiz.rdshop.seller.ui.home.ui.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.seller.adapter.CategoryListAdapter
import id.rsdiz.rdshop.seller.databinding.FragmentCategoryBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding as FragmentCategoryBinding

    private val viewModel: CategoryViewModel by viewModels()

    @Inject
    lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setVisibilityContent(View.GONE, true)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.rvCategoryList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = categoryListAdapter
        }

        binding.refreshCategory.setOnRefreshListener {
//            setVisibilityContent(View.GONE, true)
            viewModel.refreshCategory()
        }

        categoryListAdapter.setOnItemClickListener {
            Toast.makeText(context, "Category Clicked!", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            setupFabButton()
            fetchCategories()
        }
    }

    private fun fetchCategories() {
        viewModel.categories.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        categoryListAdapter.submitData(it)
                        setVisibilityContent(View.VISIBLE, true)
                    }
                }
                is Resource.Error -> {
                    Log.d("RDSHOP-DEBUG", "fetchCategories: ${resource.message}")
                    setVisibilityContent(View.GONE)
                }
                else -> {}
            }
            binding.refreshCategory.isRefreshing = false
        }
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

    private fun setupFabButton() {
        binding.buttonAddProduct.setOnClickListener {
            Toast.makeText(requireContext(), "Add Category!", Toast.LENGTH_SHORT).show()
        }
    }
}
