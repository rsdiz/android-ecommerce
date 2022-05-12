package id.rsdiz.rdshop.ui.home.ui.profile

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.domain.usecase.auth.AuthUseCase
import id.rsdiz.rdshop.domain.usecase.ongkir.OngkirUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val ongkirUseCase: OngkirUseCase
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()

    fun refreshUser() {
        reloadTrigger.value = true
    }

    init {
        refreshUser()
    }

    fun getUser(userId: String) = reloadTrigger.switchMap {
        userUseCase.getUser(userId).asLiveData(viewModelScope.coroutineContext)
    }.asFlow()

    suspend fun getCities() = ongkirUseCase.getCities()

    suspend fun getProvinces() = ongkirUseCase.getProvinces()

    suspend fun signOut(token: String) = authUseCase.signOut(token)

    suspend fun updateUserProfile(user: User, password: String, profileImage: File?) =
        userUseCase.updateUser(user = user, password = password, sourceFile = profileImage)
}
