package id.rsdiz.rdshop.data.factory

import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import id.rsdiz.rdshop.data.source.remote.response.user.UserResponse

/**
 * This class is used to generate [User], [UserEntity], and [UserResponse] for usage on tests
 */
object UserFactory {
    /**
     * Return a [User]
     */
    fun makeUser(role: Role = Role.CUSTOMER): User =
        User(
            userId = DataFactory.randomString(),
            email = DataFactory.randomString(),
            username = DataFactory.randomString(),
            name = DataFactory.randomString(),
            gender = DataFactory.randomGender(),
            address = DataFactory.randomString(),
            photo = DataFactory.randomString(),
            role = role
        )

    /**
     * Return a list of [User]
     */
    fun makeUserList(count: Int = 5, role: Role): List<User> {
        val userList = mutableListOf<User>()
        repeat(count) {
            userList.add(makeUser(role))
        }
        return userList
    }

    /**
     * Return a [UserEntity]
     */
    fun makeUserEntity(role: Role = Role.CUSTOMER): UserEntity =
        UserEntity(
            userId = DataFactory.randomString(),
            email = DataFactory.randomString(),
            username = DataFactory.randomString(),
            name = DataFactory.randomString(),
            gender = DataFactory.randomGender(),
            address = DataFactory.randomString(),
            photo = DataFactory.randomString(),
            role = role
        )

    /**
     * Return a list of [UserEntity]
     */
    fun makeUserEntityList(count: Int = 5, role: Role): List<UserEntity> {
        val userList = mutableListOf<UserEntity>()
        repeat(count) {
            userList.add(makeUserEntity(role))
        }
        return userList
    }

    /**
     * Return a [UserResponse]
     */
    fun makeUserResponse(role: Role = Role.CUSTOMER): UserResponse =
        UserResponse(
            id = DataFactory.randomString(),
            email = DataFactory.randomString(),
            username = DataFactory.randomString(),
            name = DataFactory.randomString(),
            gender = DataFactory.randomGender().name,
            address = DataFactory.randomString(),
            photo = DataFactory.randomString(),
            role = role.name
        )

    /**
     * Return a list of [UserResponse]
     */
    fun makeUserResponseList(count: Int = 5, role: Role): List<UserResponse> {
        val userList = mutableListOf<UserResponse>()
        repeat(count) {
            userList.add(makeUserResponse(role))
        }
        return userList
    }
}
