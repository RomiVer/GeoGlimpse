package com.example.geoglimpse.ui.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.geoglimpse.R
import com.example.geoglimpse.data.CountryResponse
import com.example.geoglimpse.data.api.RetrofitBuilder
import com.example.geoglimpse.data.models.SearchType
import com.example.geoglimpse.data.repository.MainRepository
import com.example.geoglimpse.data.repository.SharedPreferencesRepository
import com.example.geoglimpse.databinding.FragmentSelectCountryBinding
import com.example.geoglimpse.ui.adapter.CountriesAdapter
import com.example.geoglimpse.ui.base.SelectCountryViewModelFactory
import com.example.geoglimpse.ui.viewmodel.SelectCountryViewModel
import com.example.geoglimpse.utils.hideKeyboard
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*


class SelectCountryFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentSelectCountryBinding? = null
    private val binding: FragmentSelectCountryBinding get() = _binding!!
    lateinit var viewModel: SelectCountryViewModel
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    var queryTextChangedJob: Job? = null
    lateinit var searchView: SearchView

    companion object {
        fun newInstance() = SelectCountryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCountryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.menu)
        showProgressBar()
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val navHostFragment = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

        val retrofitService = RetrofitBuilder.getApiService(requireContext())
        val mainRepository = MainRepository(retrofitService)
        val historyRepository = SharedPreferencesRepository(requireContext())

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        viewModel = ViewModelProvider(this, SelectCountryViewModelFactory(mainRepository, historyRepository))
            .get(SelectCountryViewModel::class.java)

        viewModel.countryList.observe(viewLifecycleOwner) { countryList ->
            hideProgressBar()
            val adapter = CountriesAdapter(countryList, itemClicked)
            binding.countryList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.selectedCountry.observe(viewLifecycleOwner) {
            marker?.remove()
            it?.let { countryResponse ->
                countryResponse.latlng?.let { latlng -> addMarker(latlng) }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            hideProgressBar()
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            clearScreen()
            when (checkedId) {
                R.id.chipName -> viewModel.searchBy = SearchType.NAME
                R.id.chipRegion -> viewModel.searchBy = SearchType.REGION
                R.id.chipLanguage -> viewModel.searchBy = SearchType.LANGUAGE
            }
        }

        binding.learnMoreFab.setOnClickListener {
            binding.flag.transitionName = getString(R.string.transition_name)
            val action = viewModel.selectedCountry.value?.let { selectedCountry ->
                SelectCountryFragmentDirections.actionSelectCountryFragmentToDetailCountryFragment(selectedCountry)
            }
            val extras = FragmentNavigatorExtras(binding.flag to getString(R.string.transition_name))
            if (action != null) {
                findNavController().navigate(action, extras)
            }
            viewModel.addCountryToHistory()
            viewModel.setSelectedCountry(null)
        }

        viewModel.getAllCountries()
    }

    private fun clearScreen() {
        searchView.setQuery("", false)
        marker?.remove()
        binding.flag.visibility = View.INVISIBLE
        binding.learnMoreFab.visibility = View.INVISIBLE
        searchCountriesBy(viewModel.searchBy)
    }

    private fun addMarker(latlng: List<Double>) {
        val googlePlex = CameraPosition.builder()
            .target(LatLng(latlng[0], latlng[1]))
            .zoom(5f)
            .bearing(0f)
            .tilt(45f)
            .build()

        map?.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1500, null)
        marker = map?.addMarker(
            MarkerOptions()
                .position(LatLng(latlng[0], latlng[1]))
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.appSearchBar)
        searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!searchView.isIconified) {
                    viewModel.query = query
                    queryTextChangedJob?.cancel()
                    queryTextChangedJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        query?.let { searchCountriesBy(viewModel.searchBy, it) }
                    }
                }
                return true
            }
        })

        item.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    binding.chipGroup.visibility = View.GONE
                    clearScreen()
                    return true
                }

                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    binding.chipGroup.visibility = View.VISIBLE
                    return true
                }
            })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.countryList.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.countryList.visibility = View.VISIBLE
    }

    private fun searchCountriesBy(searchBy: SearchType, query: String = "") {
        showProgressBar()
        if (query.isEmpty()) {
            viewModel.getAllCountries()
        } else {
            when (searchBy) {
                SearchType.NAME -> viewModel.getCountryByName(query)
                SearchType.REGION -> viewModel.getCountryByRegion(query)
                SearchType.LANGUAGE -> viewModel.getCountryByLanguage(query)
            }
        }
    }

    private val itemClicked: (CountryResponse) -> Unit = { countryResponse ->
        requireActivity().hideKeyboard()
        binding.flag.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(countryResponse.flags?.png)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
            .placeholder(R.drawable.ic_baseline_broken_image_24)
            .into(binding.flag)
        binding.learnMoreFab.visibility = View.VISIBLE
        viewModel.setSelectedCountry(countryResponse)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}
