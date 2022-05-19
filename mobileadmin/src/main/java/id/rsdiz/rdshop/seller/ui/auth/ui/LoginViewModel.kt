package id.rsdiz.rdshop.seller.ui.auth.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.auth.AuthInteractor
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authInteractor: AuthInteractor) : ViewModel() {
    suspend fun signIn(login: String, password: String) = authInteractor.signIn(login, password)
}
