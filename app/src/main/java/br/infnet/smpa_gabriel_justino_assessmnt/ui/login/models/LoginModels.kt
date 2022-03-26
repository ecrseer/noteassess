package br.infnet.smpa_gabriel_justino_assessmnt.ui.login.models

import com.google.firebase.firestore.DocumentId
import java.io.IOException

class LoginModels {
    sealed class Result<out T : Any> {

        data class Success<out T : Any>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()

        override fun toString(): String {
            return when (this) {
                is Success<*> -> "Success[data=$data]"
                is Error -> "Error[exception=$exception]"
            }
        }
    }
    data class LoggedInUser(
        val userId: String,
        val displayName: String
    )
    class LoginDataSource {

        fun login(username: String, password: String): Result<LoggedInUser> {
            try {
                // TODO: handle loggedInUser authentication
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
                return Result.Success(fakeUser)
            } catch (e: Throwable) {
                return Result.Error(IOException("Error logging in", e))
            }
        }

        fun logout() {
            // TODO: revoke authentication
        }
    }


    class LoginRepository(val dataSource: LoginDataSource) {

        // in-memory cache of the loggedInUser object
        var user: LoggedInUser? = null
            private set

        val isLoggedIn: Boolean
            get() = user != null

        init {
            // If user credentials will be cached in local storage, it is recommended it be encrypted
            // @see https://developer.android.com/training/articles/keystore
            user = null
        }

        fun logout() {
            user = null
            dataSource.logout()
        }

        fun login(username: String, password: String): Result<LoggedInUser> {
            // handle login
            val result = dataSource.login(username, password)

            if (result is Result.Success) {
                setLoggedInUser(result.data)
            }

            return result
        }

        private fun setLoggedInUser(loggedInUser: LoggedInUser) {
            this.user = loggedInUser
            // If user credentials will be cached in local storage, it is recommended it be encrypted
            // @see https://developer.android.com/training/articles/keystore
        }
    }
    data class MyFirestoreUser(
        @DocumentId
        val idUser:String? = null,
        val name:String? = null,
        val email:String? = null,
    )
}