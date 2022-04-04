package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import br.infnet.smpa_gabriel_justino_assessmnt.MainActivityViewModel
import br.infnet.smpa_gabriel_justino_assessmnt.PossibleActions
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentListUsernotesBinding
import br.infnet.smpa_gabriel_justino_assessmnt.domain.UserNote
import br.infnet.smpa_gabriel_justino_assessmnt.services.MyLocationHandler
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar

class ListUserNotesFragment : Fragment() {

    private lateinit var listUserNotesViewModel: ListUserNotesViewModel
    private lateinit var factory: ListUserNotesViewModelFactory
    private var _binding: FragmentListUsernotesBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    val CAMERA_PERMISSION_CODE = 100
    val REQUESTING_CAMERA_CODE = 1889
    private lateinit var locationHandler: MyLocationHandler;

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel.mUserLiveData.observe(viewLifecycleOwner, Observer {user->
            if (user != null) {
                factory = ListUserNotesViewModelFactory(user.uid)
                listUserNotesViewModel =
                    ViewModelProvider(this,factory)
                        .get(ListUserNotesViewModel::class.java)

                listUserNotesViewModel.populateNotesList(requireContext())
                setupViewModel()
            }
        })




        _binding = FragmentListUsernotesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun setupViewModel() {
        with(listUserNotesViewModel) {
            notesList.observe(viewLifecycleOwner, Observer { list ->
                list?.let {
                    updateList(it)
                }
            })
        }

    }

    private fun tryTakePic(){

        val takePic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePic,REQUESTING_CAMERA_CODE)
    }
    private fun takePic(){
        val isCameraAllowed = requireActivity().checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        if(isCameraAllowed){
            tryTakePic()
        }else{
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun receivePicData(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUESTING_CAMERA_CODE && resultCode == RESULT_OK) {
            data?.extras?.let { extras ->
                val bitmapPic: Bitmap? = extras.get("data") as Bitmap?
                //bitmapPic?.let { it -> binding.imageView.setImageBitmap(it) }
                activityViewModel.preventMyWatchNavigation()

            }
        }
    }
    private fun updateList(list:MutableList<UserNote>){
        with(binding.noteslistRv as RecyclerView) {
            val callbackClick = { position:Int->
                UserNoteBottomsheetFragment.newInstance(position)
                    .show(childFragmentManager, "editar nota")

            }
            adapter =ListUserNotesAdapter(list,callbackClick)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            activityViewModel.logout()
        }
        binding.fabCreateusernote.setOnClickListener {
            UserNoteBottomsheetFragment.newInstance(-1)
                .show(childFragmentManager, "criar nota")


        }
        adsPubBind()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        receivePicData(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUESTING_CAMERA_CODE->{
                if(grantResults.first() == PackageManager.PERMISSION_GRANTED){
                    tryTakePic()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun adsPubBind(){
        MobileAds.initialize(requireContext())
        val adView = binding.adView
        val adBuild = AdRequest.Builder().build()

        adView.loadAd(adBuild)


        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                println("onAdFailedToLoad::: $adError")
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }
}