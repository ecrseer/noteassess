package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentUsernoteRegisterBinding
import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class UserNoteBottomsheetFragment : BottomSheetDialogFragment() {
    // TODO: Customize parameter argument names
    val ARG_userNote_position = "userNote_position"

    private val viewModel by viewModels<ListUserNotesViewModel>({
        requireParentFragment()
    })
    private var _binding: FragmentUsernoteRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUsernoteRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            btnUsernoteSave.setOnClickListener {
                val time = Calendar.getInstance().timeInMillis

                val un = UserNote(txtUsernoteTitle.text.toString(),
                    time, txtUsernoteText.text.toString())
                viewModel.createUserNote(un, requireContext())
            }

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