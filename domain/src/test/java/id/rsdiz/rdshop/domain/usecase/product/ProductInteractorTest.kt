package id.rsdiz.rdshop.domain.usecase.product

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import id.rsdiz.rdshop.MainCoroutineRule
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.factory.DataFactory
import id.rsdiz.rdshop.data.factory.ProductFactory
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.domain.repository.product.IProductRepository
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
 * Class for testing [ProductInteractor] class
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ProductInteractorTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var productList: List<Product>
    private lateinit var product: Product
    private lateinit var productInteractor: ProductInteractor

    @Mock
    private lateinit var productRepository: IProductRepository

    @Mock
    private lateinit var resourceProductObserver: Observer<Resource<Product>>

    @Mock
    private lateinit var pagingDataProductObserver: Observer<PagingData<Product>>

    @Captor
    private lateinit var captorPagingDataProduct: ArgumentCaptor<PagingData<Product>>

    @Captor
    private lateinit var captorResourceProduct: ArgumentCaptor<Resource<Product>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val list = mutableListOf<Product>()
        list.addAll(
            ProductFactory.makeProductList(
                count = 5,
                categoryId = DataFactory.randomString()
            )
        )
        list.addAll(
            ProductFactory.makeProductList(
                count = 5,
                categoryId = DataFactory.randomString()
            )
        )

        productList = list
        product = list[0]
        productInteractor = ProductInteractor(productRepository)
    }

    @Test
    fun count() = mainCoroutineRule.runTest {
        val resource = Resource.Success(data = productList.size)

        Mockito.`when`(productRepository.count()).thenReturn(resource)
        val countProduct = productRepository.count()

        assertEquals(productList.size, countProduct.data)
    }

    @Test
    fun getProducts() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(PagingData.empty())
            delay(10)
            emit((PagingData.from(productList)))
        }
        // Triggers the transformation
        Mockito.`when`(productRepository.getProducts()).thenReturn(flow)
        val productList = productRepository.getProducts().asLiveData()
        productList.observeForever(pagingDataProductObserver)

        Mockito.verify(pagingDataProductObserver).onChanged(captorPagingDataProduct.capture())

        mainCoroutineRule.advanceTimeBy(10)

        Mockito.verify(pagingDataProductObserver, Mockito.times(2))
            .onChanged(captorPagingDataProduct.capture())
        assertEquals(productList.value, captorPagingDataProduct.value)
    }

    @Test
    fun getProduct() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(Resource.Loading())
            delay(10)
            emit(Resource.Success(product))
        }

        Mockito.`when`(productRepository.getProduct(product.productId)).thenReturn(flow)
        val getProduct = productRepository.getProduct(product.productId).asLiveData()
        getProduct.observeForever(resourceProductObserver)

        Mockito.verify(resourceProductObserver).onChanged(captorResourceProduct.capture())
        assertEquals(true, captorResourceProduct.value is Resource.Loading)

        mainCoroutineRule.advanceTimeBy(10)

        Mockito.verify(resourceProductObserver, Mockito.times(2))
            .onChanged(captorResourceProduct.capture())
        assertEquals(product, captorResourceProduct.value.data)
    }

    @Test
    fun searchProduct() = mainCoroutineRule.runTest {
        val category1 = productList[0].categoryId
        val category2 = productList[5].categoryId

        val dataCategory1 = productList.filter {
            it.categoryId == category1
        }
        val dataCategory2 = productList.filter {
            it.categoryId == category2
        }

        val resource1 = Resource.Success(dataCategory1)
        val resource2 = Resource.Success(dataCategory2)

        // Triggers the transformation category 1
        Mockito.`when`(productRepository.searchProduct(category1)).thenReturn(resource1)
        var search = productRepository.searchProduct(category1)

        assertEquals(dataCategory1, search.data)

        // Triggers the transformation category 1
        Mockito.`when`(productRepository.searchProduct(category2)).thenReturn(resource2)
        search = productRepository.searchProduct(category2)

        assertEquals(dataCategory2, search.data)
    }
}
