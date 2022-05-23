package id.rsdiz.rdshop.domain.usecase.category

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import id.rsdiz.rdshop.MainCoroutineRule
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.factory.CategoryFactory
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.domain.repository.category.ICategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*

/**
 * Class for testing [CategoryInteractor] class
 */
@ExperimentalCoroutinesApi
class CategoryInteractorTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var categoryList: List<Category>
    private lateinit var category: Category
    private lateinit var categoryInteractor: CategoryInteractor

    @Mock
    private lateinit var categoryRepository: ICategoryRepository

    @Mock
    private lateinit var resourceCategoriesObserver: Observer<Resource<List<Category>>>

    @Captor
    private lateinit var captorResourcesCategory: ArgumentCaptor<Resource<List<Category>>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        categoryList = CategoryFactory.makeCategories(count = 10)
        category = categoryList[0]
        categoryInteractor = CategoryInteractor(categoryRepository)
    }

    @Test
    fun count() = mainCoroutineRule.runTest {
        val resource = Resource.Success(data = categoryList.size)
        // Triggers the transformation
        Mockito.`when`(categoryRepository.count()).thenReturn(resource)
        val countCategory = categoryRepository.count()

        assertEquals(categoryList.size, countCategory.data)
    }

    @Test
    fun getCategories() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(Resource.Loading())
            delay(10)
            emit(Resource.Success(categoryList))
        }
        // Triggers the transformation
        Mockito.`when`(categoryRepository.getCategories()).thenReturn(flow)
        val categoryList = categoryRepository.getCategories().asLiveData()
        categoryList.observeForever(resourceCategoriesObserver)
        // Received first state
        Mockito.verify(resourceCategoriesObserver).onChanged(captorResourcesCategory.capture())
        assertEquals(true, captorResourcesCategory.value is Resource.Loading)

        mainCoroutineRule.advanceTimeBy(10)
        // Received second state
        Mockito.verify(resourceCategoriesObserver, Mockito.times(2))
            .onChanged(captorResourcesCategory.capture())
        assertEquals(categoryList.value, captorResourcesCategory.value)
    }
}
