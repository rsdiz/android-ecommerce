package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.source.local.entity.Gender
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import id.rsdiz.rdshop.data.source.remote.mapper.base.EntityMapper
import id.rsdiz.rdshop.data.source.remote.response.user.UserResponse
import javax.inject.Inject

/**
 * Maps a User from remote (API) to our model
 */
open class UserRemoteMapper @Inject constructor() : EntityMapper<UserResponse, UserEntity> {
    override fun mapRemoteToEntity(remote: UserResponse): UserEntity {
        val gender = when (remote.gender) {
            "male" -> Gender.MALE
            "female" -> Gender.FEMALE
            else -> Gender.OTHER
        }

        val role = when (remote.role) {
            "admin" -> Role.ADMIN
            else -> Role.CUSTOMER
        }

        return UserEntity(
            userId = remote.id,
            username = remote.username,
            email = remote.email,
            name = remote.name,
            gender = gender,
            address = remote.address,
            photo = remote.photo,
            role = role
        )
    }

    override fun mapRemoteToEntities(remotes: List<UserResponse>): List<UserEntity> {
        val list = mutableListOf<UserEntity>()

        remotes.map {
            list.add(mapRemoteToEntity(it))
        }

        return list
    }
}
