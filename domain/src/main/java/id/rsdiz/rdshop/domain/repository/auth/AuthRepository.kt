package id.rsdiz.rdshop.domain.repository.auth

import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.set
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.source.remote.AuthRemoteDataSource
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IAuthRepository] from Domain Layer
 */
@Singleton
class AuthRepository @Inject constructor(
    private val dataSource: AuthRemoteDataSource,
    preference: PreferenceHelper
) : IAuthRepository {
    private val prefs = preference.customPrefs(Consts.PREFERENCE_NAME)

    override suspend fun signUp(
        name: String,
        username: String,
        email: String,
        password: String
    ): Resource<String> =
        when (
            val response = dataSource.signUp(
                name = name,
                username = username,
                email = email,
                password = password
            ).first()
        ) {
            is ApiResponse.Success -> {
                Resource.Success(response.data)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun signIn(login: String, password: String): Resource<String> =
        when (val response = dataSource.signIn(login = login, password = password).first()) {
            is ApiResponse.Success -> {
                dataSource.mapper.mapRemoteToEntity(response.data.user).let {
                    prefs[Consts.PREF_ID] = it.userId
                    prefs[Consts.PREF_USERNAME] = it.username
                    prefs[Consts.PREF_EMAIL] = it.email
                    prefs[Consts.PREF_NAME] = it.name
                    prefs[Consts.PREF_GENDER] = it.gender
                    prefs[Consts.PREF_ADDRESS] = it.address
                    prefs[Consts.PREF_PHOTO] = it.photo
                    prefs[Consts.PREF_ROLE] = it.role
                }

                prefs[Consts.PREF_TOKEN] = response.data.apiKey

                Resource.Success(response.data.message)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun signOut(apiKey: String): Resource<String> =
        when (val response = dataSource.signOut(token = apiKey).first()) {
            is ApiResponse.Success -> {
                Resource.Success(response.data)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }
}
