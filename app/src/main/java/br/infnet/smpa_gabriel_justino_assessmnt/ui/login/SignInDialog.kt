package br.infnet.smpa_gabriel_justino_assessmnt.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.AuthResult

class SignInDialog: LoginDialogFragmentOpen() {
    override fun confirmBtnAction(email: String, password: String) {

        val currentactivity = requireActivity()
        currentactivity?.let {

            with(activityViewModel) {
                val loginSucessListener = { taskResult: AuthResult ->
                    mUserLiveData.postValue(mAuth!!.currentUser)
                    dismiss()
                    Toast.makeText(
                        it, "bem vindo de volta",
                        Toast.LENGTH_LONG + 4242
                    ).show()


                }
                val loginFailListener = { exception: Exception ->

                    Log.d("ERRO LOGIN/CREATE", "${exception!!.message}")
                    Toast.makeText(
                        it, "Falha na Autenticação",
                        Toast.LENGTH_SHORT
                    ).show()

                    mUserLiveData.postValue(null)

                }
                println(mAuth)
                with(mAuth?.signInWithEmailAndPassword(email, password)) {
                    this?.addOnSuccessListener(requireActivity(), loginSucessListener)
                    this?.addOnFailureListener(requireActivity(), loginFailListener)
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setDialogTitle("Entre na sua conta")
        super.onViewCreated(view, savedInstanceState)
    }
}