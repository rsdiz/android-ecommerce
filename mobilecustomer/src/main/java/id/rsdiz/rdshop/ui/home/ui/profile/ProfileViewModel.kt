package id.rsdiz.rdshop.ui.home.ui.profile

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.auth.AuthUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase
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

    suspend fun signOut(token: String) = authUseCase.signOut(token)
}
