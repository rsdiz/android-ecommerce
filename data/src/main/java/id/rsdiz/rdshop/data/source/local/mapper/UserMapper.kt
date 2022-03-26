package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import id.rsdiz.rdshop.data.source.local.mapper.base.DataMapper
import id.rsdiz.rdshop.data.model.User
import javax.inject.Inject

/**
 * Mapper for user entity from data to domain layer
 */
open class UserMapper @Inject constructor() : DataMapper<UserEntity, User> {
    override fun mapFromEntity(entity: UserEntity): User =
        User(
            userId = entity.userId,
            email = entity.email,
            username = entity.username,
            name = entity.name,
            gender = entity.gender,
            address = entity.address,
            photo = entity.photo,
            role = entity.role
        )

    override fun mapFromEntities(entities: List<UserEntity>): List<User> =
        entities.map {
            mapFromEntity(it)
        }

    override fun mapToEntity(model: User): UserEntity =
        UserEntity(
            userId = model.userId,
            email = model.email,
            username = model.username,
            name = model.name,
            gender = model.gender,
            address = model.address,
            photo = model.photo,
            role = model.role
        )
}
