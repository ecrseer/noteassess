package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import br.infnet.smpa_gabriel_justino_assessmnt.PossibleActions
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentUsernoteRegisterBinding
import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import br.infnet.smpa_gabriel_justino_assessmnt.services.MyLocationHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class UserNoteBottomsheetFragment : BottomSheetDialogFragment() {
    // TODO: Customize parameter argument names
    val ARG_userNote_position = "userNote_position"
    var selectedPosition = -1
    private val viewModel by viewModels<ListUserNotesViewModel>({
        requireParentFragment()
    })
    private var _binding: FragmentUsernoteRegisterBinding? = null

    private lateinit var locationHandler: MyLocationHandler;
    private var lastLocation:Location? = null
    private var requestAtempts = 0
    val REQUESTLOCATION_CODE = 128

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
fun expand(){
    dialog?.setOnShowListener {
        val bottomSheetDialog = it as BottomSheetDialog
        val parentLayout = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
        parentLayout?.let { bottomSheet ->
            val behaviour = BottomSheetBehavior.from(bottomSheet)
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
            behaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED+2
        }
    }

}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUsernoteRegisterBinding.inflate(inflater, container, false)

        viewModel.actionsState.observe(viewLifecycleOwner, Observer {actionState->
            if(actionState.lastActionTaken == PossibleActions.FILEWRITEN){
                Toast.makeText(requireContext(),"Arquivo escrito em "+actionState.filePathChanged,
                    Toast.LENGTH_LONG).show()
                actionState.lastActionTaken = PossibleActions.EMPTY
                dismiss()
            }
        })

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            btnUsernoteSave.setOnClickListener {
                val time = Calendar.getInstance().timeInMillis
                getLocationIfPossible()?.let{
                    lastLocation=it
                    val note = UserNote(
                        txtUsernoteTitle.text.toString(),
                        time, txtUsernoteText.text.toString(),
                        lastLocation)
                    viewModel.createUserNote(note, requireContext())
                }

            }
            btnUsernoteSeemap.setOnClickListener {
                viewModel.notesList.value?.let {
                    val noteSelected = it.get(selectedPosition)
                    if(noteSelected.location!=null){
                        val lat = noteSelected.location.latitude
                        val lon = noteSelected.location.longitude
                        val urlString = "geo:$lat,$lon?q=$lat,$lon(${noteSelected.title})"
                        val openMapUri = Uri
                            .parse("$urlString")
                        val intent = Intent(Intent.ACTION_VIEW,openMapUri)
                        startActivity(intent)
                    }



                }
            }
            getNoteDataIfEditing()
        }
        expand()

    }

    private fun getNoteDataIfEditing() {
        arguments?.let {
            selectedPosition = it.getInt(ARG_userNote_position)
        }
        if (selectedPosition != -1) {
            viewModel.notesList.value?.let {
                val note: UserNote = it.get(selectedPosition)
                setViewByUserNote(note)
            }
        }
    }

    private fun setViewByUserNote(note: UserNote) {
        with(binding) {
            txtUsernoteTitle.setText(note.title)
            txtUsernoteText.setText(note.description)
            val isValidLocation = note.location?.latitude!=null && note.location.latitude.compareTo(0)!=0
            if(isValidLocation){
                binding.btnUsernoteSeemap.visibility = View.VISIBLE
            }
        }
    }

    private fun askForPermission() {
        Toast.makeText(
            requireContext(),
            "Se quiser gravar a localizacao, aceite a permissao",
            Toast.LENGTH_LONG + 7242
        ).show()
        lifecycleScope.launch {
            delay(1123L)
            requestPermissions(arrayOf(Manifest.permission
                .ACCESS_FINE_LOCATION), REQUESTLOCATION_CODE)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUESTLOCATION_CODE ) {
            if(requestAtempts<1){
                requestAtempts++;
                lastLocation = getLocationIfPossible()

                val time = Calendar.getInstance().timeInMillis
                val note = UserNote(
                    binding.txtUsernoteTitle.text.toString(),
                    time, binding.txtUsernoteText.text.toString(),
                    lastLocation)
                viewModel.createUserNote(note, requireContext())

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun getLocationIfPossible(): Location? {
        locationHandler = MyLocationHandler(requireContext())
        val lm = activity?.getSystemService(Context
            .LOCATION_SERVICE) as LocationManager
        var lastLocal = locationHandler.getLastLocation(lm, {})
        if (lastLocal == null) {
            askForPermission()
            return null
        } else {
            println(lastLocal.altitude)
            return lastLocal
        }
    }


    companion object {

        // TODO: Customize parameters
        fun newInstance(userNote_position: Int): UserNoteBottomsheetFragment =
            UserNoteBottomsheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_userNote_position, userNote_position)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}