package id.rsdiz.rdshop.seller.ui.home.ui.profile.edit

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.*
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.City
import id.rsdiz.rdshop.data.model.Province
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.data.source.local.entity.Gender
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.seller.R
import id.rsdiz.rdshop.seller.databinding.ActivityEditProfileBinding
import id.rsdiz.rdshop.seller.databinding.DialogPasswordConfirmationBinding
import id.rsdiz.rdshop.seller.ui.home.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSIONS_REQUEST = 102
    }

    private var _binding: ActivityEditProfileBinding? = null
    private val binding get() = _binding as ActivityEditProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var userProfile: User
    private lateinit var managePermissions: ManagePermissions
    private lateinit var prefs: SharedPreferences

    private var imageFile: File? = null

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                data?.let { intent ->
                    intent.data?.let { selectedImageUri ->
                        val selectedImageBitmap: Bitmap
                        try {
                            URIPathHelper.getPath(this, selectedImageUri)?.let { path ->
                                imageFile = File(path)
                            }

                            selectedImageBitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

                            Glide.with(this@EditProfileActivity)
                                .load(selectedImageBitmap)
                                .apply(RequestOptions.placeholderOf(R.drawable.bg_image_loading))
                                .error(R.drawable.bg_image_error)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .centerCrop()
                                .into(binding.imagePreview)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    private var cityList = mutableListOf<City>()

    private var provinceList = mutableListOf<Province>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceHelper(this).customPrefs(Consts.PREFERENCE_NAME)

        val permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        managePermissions = ManagePermissions(this, permissions, PERMISSIONS_REQUEST)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.buttonGallery.setOnClickListener {
            when (managePermissions.isPermissionsGranted()) {
                PackageManager.PERMISSION_GRANTED -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT

                    selectImageFromGalleryResult.launch(intent)
                }
                PackageManager.PERMISSION_DENIED -> {
                    managePermissions.checkPermissions()
                }
            }
        }
        binding.buttonSave.setOnClickListener {
            val materialDialog = MaterialAlertDialogBuilder(binding.root.context)
            val layout = DialogPasswordConfirmationBinding.inflate(layoutInflater)
            val inputPasswordText: EditText? = layout.inputPassword.editText

            materialDialog.setView(layout.root)
                .setTitle("Konfirmasi")
                .setMessage("Masukkan Password untuk Melanjutkan")
                .setNeutralButton("Batal") { dialog, _ ->
                    removeParent(layout.root)
                    dialog.dismiss()
                }
                .setPositiveButton("Ubah Profil") { dialog, _ ->
                    layout.indicator.visibility = View.VISIBLE

                    if (inputPasswordText?.text.isNullOrEmpty()) {
                        layout.inputPassword.isErrorEnabled = true
                        layout.inputPassword.error = "Password harus diisi!"
                    } else {
                        layout.inputPassword.isErrorEnabled = false
                        layout.inputPassword.error = ""

                        val newData = User(
                            userId = userProfile.userId,
                            email = userProfile.email,
                            username = userProfile.username,
                            name = binding.inputProfileName.editText?.text.toString(),
                            gender = when (binding.inputProfileGender.selectedItemPosition) {
                                0 -> Gender.MALE
                                1 -> Gender.FEMALE
                                else -> Gender.OTHER
                            },
                            address = StringBuilder(
                                binding.inputProfileAddress.editText?.text.toString()
                            )
                                .append('|')
                                .append(binding.inputProfileAddressCity.selectedItem.toString())
                                .append('|')
                                .append(binding.inputProfileAddressProvince.selectedItem.toString())
                                .append('|')
                                .append(binding.inputProfileAddressPostalCode.editText?.text.toString())
                                .toString(),
                            photo = userProfile.photo,
                            role = Role.CUSTOMER
                        )

                        lifecycleScope.launch {
                            when (
                                val response = viewModel.updateUserProfile(
                                    user = newData,
                                    password = inputPasswordText?.text.toString(),
                                    profileImage = imageFile
                                )
                            ) {
                                is Resource.Success -> {
                                    layout.indicator.visibility = View.GONE
                                    Snackbar.make(
                                        this@EditProfileActivity,
                                        binding.root,
                                        response.data.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    removeParent(layout.root)
                                    dialog.dismiss()
                                }
                                is Resource.Error -> {
                                    layout.indicator.visibility = View.GONE
                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "Gagal Mengubah Profile!\nError: ${response.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {}
                            }
                        }
                    }
                }
                .setOnCancelListener {
                    removeParent(layout.root)
                }
                .show()
        }
        binding.inputProfileAddressCity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected =
                        binding.inputProfileAddressCity.adapter.getItem(position).toString()
                    val selectedCity = cityList.find { city -> city.cityName == selected }
                    selectedCity?.let {
                        val indexProvince =
                            provinceList.mapIndexed { i, s -> if (s.province == it.province) i else null }
                                .filterNotNull().toList()
                        if (!indexProvince.isNullOrEmpty()) binding.inputProfileAddressProvince.setSelection(
                            indexProvince.first()
                        )
                        binding.inputProfileAddressPostalCode.editText?.setText(it.postalCode.toString())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    binding.inputProfileAddressProvince.isSelected = false
                }
            }

        lifecycleScope.launch {
            setVisibilityContent(View.GONE, true)

            fetchProvinceList()
            fetchCityList()
            fetchUserData()
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

    private suspend fun fetchProvinceList() {
        when (val response = viewModel.getProvinces()) {
            is Resource.Success -> {
                response.data?.let {
                    it.forEach { province ->
                        provinceList.add(province)
                    }
                }

                val provinceNameList = provinceList.map {
                    it.province
                }

                val arrayAdapter = ArrayAdapter(
                    this,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    provinceNameList
                )
                binding.inputProfileAddressProvince.adapter = arrayAdapter
                binding.inputProfileAddressProvince.setSelection(-1)
            }
            else -> {
                Log.d("RDSHOP-DEBUG", "fetchProvinceList: Error: ${response.message}")
            }
        }
    }

    private suspend fun fetchCityList() {
        when (val response = viewModel.getCities()) {
            is Resource.Success -> {
                response.data?.let {
                    it.forEach { city ->
                        cityList.add(city)
                    }
                }

                val cityNameList = cityList.map {
                    it.cityName
                }

                val arrayAdapter = ArrayAdapter(
                    this,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    cityNameList
                )
                binding.inputProfileAddressCity.adapter = arrayAdapter
                binding.inputProfileAddressCity.setSelection(-1)
            }
            else -> {
                Log.d("RDSHOP-DEBUG", "fetchCityList: Error: ${response.message}")
            }
        }
    }

    private suspend fun fetchUserData() {
        collectLast(viewModel.getUser(prefs[Consts.PREF_ID])) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { user ->
                        userProfile = user

                        binding.apply {
                            if (userProfile.photo != null) {
                                Glide.with(this@EditProfileActivity)
                                    .load(userProfile.photo)
                                    .error(R.drawable.bg_image_error)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imagePreview)
                            } else {
                                Glide.with(this@EditProfileActivity)
                                    .load(R.drawable.bg_image_loading)
                                    .error(R.drawable.bg_image_error)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imagePreview)
                            }

                            inputProfileName.editText?.setText(userProfile.name)
                            inputProfileUsername.editText?.setText(userProfile.username)

                            userProfile.address?.let {
                                if (it.split('|').size == 4) {
                                    val addressStreetName = it.split('|')[0]
                                    val addressCity = it.split('|')[1]
                                    val addressProvince = it.split('|')[2]
                                    val addressPostalCode = it.split('|')[3]

                                    inputProfileAddress.editText?.setText(addressStreetName)
                                    addressCity.let {
                                        val index =
                                            cityList.mapIndexed { index, s -> if (s.cityName == addressCity) index else null }
                                                .filterNotNull().toList()
                                        inputProfileAddressCity.setSelection(index.first())
                                    }
                                    addressProvince.let {
                                        val index =
                                            provinceList.mapIndexed { index, s -> if (s.province == addressProvince) index else null }
                                                .filterNotNull().toList()
                                        inputProfileAddressProvince.setSelection(index.first())
                                    }
                                    addressPostalCode.let {
                                        inputProfileAddressPostalCode.editText?.setText(
                                            addressPostalCode
                                        )
                                    }
                                }
                            }

                            when (userProfile.gender) {
                                Gender.MALE -> 0
                                Gender.FEMALE -> 1
                                else -> null
                            }?.let { inputProfileGender.setSelection(it) }
                        }
                    }

                    setVisibilityContent(View.VISIBLE)
                }
                is Resource.Loading -> setVisibilityContent(View.GONE, true)
                is Resource.Error -> {
                    setVisibilityContent(View.GONE, false)
                    binding.errorText.text = resource.message
                }
            }
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

    private fun removeParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
