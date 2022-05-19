package id.rsdiz.rdshop.seller.ui.home.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userInteractor: UserInteractor
) : ViewModel() {
    fun getUsers() = userInteractor.getUsers().cachedIn(viewModelScope)
}
