package id.rsdiz.rdshop.domain.usecase.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import id.rsdiz.rdshop.MainCoroutineRule
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.factory.UserFactory
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.domain.repository.user.IUserRepository
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
 * Class for testing [UserInteractor] class
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class UserInteractorTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var userList: List<User>
    private lateinit var user: User
    private lateinit var userInteractor: UserInteractor

    @Mock
    private lateinit var userRepository: IUserRepository

    @Mock
    private lateinit var resourceUserObserver: Observer<Resource<User>>

    @Mock
    private lateinit var pagingDataUserObserver: Observer<PagingData<User>>

    @Captor
    private lateinit var captorPagingDataUser: ArgumentCaptor<PagingData<User>>

    @Captor
    private lateinit var captorResourceUser: ArgumentCaptor<Resource<User>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userList = UserFactory.makeUserList(count = 10, role = Role.CUSTOMER)
        user = userList[0]
        userInteractor = UserInteractor(userRepository)
    }

    @Test
    fun count() = mainCoroutineRule.runTest {
        val resource = Resource.Success(data = userList.size)
        // Triggers the transformation
        Mockito.`when`(userRepository.count()).thenReturn(resource)
        val countUser = userRepository.count()

        assertEquals(userList.size, countUser.data)
    }

    @Test
    fun getUsers() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(PagingData.empty())
            delay(10)
            emit((PagingData.from(userList)))
        }
        // Triggers the transformation
        Mockito.`when`(userRepository.getUsers()).thenReturn(flow)
        val userList = userRepository.getUsers().asLiveData()
        userList.observeForever(pagingDataUserObserver)
        // Received first state
        Mockito.verify(pagingDataUserObserver).onChanged(captorPagingDataUser.capture())

        mainCoroutineRule.advanceTimeBy(10)
        // Received second state
        Mockito.verify(pagingDataUserObserver, Mockito.times(2))
            .onChanged(captorPagingDataUser.capture())
        assertEquals(userList.value, captorPagingDataUser.value)
    }

    @Test
    fun getUser() = mainCoroutineRule.runTest {
        val flow = flow {
            emit(Resource.Loading())
            delay(10)
            emit(Resource.Success(user))
        }
        // Triggers the transformation
        Mockito.`when`(userRepository.getUser(user.userId)).thenReturn(flow)
        val getUser = userRepository.getUser(user.userId).asLiveData()
        getUser.observeForever(resourceUserObserver)
        // Received first state = [Resource.Loading]
        Mockito.verify(resourceUserObserver).onChanged(captorResourceUser.capture())
        assertEquals(true, captorResourceUser.value is Resource.Loading)

        mainCoroutineRule.advanceTimeBy(10)
        // Received second state = [Resource.Success]
        Mockito.verify(resourceUserObserver, Mockito.times(2))
            .onChanged(captorResourceUser.capture())
        assertEquals(user, captorResourceUser.value.data)
    }

    @Test
    fun searchUser() = mainCoroutineRule.runTest {
        val dataSearched = userList.filter { it.name == user.name }

        val resource = Resource.Success(dataSearched)
        // Triggers the transformation
        Mockito.`when`(userRepository.searchUser(user.name)).thenReturn(resource)
        val search = userRepository.searchUser(user.name)

        assertEquals(user, search.data?.get(0))
    }
}
