package id.rsdiz.rdshop.data.source.local.mapper

import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import id.rsdiz.rdshop.data.factory.OrderFactory
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class OrderMapperTest {
    private lateinit var orderMapper: OrderMapper

    @Mock
    private lateinit var context: Context

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        AndroidThreeTen.init(context)
        orderMapper = OrderMapper()
    }

    /**
     * Function for testing [OrderMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntity() {
        val orderEntity = OrderFactory.makeOrderWithDetails()
        val order = orderMapper.mapFromEntity(orderEntity)

        assertOrderDataEquals(orderEntity, order)
    }

    /**
     * Function for testing [OrderMapper.mapToEntity] method
     */
    @Test
    fun mapToEntity() {
        val order = OrderFactory.makeOrder()
        val orderEntity = orderMapper.mapToEntity(order)

        assertOrderDataEquals(orderEntity, order)
    }

    /**
     * Function for testing [OrderMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntities() {
        val orderEntities = OrderFactory.makeOrderWithDetailsList()
        val orderList = orderMapper.mapFromEntities(orderEntities)

        repeat(orderEntities.size) {
            assertOrderDataEquals(orderEntities[it], orderList[it])
        }
    }

    private fun assertOrderDataEquals(entity: OrderWithDetails, order: Order) {
        assertEquals(entity.order.orderId, order.orderId)
        assertEquals(entity.order.userId, order.userId)
        assertEquals(entity.order.date, order.date)
        assertEquals(entity.order.amount, order.amount)
        assertEquals(entity.order.shipName, order.shipName)
        assertEquals(entity.order.shipAddress, order.shipAddress)
        assertEquals(entity.order.shippingCost, order.shippingCost)
        assertEquals(entity.order.phone, order.phone)
        assertEquals(entity.order.status, order.status)
        assertEquals(entity.order.trackingNumber, order.trackingNumber)

        repeat(entity.details.size) {
            assertEquals(entity.details[it].detailId, order.orderDetail[it].detailId)
            assertEquals(entity.details[it].productId, order.orderDetail[it].productId)
            assertEquals(entity.details[it].price, order.orderDetail[it].price)
            assertEquals(entity.details[it].quantity, order.orderDetail[it].quantity)
        }
    }
}
