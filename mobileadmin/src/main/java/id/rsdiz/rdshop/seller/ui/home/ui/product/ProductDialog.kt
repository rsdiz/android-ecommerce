package id.rsdiz.rdshop.seller.ui.home.ui.product

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.seller.BuildConfig
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.adapter.ImageListAdapter
import id.rsdiz.rdshop.seller.databinding.DialogAddEditProductBinding
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ProductDialog : DialogFragment() {
    private var _binding: DialogAddEditProductBinding? = null
    private val binding get() = _binding as DialogAddEditProductBinding

    private val imageListAdapter by lazy { ImageListAdapter() }
    private var latestTemporaryUri: Uri? = null

    private val dataArgs get() = ProductDialogArgs.fromBundle(arguments as Bundle)

    private var dialogType = -1 as? Int
    private var productData: Product? = null

    private val viewModel: ProductViewModel by viewModels()
    private val categories = mutableListOf<String>()
    private val categoryIndex = mutableListOf<String>()

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTemporaryUri?.let { uri ->
                    imageListAdapter.insertData(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            uriList.forEach { uri ->
                imageListAdapter.insertData(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL, R.style.Theme_RDSHOP_FullScreenDialog
        )
        dialog?.let {
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.window?.setWindowAnimations(
                R.style.Theme_RDSHOP_Slide
            )
        }
        dialogType = dataArgs.dialogtype
        productData = dataArgs.product
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.setNavigationOnClickListener { dismiss() }

            when (dialogType) {
                TYPE_ADD -> {
                    toolbar.title = "Tambah Produk"
                    toolbar.setOnMenuItemClickListener {

                        dismiss()
                        return@setOnMenuItemClickListener true
                    }
                }
                TYPE_EDIT -> {
                    toolbar.title = "Edit Produk"
                    toolbar.setOnMenuItemClickListener {

                        dismiss()
                        return@setOnMenuItemClickListener true
                    }

                    rvProductImage.let {
                        it.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            true
                        )
                        it.adapter = imageListAdapter
                    }

//                    inputCategory.editText?.isEnabled = false

                    productData?.let { product ->
                        lifecycleScope.launchWhenCreated {
                            viewModel.getCategories().observe(viewLifecycleOwner) { resource ->
                                when (resource) {
                                    is Resource.Success -> {
                                        resource.data?.let {
                                            it.forEach { category ->
                                                categoryIndex.add(category.categoryId)
                                                categories.add(category.name)
                                            }

                                            val adapter = ArrayAdapter(requireContext(), R.layout.item_list_text, categories)
                                            (inputCategory.editText as? AutoCompleteTextView)?.apply {
                                                setAdapter(adapter)
                                                setText(adapter.getItem(categoryIndex.indexOf(product.categoryId)), false)
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                        product.image.forEach {
                            imageListAdapter.insertData(Uri.parse(it.path))
                        }
                        inputProductName.editText?.setText(product.name)
                        inputProductDescription.editText?.setText(product.description)
                        inputProductPrice.editText?.setText(product.price)
                        inputProductStock.editText?.setText(product.stock)
                        inputProductWeight.editText?.setText(product.weight.toString())
                    }
                    buttonAddImage.setOnClickListener {
                        val materialDialog = MaterialAlertDialogBuilder(requireContext())
                        materialDialog.setTitle("Gambar Produk")
                            .setMessage("Pilih opsi pengambilan gambar")
                            .setNeutralButton("Batal") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setNegativeButton("Kamera") { dialog, _ ->
                                takeImage()
                                dialog.dismiss()
                            }
                            .setPositiveButton("Galeri") { dialog, _ ->
                                selectImageFromGallery()
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTemporaryFileUri().let { uri ->
                latestTemporaryUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTemporaryFileUri(): Uri {
        val tmpFile =
            File.createTempFile("tmp_product_file", ".png", requireContext().cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

        return FileProvider.getUriForFile(
            requireContext().applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private var TYPE = "DIALOG_TYPE"
        private var TAG = "product_dialog"

        const val TYPE_ADD = 0
        const val TYPE_EDIT = 1
    }
}
