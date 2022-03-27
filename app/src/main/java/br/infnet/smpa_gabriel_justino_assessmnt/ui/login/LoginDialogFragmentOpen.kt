package br.infnet.smpa_gabriel_justino_assessmnt.ui.login

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import br.infnet.smpa_gabriel_justino_assessmnt.MainActivityViewModel
import br.infnet.smpa_gabriel_justino_assessmnt.R
import com.google.android.material.snackbar.Snackbar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginDialogFragmentOpen.newInstance] factory method to
 * create an instance of this fragment.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}


open class LoginDialogFragmentOpen : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val loginDialogViewModel: LoginDialogFragmentOpenViewModel by viewModels(){
        LoginViewModelFactory()
    }
    val activityViewModel: MainActivityViewModel by activityViewModels()
    lateinit var titleDialogField: TextView
    lateinit var emailField: EditText
    lateinit var passwordField: EditText
    lateinit var confirmButton: Button

    lateinit var loading: ProgressBar


    private var dialogTitle:String = "Login dialog"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }

    private fun setupObservers(username:EditText,password:EditText,login:Button){
        with(loginDialogViewModel){
            loginFormState.observe(viewLifecycleOwner, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    username.error = getString(loginState.usernameError)
                }
                if (loginState.passwordError != null) {
                    password.error = getString(loginState.passwordError)
                }
            })
            loginResult.observe(viewLifecycleOwner, Observer {
                val loginResult = it ?: return@Observer

                loading.visibility = View.GONE
                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                /*if (loginResult.success != null) {
                    updateUiWithUser(loginResult.success)
                }*/

                //Complete and destroy login activity once successful
                ///setResult(Activity.RESULT_OK)
                //finish()
            })
        }

    }
    private fun setupFieldsValidations(username:EditText, password:EditText, login:Button) {
        username.afterTextChanged {
            loginDialogViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
            //loginDialogViewModel.searchImage(it)
        }

        password.apply {
            afterTextChanged {
                loginDialogViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginDialogViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginDialogViewModel.login(username.text.toString(), password.text.toString())
                val emailtxt = emailField.text.toString()
                val passwordtxt = passwordField.text.toString()
                confirmBtnClick(emailtxt,passwordtxt)


            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view){
            titleDialogField = findViewById(R.id.txt_dialog_title)
            loading = findViewById(R.id.loading2)
            emailField = findViewById<EditText>(R.id.editTextTextEmailAddress)
            passwordField = findViewById<EditText>(R.id.editTextTextPassword)
            confirmButton = findViewById<Button>(R.id.confirm_dialog_btn)
            confirmButton.setText("login")
        }

        setupObservers(emailField,passwordField, confirmButton)
        titleDialogField.text = dialogTitle

        setupFieldsValidations(emailField,passwordField,confirmButton)
    }
    private fun confirmBtnClick(email:String,password:String){
        loading.visibility = View.VISIBLE
        confirmBtnAction( email,password)
    }
    open fun confirmBtnAction(email:String,password:String){}

    fun setDialogTitle(newTitle:String){
        dialogTitle = newTitle
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
    fun snackAlert(text: String) {
        val up_layout: View = emailField
        Snackbar.make(up_layout, "  ${text}", Snackbar.LENGTH_LONG + 4242).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginDialogFragmentOpen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginDialogFragmentOpen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private lateinit var viewModel: LoginDialogFragmentOpenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_dialog_fragment_open, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginDialogFragmentOpenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}