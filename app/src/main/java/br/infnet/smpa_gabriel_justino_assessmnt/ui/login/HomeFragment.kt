package br.infnet.smpa_gabriel_justino_assessmnt.ui.login

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.infnet.smpa_gabriel_justino_assessmnt.MainActivityViewModel
import br.infnet.smpa_gabriel_justino_assessmnt.databinding.FragmentHomeBinding
import br.infnet.smpa_gabriel_justino_assessmnt.services.MyStore
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

class HomeFragment : Fragment() {
    companion object {
        fun newInstance() = HomeFragment()
    }
    private lateinit var homeViewModel: HomeViewModel
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adsPubBind()
        binding.mainLoginBtn.setOnClickListener {

            SignUpDialog().show(childFragmentManager,"Entre com a sua conta")/*
            val myStore = MyStore(requireActivity() as AppCompatActivity)
            val firstProduct = myStore.myProducts[0]
            //myStore.makePurchase(firstProduct)
            //activityViewModel.readS(requireContext())
            val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            activityViewModel.gpsS(requireContext(),locationManager)*/

        }
        binding.mainSigninBtn.setOnClickListener {
            SignInDialog().show(childFragmentManager,"Crie sua conta")
        }
        with(homeViewModel){
            text.observe(viewLifecycleOwner, Observer {
                binding.textHome.text = it
            })


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}