package id.rsdiz.rdshop.seller.ui.home.ui.product.manage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.htmlEncode
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.ManagePermissions
import id.rsdiz.rdshop.base.utils.URIPathHelper
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.adapter.ImageListAdapter
import id.rsdiz.rdshop.seller.databinding.ActivityManageProductBinding
import id.rsdiz.rdshop.seller.databinding.DialogAddImageProductBinding
import id.rsdiz.rdshop.seller.ui.home.ui.product.ProductViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class ManageProductActivity : AppCompatActivity() {
    private var _binding: ActivityManageProductBinding? = null
    private val binding get() = _binding as ActivityManageProductBinding
    private var _layout: DialogAddImageProductBinding? = null
    private val layout get() = _layout as DialogAddImageProductBinding

    private lateinit var managePermissions: ManagePermissions
    private lateinit var imageListAdapter: ImageListAdapter

    private val dataArgs get() = ManageProductActivityArgs.fromBundle(intent.extras as Bundle)

    private var dialogType = -1 as? Int
    private var productData: Product? = null

    private val viewModel: ProductViewModel by viewModels()
    private val categories = mutableListOf<String>()
    private val categoryIndex = mutableListOf<String>()

    private var imageFileThumbnail: File? = null
    private lateinit var imageFileCatalog: File

    private val resultImageThumbnail =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                data?.let { intent ->
                    intent.data?.let { selectedImageUri ->
                        val selectedImageBitmap: Bitmap
                        try {
                            URIPathHelper.getPath(this, selectedImageUri)?.let { path ->
                                imageFileThumbnail = File(path)
                            }

                            selectedImageBitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

                            Glide.with(this)
                                .load(selectedImageBitmap)
                                .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                                .error(R.drawable.bg_image_error)
                                .centerCrop()
                                .into(binding.imagePreview)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    private val resultImageCatalog =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let { intent ->
                    intent.data?.let { selectedImageUri ->
                        val selectedImageBitmap: Bitmap
                        try {
                            URIPathHelper.getPath(this, selectedImageUri)?.let { path ->
                                imageFileCatalog = File(path)
                            }

                            selectedImageBitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

                            Glide.with(this)
                                .load(selectedImageBitmap)
                                .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                                .error(R.drawable.bg_image_error)
                                .centerCrop()
                                .into(layout.imagePreview)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityManageProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        managePermissions = ManagePermissions(this, permissions, PERMISSIONS_REQUEST)

        dialogType = dataArgs.type
        productData = dataArgs.product
        imageListAdapter = ImageListAdapter(
            productData?.productId ?: dataArgs.product?.productId ?: UUID.randomUUID().toString()
        )

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        with(binding) {
            buttonIncrease.setOnClickListener {
                val stock = inputProductStock.editText?.text.toString().toIntOrNull()
                inputProductStock.editText?.setText((stock?.plus(1) ?: 1).toString())
            }

            buttonDecrease.setOnClickListener {
                val stock = inputProductStock.editText?.text.toString().toIntOrNull()
                if (stock != null && stock >= 0) inputProductStock.editText?.setText(
                    stock.minus(1).toString()
                )
            }

            binding.buttonGallery.setOnClickListener {
                when (managePermissions.isPermissionsGranted()) {
                    PackageManager.PERMISSION_GRANTED -> {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT

                        resultImageThumbnail.launch(intent)
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        managePermissions.checkPermissions()
                    }
                }
            }

            when (dialogType) {
                TYPE_ADD -> {
                    toolbar.title = "Tambah Produk"
                    toolbar.menu.getItem(0).isVisible = false

                    fetchCategories(type = TYPE_ADD)

                    buttonSave.setOnClickListener {
                        progressBarSave.visibility = View.VISIBLE

                        var categoryId = ""
                        inputCategory.editText?.text?.toString().let { categoryName ->
                            categoryId =
                                categoryIndex[categories.indexOfFirst { it == categoryName }]
                        }

                        productData = Product(
                            productId = UUID.randomUUID().toString(),
                            categoryId = categoryId,
                            name = inputProductName.editText?.text.toString(),
                            description = inputProductDescription.editText?.text.toString(),
                            price = inputProductPrice.editText?.text.toString().toInt(),
                            stock = inputProductStock.editText?.text.toString().toInt(),
                            weight = inputProductWeight.editText?.text.toString().toFloat(),
                            image = listOf()
                        )

                        productData?.let { product ->
                            lifecycleScope.launch {
                                when (
                                    val response = viewModel.createProduct(
                                        product = product,
                                        imageFile = imageFileThumbnail
                                    )
                                ) {
                                    is Resource.Success -> {
                                        progressBarSave.visibility = View.GONE
                                        Snackbar.make(
                                            this@ManageProductActivity,
                                            binding.root,
                                            "Berhasil Menambah Data!",
                                            Snackbar.LENGTH_SHORT
                                        ).addCallback(object : Snackbar.Callback() {
                                            override fun onDismissed(
                                                transientBottomBar: Snackbar?,
                                                event: Int
                                            ) {
                                                finish()
                                            }
                                        }).show()
                                    }
                                    else -> {
                                        progressBarSave.visibility = View.GONE
                                        Snackbar.make(
                                            this@ManageProductActivity,
                                            binding.root,
                                            "Gagal Menambah Data! ${response.message}",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
                TYPE_EDIT -> {
                    toolbar.title = "Edit Produk"
                    toolbar.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.delete -> {
                                productData?.let { product ->
                                    lifecycleScope.launch {
                                        when (
                                            val response = viewModel.deleteProduct(
                                                productId = product.productId
                                            )
                                        ) {
                                            is Resource.Success -> {
                                                Snackbar.make(
                                                    this@ManageProductActivity,
                                                    binding.root,
                                                    "Berhasil Menghapus Data!",
                                                    Snackbar.LENGTH_SHORT
                                                ).addCallback(object : Snackbar.Callback() {
                                                    override fun onDismissed(
                                                        transientBottomBar: Snackbar?,
                                                        event: Int
                                                    ) {
                                                        finish()
                                                    }
                                                }).show()
                                            }
                                            else -> {
                                                Snackbar.make(
                                                    this@ManageProductActivity,
                                                    binding.root,
                                                    "Gagal Menghapus Data! ${response.message}",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        true
                    }

                    buttonSave.setOnClickListener {
                        progressBarSave.visibility = View.VISIBLE

                        var categoryId = ""
                        inputCategory.editText?.text?.toString().let { categoryName ->
                            categoryId =
                                categoryIndex[categories.indexOfFirst { it == categoryName }]
                        }

                        productData = Product(
                            productId = productData?.productId!!,
                            categoryId = categoryId,
                            name = inputProductName.editText?.text.toString(),
                            description = inputProductDescription.editText?.text.toString()
                                .htmlEncode(),
                            price = inputProductPrice.editText?.text.toString().toInt(),
                            stock = inputProductStock.editText?.text.toString().toInt(),
                            weight = inputProductWeight.editText?.text.toString().toFloat(),
                            image = listOf()
                        )

                        productData?.let { product ->
                            lifecycleScope.launch {
                                when (
                                    val response = viewModel.updateProducts(
                                        product = product,
                                        imageFile = imageFileThumbnail
                                    )
                                ) {
                                    is Resource.Success -> {
                                        progressBarSave.visibility = View.GONE
                                        Snackbar.make(
                                            this@ManageProductActivity,
                                            binding.root,
                                            response.data.toString(),
                                            Snackbar.LENGTH_SHORT
                                        ).addCallback(object : Snackbar.Callback() {
                                            override fun onDismissed(
                                                transientBottomBar: Snackbar?,
                                                event: Int
                                            ) {
                                                finish()
                                            }
                                        }).show()
                                    }
                                    else -> {
                                        progressBarSave.visibility = View.GONE
                                        Snackbar.make(
                                            this@ManageProductActivity,
                                            binding.root,
                                            "Gagal Mengubah Data! ${response.message}",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }

                    rvProductImage.apply {
                        layoutManager = LinearLayoutManager(
                            this@ManageProductActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                        adapter = imageListAdapter
                        imageListAdapter.setOnDeleteListener { position, productId, imageId ->
                            lifecycleScope.launch {
                                when (viewModel.removeImageCatalog(productId, imageId)) {
                                    is Resource.Success -> {
                                        Toast.makeText(
                                            this@ManageProductActivity,
                                            "Berhasil Menghapus Gambar dari Katalog!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        imageListAdapter.deleteData(position)
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@ManageProductActivity,
                                            "Gagal Menghapus Gambar dari Katalog!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }

                    setupView()

                    buttonAddImage.setOnClickListener {
                        _layout = DialogAddImageProductBinding.inflate(layoutInflater)
                        val materialDialog = MaterialAlertDialogBuilder(buttonAddImage.context)
                        layout.buttonGallery.setOnClickListener {
                            when (managePermissions.isPermissionsGranted()) {
                                PackageManager.PERMISSION_GRANTED -> {
                                    val intent = Intent()
                                    intent.type = "image/*"
                                    intent.action = Intent.ACTION_GET_CONTENT

                                    resultImageCatalog.launch(intent)
                                }
                                PackageManager.PERMISSION_DENIED -> {
                                    managePermissions.checkPermissions()
                                }
                            }
                        }
                        layout.buttonSave.setOnClickListener {
                            layout.progressBarSave.visibility = View.VISIBLE
                            productData?.let { product ->
                                lifecycleScope.launch {
                                    when (
                                        val response = viewModel.addImageCatalog(
                                            productId = product.productId,
                                            imageFile = imageFileCatalog
                                        )
                                    ) {
                                        is Resource.Success -> {
                                            layout.progressBarSave.visibility = View.GONE
                                            Toast.makeText(
                                                this@ManageProductActivity,
                                                "Gambar Berhasil ditambahkan ke Katalog!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else -> {
                                            layout.progressBarSave.visibility = View.GONE
                                            Toast.makeText(
                                                this@ManageProductActivity,
                                                "Gagal Menambahkan Gambar ke Katalog!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.e(TAG, "addImageCatalog: ${response.message}")
                                        }
                                    }
                                }
                            }
                        }

                        materialDialog.setView(layout.root)
                            .setTitle("Gambar Produk")
                            .setMessage("Pilih opsi pengambilan gambar")
                            .setNeutralButton("Batal") { dialog, _ ->
                                removeParent(layout.root)
                                dialog.dismiss()
                            }
                            .setPositiveButton("OK") { dialog, _ ->
                                refreshProductData()
                                removeParent(layout.root)
                                viewModel.refreshProduct()
                                dialog.dismiss()
                            }
                            .setOnCancelListener {
                                removeParent(layout.root)
                                it.dismiss()
                            }
                            .show()
                    }
                }
                else -> {
                    val materialDialog = MaterialAlertDialogBuilder(this@ManageProductActivity)
                    materialDialog.setTitle("Terjadi Kesalahan!")
                        .setMessage("Tipe tidak dikenali")
                        .setPositiveButton("Kembali") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .setOnCancelListener {
                            it.dismiss()
                            finish()
                        }
                        .show()
                }
            }
        }
    }

    private fun setupView() {
        productData?.let { product ->
            fetchCategories(product, TYPE_EDIT)

            if (product.image.isNotEmpty())
                Glide.with(this@ManageProductActivity)
                    .load(product.image[0].path)
                    .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                    .error(R.drawable.bg_image_error)
                    .centerCrop()
                    .into(binding.imagePreview)

            product.image.forEachIndexed { index, productImage ->
                if (index != 0) imageListAdapter.insertData(
                    imageId = productImage.imageId,
                    urlPath = productImage.path
                )
            }

            binding.apply {
                inputProductName.editText?.setText(product.name)
                inputProductDescription.editText?.setText(product.description)
                inputProductPrice.editText?.setText(product.price.toString())
                inputProductStock.editText?.setText(product.stock.toString())
                inputProductWeight.editText?.setText(product.weight.toString())
            }
        }
    }

    private fun fetchCategories(product: Product? = null, type: Int) {
        lifecycleScope.launch {
            viewModel.categories
                .observe(this@ManageProductActivity) { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let {
                                it.forEach { category ->
                                    categoryIndex.add(category.categoryId)
                                    categories.add(category.name)
                                }

                                val adapter = ArrayAdapter(
                                    this@ManageProductActivity,
                                    R.layout.item_list_text,
                                    categories
                                )

                                (binding.inputCategory.editText as? AutoCompleteTextView)?.apply {
                                    setAdapter(adapter)
                                    if (type == TYPE_EDIT && product != null) {
                                        setText(
                                            adapter.getItem(categoryIndex.indexOf(product.categoryId)),
                                            false
                                        )
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun refreshProductData() {
        productData?.let { product ->
            lifecycleScope.launch {
                viewModel.getProductById(product.productId)
                    .observe(this@ManageProductActivity) { response ->
                        when (response) {
                            is Resource.Success -> {
                                productData = response.data
                                setupView()
                            }
                            else -> {
                                Toast.makeText(
                                    this@ManageProductActivity,
                                    "Data mengupdate data!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                grantResults[0].let {
                    if (it == PackageManager.PERMISSION_GRANTED) {
                        Log.d(
                            "RDSHOP-DEBUG",
                            "onRequestPermissionsResult: ${permissions[it]}: GRANTED"
                        )
                    }
                }
            }
        }
    }

    private fun removeParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private var TAG = "product_activity"
        private const val PERMISSIONS_REQUEST = 101

        const val TYPE_ADD = 0
        const val TYPE_EDIT = 1
    }
}
