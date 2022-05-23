package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.factory.UserFactory
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class UserMapperTest {
    private lateinit var userMapper: UserMapper

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        userMapper = UserMapper()
    }

    /**
     * Function for testing [UserMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntity() {
        val userEntity = UserFactory.makeUserEntity()
        val user = userMapper.mapFromEntity(userEntity)

        assertUserDataEquals(userEntity, user)
    }

    /**
     * Function for testing [UserMapper.mapToEntity] method
     */
    @Test
    fun mapToEntity() {
        val user = UserFactory.makeUser()
        val userEntity = userMapper.mapToEntity(user)

        assertUserDataEquals(userEntity, user)
    }

    /**
     * Function for testing [UserMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntities() {
        val userEntities = UserFactory.makeUserEntityList(role = Role.ADMIN)
        val userList = userMapper.mapFromEntities(userEntities)

        repeat(userEntities.size) {
            assertUserDataEquals(userEntities[it], userList[it])
        }
    }

    private fun assertUserDataEquals(entity: UserEntity, user: User) {
        assertEquals(entity.userId, user.userId)
        assertEquals(entity.username, user.username)
        assertEquals(entity.name, user.name)
        assertEquals(entity.gender, user.gender)
        assertEquals(entity.address, user.address)
        assertEquals(entity.photo, user.photo)
        assertEquals(entity.role, user.role)
    }
}
