package id.rsdiz.rdshop.data.source.remote.response

import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import id.rsdiz.rdshop.data.factory.OrderFactory
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class OrderResponseTest {
    private lateinit var orderResponse: OrderResponse

    @Mock
    private lateinit var context: Context

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        AndroidThreeTen.init(context)
        orderResponse = OrderFactory.makeOrderResponse()
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val orderResponse1 = OrderResponse(
            orderResponse.id,
            orderResponse.userId,
            orderResponse.date,
            orderResponse.amount,
            orderResponse.shipName,
            orderResponse.shipAddress,
            orderResponse.shippingCost,
            orderResponse.phone,
            orderResponse.status,
            orderResponse.trackingNumber,
            orderResponse.detailOrder
        )

        val orderResponse2 = OrderResponse(
            orderResponse.id,
            orderResponse.userId,
            orderResponse.date,
            orderResponse.amount,
            orderResponse.shipName,
            orderResponse.shipAddress,
            orderResponse.shippingCost,
            orderResponse.phone,
            orderResponse.status,
            orderResponse.trackingNumber,
            orderResponse.detailOrder
        )

        assertTrue(orderResponse1 == orderResponse2 && orderResponse2 == orderResponse1)
        assertEquals(orderResponse1.hashCode(), orderResponse2.hashCode())
    }
}
