package id.rsdiz.rdshop.domain.model

import android.os.Parcelable
import id.rsdiz.rdshop.data.source.local.entity.Gender
import id.rsdiz.rdshop.data.source.local.entity.Role
import kotlinx.parcelize.Parcelize

/**
 * Model for User
 */
@Parcelize
data class User(
    val userId: String,
    val email: String,
    val username: String,
    val name: String,
    val gender: Gender,
    val address: String,
    val photo: String,
    val role: Role
) : Parcelable
