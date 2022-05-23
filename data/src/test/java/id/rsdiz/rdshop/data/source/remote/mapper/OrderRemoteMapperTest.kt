package id.rsdiz.rdshop.data.source.remote.mapper

import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import id.rsdiz.rdshop.base.utils.toOffsetDateTime
import id.rsdiz.rdshop.data.factory.OrderFactory
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class OrderRemoteMapperTest {
    private lateinit var orderRemoteMapper: OrderRemoteMapper

    @Mock
    private lateinit var context: Context

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        AndroidThreeTen.init(context)
        orderRemoteMapper = OrderRemoteMapper()
    }

    /**
     * Function for testing [OrderRemoteMapper.mapRemoteToEntity] method
     */
    @Test
    fun mapRemoteToEntity() {
        val orderRemote = OrderFactory.makeOrderResponse()
        val orderEntity = orderRemoteMapper.mapRemoteToEntity(orderRemote)

        assertOrderDataEquals(orderEntity, orderRemote)
    }

    /**
     * Function for testing [OrderRemoteMapper.mapRemoteToEntities] method
     */
    @Test
    fun mapRemoteToEntities() {
        val orderRemoteList = OrderFactory.makeOrderResponseList()
        val orderEntities = orderRemoteMapper.mapRemoteToEntities(orderRemoteList)

        repeat(orderRemoteList.size) {
            assertOrderDataEquals(orderEntities[it], orderRemoteList[it])
        }
    }

    private fun assertOrderDataEquals(entity: OrderWithDetails, response: OrderResponse) {
        assertEquals(entity.order.orderId, response.id)
        assertEquals(entity.order.userId, response.userId)
        assertEquals(entity.order.date, response.date.toOffsetDateTime())
        assertEquals(entity.order.amount, response.amount)
        assertEquals(entity.order.shipName, response.shipName)
        assertEquals(entity.order.shipAddress, response.shipAddress)
        assertEquals(entity.order.shippingCost, response.shippingCost)
        assertEquals(entity.order.phone, response.phone)
        assertEquals(entity.order.status, response.status)
        assertEquals(entity.order.trackingNumber, response.trackingNumber)

        repeat(entity.details.size) {
            assertEquals(entity.details[it].detailId, response.detailOrder[it].id)
            assertEquals(entity.details[it].productId, response.detailOrder[it].productId)
            assertEquals(entity.details[it].price, response.detailOrder[it].price)
            assertEquals(entity.details[it].quantity, response.detailOrder[it].quantity)
        }
    }
}
