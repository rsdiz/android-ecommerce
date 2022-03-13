package id.rsdiz.rdshop.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for User
 */
@Parcelize
data class User(
    val userId: String,
    val email: String,
    val username: String,
    val password: String,
    val name: String,
    val gender: String,
    val address: String,
    val photo: String,
    val role: String
) : Parcelable
