package id.rsdiz.rdshop.ui.home.ui.profile

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.domain.usecase.auth.AuthInteractor
import id.rsdiz.rdshop.domain.usecase.ongkir.OngkirInteractor
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val userInteractor: UserInteractor,
    private val ongkirInteractor: OngkirInteractor
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()

    fun refreshUser() {
        reloadTrigger.value = true
    }

    init {
        refreshUser()
    }

    fun getUser(userId: String) = reloadTrigger.switchMap {
        userInteractor.getUser(userId).asLiveData(viewModelScope.coroutineContext)
    }.asFlow()

    suspend fun getCities() = ongkirInteractor.getCities()

    suspend fun getProvinces() = ongkirInteractor.getProvinces()

    suspend fun signOut(token: String) = authInteractor.signOut(token)

    suspend fun updateUserProfile(user: User, password: String, profileImage: File?) =
        userInteractor.updateUser(user = user, password = password, sourceFile = profileImage)
}
