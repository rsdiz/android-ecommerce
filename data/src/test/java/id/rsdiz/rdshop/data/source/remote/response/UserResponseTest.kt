package id.rsdiz.rdshop.data.source.remote.response

import id.rsdiz.rdshop.data.factory.UserFactory
import id.rsdiz.rdshop.data.source.remote.response.user.UserResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class UserResponseTest {
    private lateinit var userResponse: UserResponse

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        userResponse = UserFactory.makeUserResponse()
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val userResponse1 = UserResponse(
            userResponse.id,
            userResponse.username,
            userResponse.email,
            userResponse.name,
            userResponse.gender,
            userResponse.address,
            userResponse.photo,
            userResponse.role
        )

        val userResponse2 = UserResponse(
            userResponse.id,
            userResponse.username,
            userResponse.email,
            userResponse.name,
            userResponse.gender,
            userResponse.address,
            userResponse.photo,
            userResponse.role
        )

        assertTrue(userResponse1 == userResponse2 && userResponse2 == userResponse1)
        assertEquals(userResponse1.hashCode(), userResponse2.hashCode())
    }
}
