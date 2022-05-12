package id.rsdiz.rdshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for City
 */
@Parcelize
data class City(
    val cityId: Int,
    val provinceId: Int,
    val province: String,
    val type: String,
    val cityName: String,
    val postalCode: Int
) : Parcelable
