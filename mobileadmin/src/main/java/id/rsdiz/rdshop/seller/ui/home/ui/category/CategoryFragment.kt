package id.rsdiz.rdshop.seller.ui.home.ui.category

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.seller.adapter.CategoryListAdapter
import id.rsdiz.rdshop.seller.databinding.DialogAddEditCategoryBinding
import id.rsdiz.rdshop.seller.databinding.FragmentCategoryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding as FragmentCategoryBinding

    private val viewModel: CategoryViewModel by viewModels()

    private val categoryListAdapter = CategoryListAdapter()

    private lateinit var materialDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisibilityContent(View.GONE, true)

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

        categoryListAdapter.setOnItemClickListener { position, data ->
            materialDialog = MaterialAlertDialogBuilder(requireContext())
            val categoryLayout = DialogAddEditCategoryBinding.inflate(LayoutInflater.from(binding.root.context))
            val inputCategoryName: EditText? = categoryLayout.inputCategoryName.editText
            categoryLayout.inputCategoryName.editText?.text = Editable.Factory.getInstance().newEditable(data.name)

            materialDialog.setView(categoryLayout.root)
                .setTitle("Edit Kategori")
                .setNeutralButton("Batal") { dialog, _ ->
                    removeParent(categoryLayout.root)
                    dialog.dismiss()
                }
                .setNegativeButton("Hapus") { dialog, _ ->
                    lifecycleScope.launch {
                        when (val response = viewModel.deleteCategory(data.categoryId)) {
                            is Resource.Success -> {
                                Snackbar.make(
                                    requireContext(),
                                    binding.root,
                                    response.data.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                removeParent(categoryLayout.root)
                                categoryListAdapter.deleteData(position)
                                dialog.dismiss()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal Menghapus Kategori!\nError: ${response.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
                }
                .setPositiveButton("Simpan") { dialog, _ ->
                    lifecycleScope.launch {
                        val newData = Category(data.categoryId, inputCategoryName?.text.toString())
                        when (val response = viewModel.updateCategory(newData)) {
                            is Resource.Success -> {
                                Snackbar.make(
                                    requireContext(),
                                    binding.root,
                                    response.data.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                removeParent(categoryLayout.root)
                                categoryListAdapter.updateData(position, newData)
                                dialog.dismiss()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal Mengupdate Kategori!\nError: ${response.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
                }
                .setOnCancelListener {
                    removeParent(categoryLayout.root)
                }
                .show()
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
        materialDialog = MaterialAlertDialogBuilder(requireContext())
        val categoryLayout = DialogAddEditCategoryBinding.inflate(LayoutInflater.from(binding.root.context))
        val inputCategoryName: EditText? = categoryLayout.inputCategoryName.editText

        binding.buttonAddCategory.setOnClickListener {
            materialDialog.setView(categoryLayout.root)
                .setTitle("Tambah Kategori")
                .setMessage("Silahkan masukkan nama kategori yang ingin ditambahkan.")
                .setNegativeButton("Batal") { dialog, _ ->
                    removeParent(categoryLayout.root)
                    dialog.dismiss()
                }
                .setPositiveButton("Tambahkan") { dialog, _ ->
                    lifecycleScope.launch {
                        when (val response = viewModel.addCategory(inputCategoryName?.text.toString())) {
                            is Resource.Success -> {
                                Snackbar.make(
                                    requireContext(),
                                    binding.root,
                                    response.data.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                removeParent(categoryLayout.root)
                                viewModel.refreshCategory()
                                dialog.dismiss()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal Menambahkan Kategori!\nError: ${response.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {}
                        }
                    }
                }
                .setOnCancelListener {
                    removeParent(categoryLayout.root)
                }
                .show()
        }
    }

    private fun removeParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }
}
