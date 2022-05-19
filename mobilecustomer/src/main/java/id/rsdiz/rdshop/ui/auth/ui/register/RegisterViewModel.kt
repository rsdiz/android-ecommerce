package id.rsdiz.rdshop.ui.auth.ui.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.auth.AuthInteractor
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authInteractor: AuthInteractor) : ViewModel() {
    suspend fun signUp(name: String, username: String, email: String, password: String) = authInteractor.signUp(
        name = name,
        username = username,
        email = email,
        password = password
    )
}
