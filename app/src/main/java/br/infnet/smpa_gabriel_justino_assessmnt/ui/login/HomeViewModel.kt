package br.infnet.smpa_gabriel_justino_assessmnt.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class PossibleActions{
    FILEWRITEN
}
class MyActionState(
    var lastActionTaken:PossibleActions,
    var filePathChanged:String)
class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

}