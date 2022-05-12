package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Province
 */
@Parcelize
data class Province(
    val provinceId: Int,
    val province: String
) : Parcelable
