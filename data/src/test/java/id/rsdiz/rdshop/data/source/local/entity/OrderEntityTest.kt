package id.rsdiz.rdshop.data.source.local.entity

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import id.rsdiz.rdshop.data.factory.DataFactory
import id.rsdiz.rdshop.data.factory.OrderFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class OrderEntityTest {
    private lateinit var orderEntity: OrderEntity

    @Mock
    private lateinit var context: Context

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        AndroidThreeTen.init(context)
        orderEntity = OrderFactory.makeOrderEntity(null)
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val orderEntity1 = OrderEntity(
            orderEntity.orderId,
            orderEntity.userId,
            orderEntity.date,
            orderEntity.amount,
            orderEntity.shipName,
            orderEntity.shipAddress,
            orderEntity.shippingCost,
            orderEntity.phone,
            orderEntity.status,
            orderEntity.trackingNumber
        )

        val orderEntity2 = OrderEntity(
            orderEntity.orderId,
            orderEntity.userId,
            orderEntity.date,
            orderEntity.amount,
            orderEntity.shipName,
            orderEntity.shipAddress,
            orderEntity.shippingCost,
            orderEntity.phone,
            orderEntity.status,
            orderEntity.trackingNumber
        )

        assertTrue(orderEntity1 == orderEntity2 && orderEntity2 == orderEntity1)
        assertEquals(orderEntity1.hashCode(), orderEntity2.hashCode())
    }
}
