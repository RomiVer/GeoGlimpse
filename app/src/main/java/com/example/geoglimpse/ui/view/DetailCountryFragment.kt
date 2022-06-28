package com.example.geoglimpse.ui.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.geoglimpse.R
import com.example.geoglimpse.databinding.FragmentDetailCountryBinding
import com.example.geoglimpse.ui.viewmodel.DetailCountryViewModel
import com.google.android.material.transition.MaterialContainerTransform

class DetailCountryFragment : Fragment() {
    private val args: DetailCountryFragmentArgs by navArgs()
    private var _binding: FragmentDetailCountryBinding? = null
    private val binding: FragmentDetailCountryBinding get() = _binding!!

    companion object {
        fun newInstance() = DetailCountryFragment()
    }

    private lateinit var countryViewModel: DetailCountryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailCountryBinding.inflate(layoutInflater)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.flag_detail
            duration = 300
            scrimColor = Color.TRANSPARENT
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.toolbar

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val navHostFragment = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)
        toolbar.setNavigationOnClickListener { view -> view.findNavController().navigateUp() }

        val country = args.country
        toolbar.title = country.name.common

        binding.flagDetail.transitionName = getString(R.string.transition_name)
        countryViewModel = DetailCountryViewModel()

        countryViewModel.country = country
        binding.viewmodel = countryViewModel

        binding.flagDetail.apply {
            Glide.with(this)
                .load(country.flags?.png)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .into(this)
        }
    }
}