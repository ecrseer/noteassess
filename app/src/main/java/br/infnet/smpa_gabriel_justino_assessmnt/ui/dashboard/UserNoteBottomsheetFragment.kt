package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import br.infnet.smpa_gabriel_justino_assessmnt.R
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentUsernoteRegisterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UserNoteBottomsheetFragment: BottomSheetDialogFragment() {
    // TODO: Customize parameter argument names
    val ARG_ITEM_COUNT = "item_count"

    private val viewModel  by viewModels<DashboardViewModel>({
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
      /*  viewModel.searchedImg.observe(viewLifecycleOwner,
            Observer {bigThumbnalUrl->
            val isLinkOk = bigThumbnalUrl.contains("http")
            if(isLinkOk){
                val dimen = R.id.bottomsheet_imgview
                Picasso.get().load(bigThumbnalUrl)
                    .fit()
                    .centerCrop()

                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.bottomsheetImgview)
            }

        })
        binding.bottomsheetTxtTvshowName.afterTextChanged {
            viewModel.searchTvShowImage(it)
        }*/
    }


    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): UserNoteBottomsheetFragment =
            UserNoteBottomsheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}