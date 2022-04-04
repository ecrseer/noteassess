package br.infnet.smpa_gabriel_justino_assessmnt

import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.*
import br.infnet.smpa_gabriel_justino_assessmnt.services.MyEncriptionHandler
import br.infnet.smpa_gabriel_justino_assessmnt.services.MyLocationHandler
import br.infnet.smpa_gabriel_justino_assessmnt.ui.login.models.LoginModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

enum class PossibleActions{
    FILEWRITEN,EMPTY
}
class MyActionState(
    var lastActionTaken:PossibleActions,
    var filePathChanged:String)

class MainActivityViewModel:ViewModel() {
    val actionsState = MutableLiveData<MyActionState>()

    val service = MyEncriptionHandler("ga@bris.com")


    fun writeS(context: Context){
        viewModelScope.launch{
            val path = service.insertEncryptFile(context,
                "abc.txt","azulin")
            actionsState.postValue(
                MyActionState(PossibleActions.FILEWRITEN,path)
            )
            println(path)


        }
    }
    fun readS(context:Context){
        viewModelScope.launch{
            val strings = service.readEncryptFile(context,"abc.txt")
            actionsState.postValue(
                MyActionState(PossibleActions.FILEWRITEN,strings)
            )
            println(strings)
        }
    }
    fun gpsS(context: Context, locationManager: LocationManager){
        viewModelScope.launch {
            val lh = MyLocationHandler(context)
            val result = lh.getLastLocation(locationManager,null)
            if (result==null){
                println("localizacao sem permissao")
            }else{
                println("localizacao eh ${result.altitude}")
            }
        }
    }

    var mAuth: FirebaseAuth? = null

    val mUserLiveData = MutableLiveData<FirebaseUser?>().apply { value = null }

    fun getLiveDataWhenOtherLiveDataChange(
        changedLiveData: MutableLiveData<FirebaseUser?>,
        functionWhichReturnsNewValue: (FirebaseUser?) -> Boolean
    ): LiveData<Boolean> {
        return Transformations.map(changedLiveData, functionWhichReturnsNewValue)
    }

    val isUserLoggedIn: LiveData<Boolean> =
        getLiveDataWhenOtherLiveDataChange(mUserLiveData, { user ->
            user != null
        })
    val firestoreUser = MutableLiveData<LoginModels.MyFirestoreUser>()

    init {
        mAuth = FirebaseAuth.getInstance()
        mAuth?.let {
            mUserLiveData.postValue(it.currentUser)
        }
    }

    fun logout() {
        mAuth?.signOut()
        mUserLiveData.postValue(null)
    }

    private fun createUserByAuth() {
        val mUser: FirebaseUser? = mUserLiveData.value
        mUser?.let {

            val users_collection = Firebase.firestore.collection("users")
            //val tvShowSample = Tvshow(21, "friends", "#http")
            val newuser: LoginModels.MyFirestoreUser? = mUser.email?.let {
                LoginModels.MyFirestoreUser(mUser.uid, mUser.displayName, it)
            }
            if (newuser != null) {
                val task = users_collection.document(mUser.uid).set(newuser)
                task?.addOnSuccessListener {
                    val ldf = it
                    val dd = ""
                }
            }

        }

    }

    fun verifyCurrentUser() {
        mUserLiveData?.value?.let {
            val db = Firebase.firestore
            val users_collection = db.collection("users")
            val userUid = it.uid
            val test = users_collection.document(userUid).get()

            test.addOnFailureListener {

                val dd = 4

            }
            test.addOnSuccessListener { document ->
                val createdUser = document?.toObject(LoginModels.MyFirestoreUser::class.java)
                if (createdUser == null) {
                    createUserByAuth()
                } else {
                    firestoreUser.postValue(createdUser!!)
                }
            }

        }

    }
    val preventNavigation = MutableLiveData<Boolean>().apply { value = false }

    fun preventMyWatchNavigation() {
        preventNavigation.value=true
    }
}