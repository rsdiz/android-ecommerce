package id.rsdiz.rdshop.data.source.local.entity

import id.rsdiz.rdshop.data.factory.UserFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class UserEntityTest {
    private lateinit var userEntity: UserEntity

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        userEntity = UserFactory.makeUserEntity(Role.ADMIN)
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val userEntity1 = UserEntity(
            userEntity.userId,
            userEntity.username,
            userEntity.email,
            userEntity.name,
            userEntity.gender,
            userEntity.address,
            userEntity.photo,
            userEntity.role
        )

        val userEntity2 = UserEntity(
            userEntity.userId,
            userEntity.username,
            userEntity.email,
            userEntity.name,
            userEntity.gender,
            userEntity.address,
            userEntity.photo,
            userEntity.role
        )

        assertTrue(userEntity1 == userEntity2 && userEntity2 == userEntity1)
        assertEquals(userEntity1.hashCode(), userEntity2.hashCode())
    }
}