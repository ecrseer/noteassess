package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.content.Context
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

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    val actionsState = MutableLiveData<MyActionState>()

    val text: LiveData<String> = _text
    val notesList = MutableLiveData<MutableList<UserNote>>()

    val service = MyEncriptionHandler("${myFirestoreUserId}")


    fun populateNotesList(context: Context){
        val lst = service.listFiles(context)
        notesList.postValue(lst)

    }

    fun writeUserNoteFile(context: Context,title:String,description:String,timeNow:Long){
        viewModelScope.launch{

            val bulkText = "$description ${UserNote.timeStampDivider} $timeNow"
            val path = service.insertEncryptFile(context,
                "$title.txt","$bulkText")
            actionsState.postValue(
                MyActionState(PossibleActions.FILEWRITEN,path)
            )
            println(path)


        }
    }

    fun readS(context: Context){
        viewModelScope.launch{
            val strings = service.readEncryptFile(context,"abc.txt")
            actionsState.postValue(
                MyActionState(PossibleActions.FILEWRITEN,strings)
            )
            println(strings)
        }
    }


    fun createUserNote(un: UserNote,context: Context) {

        with(un){
            if(title!=null && description !=null && timestamp !=null)
                writeUserNoteFile(context,title,description,timestamp)
        }
    }
}