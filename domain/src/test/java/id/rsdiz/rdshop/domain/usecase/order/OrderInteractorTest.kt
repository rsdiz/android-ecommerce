package id.rsdiz.rdshop.domain.usecase.order

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.jakewharton.threetenabp.AndroidThreeTen
import id.rsdiz.rdshop.MainCoroutineRule
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.factory.OrderFactory
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.domain.repository.order.IOrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*

/**
 * Class for testing [OrderInteractor] class
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class OrderInteractorTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var orderList: List<Order>
    private lateinit var order: Order
    private lateinit var orderInteractor: OrderInteractor

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var orderRepository: IOrderRepository

    @Mock
    private lateinit var resourceOrderObserver: Observer<Resource<Order>>

    @Mock
    private lateinit var pagingDataOrderObserver: Observer<PagingData<Order>>

    @Captor
    private lateinit var captorPagingDataOrder: ArgumentCaptor<PagingData<Order>>

    @Captor
    private lateinit var captorResourceOrder: ArgumentCaptor<Resource<Order>>

    @Mock
    private lateinit var resourceOrderListObserver: Observer<Resource<List<Order>>>

    @Captor
    private lateinit var captorResourcesOrder: ArgumentCaptor<Resource<List<Order>>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        AndroidThreeTen.init(context)
        orderList = OrderFactory.makeOrderList(count = 10)
        order = orderList[0]
        orderInteractor = OrderInteractor(orderRepository)
    }

    @Test
    fun count() = mainCoroutineRule.runTest {
        val resource = Resource.Success(data = orderList.size)
        // Triggers the transformation
        Mockito.`when`(orderRepository.count()).thenReturn(resource)
        val countOrder = orderRepository.count()

        assertEquals(orderList.size, countOrder.data)
    }

    @Test
    fun getOrders() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(PagingData.empty())
            delay(10)
            emit((PagingData.from(orderList)))
        }
        // Triggers the transformation
        Mockito.`when`(orderRepository.getOrders(null)).thenReturn(flow)
        val orderList = orderRepository.getOrders(null).asLiveData()
        orderList.observeForever(pagingDataOrderObserver)
        // Received first state
        Mockito.verify(pagingDataOrderObserver).onChanged(captorPagingDataOrder.capture())

        mainCoroutineRule.advanceTimeBy(10)
        // Received second state
        Mockito.verify(pagingDataOrderObserver, Mockito.times(2))
            .onChanged(captorPagingDataOrder.capture())
        assertEquals(orderList.value, captorPagingDataOrder.value)
    }

    @Test
    fun getNewestOrders() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(Resource.Loading())
            delay(10)
            emit(Resource.Success(orderList))
        }
        // Triggers the transformation
        Mockito.`when`(orderRepository.getNewestOrders()).thenReturn(flow)
        val orderList = orderRepository.getNewestOrders().asLiveData()
        orderList.observeForever(resourceOrderListObserver)
        // Received first state = [Resource.Loading]
        Mockito.verify(resourceOrderListObserver).onChanged(captorResourcesOrder.capture())
        assertEquals(true, captorResourcesOrder.value is Resource.Loading)

        mainCoroutineRule.advanceTimeBy(10)
        // Received second state = [Resource.Success]
        Mockito.verify(resourceOrderListObserver, Mockito.times(2))
            .onChanged(captorResourcesOrder.capture())
        assertEquals(orderList.value, captorResourcesOrder.value)
    }

    @Test
    fun getOrder() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(Resource.Loading())
            delay(10)
            emit(Resource.Success(order))
        }
        // Triggers the transformation
        Mockito.`when`(orderRepository.getOrder(order.orderId)).thenReturn(flow)
        val getOrder = orderRepository.getOrder(order.orderId).asLiveData()
        getOrder.observeForever(resourceOrderObserver)
        // Received first state = [Resource.Loading]
        Mockito.verify(resourceOrderObserver).onChanged(captorResourceOrder.capture())
        assertEquals(true, captorResourceOrder.value is Resource.Loading)

        mainCoroutineRule.advanceTimeBy(10)
        // Received second state = [Resource.Success]
        Mockito.verify(resourceOrderObserver, Mockito.times(2))
            .onChanged(captorResourceOrder.capture())
        assertEquals(order, captorResourceOrder.value.data)
    }
}
