package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import br.infnet.smpa_gabriel_justino_assessmnt.MyActionState
import br.infnet.smpa_gabriel_justino_assessmnt.PossibleActions
import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import br.infnet.smpa_gabriel_justino_assessmnt.services.MyEncriptionHandler
import kotlinx.coroutines.launch
import java.util.stream.Stream

class ListUserNotesViewModelFactory(
    private val myFirestoreUserId:String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListUserNotesViewModel::class.java)) {
            return ListUserNotesViewModel(myFirestoreUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class ListUserNotesViewModel(private val myFirestoreUserId:String) : ViewModel() {


    val actionsState = MutableLiveData<MyActionState>()

    val notesList = MutableLiveData<MutableList<UserNote>>()

    val service = MyEncriptionHandler("${myFirestoreUserId}")


    fun populateNotesList(context: Context){
        val lst = service.listFiles(context)
        notesList.postValue(lst)

    }

    fun writeUserNoteFile(context: Context,title:String,description:String,timeNow:Long,location:Location){
        viewModelScope.launch{

            val locationText ="""
                latitude:${location.latitude},longitude:${location.longitude}
                """.trimIndent()

            val bulkText = """$description ${UserNote.timeStampDivider}
                     $timeNow ${UserNote.locationDivider} $locationText
                     """.trimIndent()
            val path = service.insertEncryptFile(context,
                "$title.txt","$bulkText")
            actionsState.postValue(
                MyActionState(PossibleActions.FILEWRITEN,path)
            )
            println(path)
            populateNotesList(context)

        }
    }


    fun createUserNote(un: UserNote,context: Context) {

        with(un){
            val emptyLocal=  Location("")
            if(title!=null && description !=null && timestamp !=null )
                writeUserNoteFile(context,title,description,timestamp,location?: emptyLocal)
        }

    }
}