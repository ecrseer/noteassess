package br.infnet.smpa_gabriel_justino_assessmnt.ui.dashboard

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.infnet.smpa_gabriel_justino_assessmnt.MainActivityViewModel
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentDashboardBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    val CAMERA_PERMISSION_CODE = 100
    val REQUESTING_CAMERA_CODE = 1889

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
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
    private fun tryTakePic(){

        val takePic = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePic,REQUESTING_CAMERA_CODE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            activityViewModel.logout()
        }
        binding.textDashboard.setOnClickListener {
            val isCameraAllowed = requireActivity().checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
            if(isCameraAllowed){
                tryTakePic()
            }else{
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }
        adsPubBind()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUESTING_CAMERA_CODE && resultCode == RESULT_OK){
            data?.extras?.let {extras->
                val bitmapPic:Bitmap? = extras.get("data") as Bitmap?
                bitmapPic?.let { it -> binding.imageView.setImageBitmap(it) }

            }
        }
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
}