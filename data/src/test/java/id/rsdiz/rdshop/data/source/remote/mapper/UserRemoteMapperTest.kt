package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.factory.UserFactory
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import id.rsdiz.rdshop.data.source.remote.response.user.UserResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class UserRemoteMapperTest {
    private lateinit var userRemoteMapper: UserRemoteMapper

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        userRemoteMapper = UserRemoteMapper()
    }

    /**
     * Function for testing [UserRemoteMapper.mapRemoteToEntity] method
     */
    @Test
    fun mapRemoteToEntity() {
        val userRemote = UserFactory.makeUserResponse()
        val userEntity = userRemoteMapper.mapRemoteToEntity(userRemote)

        assertUserDataEquals(userEntity, userRemote)
    }

    /**
     * Function for testing [UserRemoteMapper.mapRemoteToEntities] method
     */
    @Test
    fun mapRemoteToEntities() {
        val userRemoteList = UserFactory.makeUserResponseList(role = Role.CUSTOMER)
        val userEntities = userRemoteMapper.mapRemoteToEntities(userRemoteList)

        repeat(userRemoteList.size) {
            assertUserDataEquals(userEntities[it], userRemoteList[it])
        }
    }

    private fun assertUserDataEquals(entity: UserEntity, response: UserResponse) {
        assertEquals(entity.userId, response.id)
        assertEquals(entity.username, response.username)
        assertEquals(entity.email, response.email)
        assertEquals(entity.gender?.name, response.gender)
        assertEquals(entity.address, response.address)
        assertEquals(entity.photo, response.photo)
        assertEquals(entity.name, response.name)
        assertEquals(entity.role.name, response.role)
    }
}
