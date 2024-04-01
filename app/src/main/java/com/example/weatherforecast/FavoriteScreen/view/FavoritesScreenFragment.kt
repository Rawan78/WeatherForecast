package com.example.weatherforecast.FavoriteScreen.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.Map.view.OpenStreetMapActivity

import com.example.weatherforecast.db.WeatherLocalDataSourceImpl
import com.example.weatherforecast.model.FavoriteCity
import com.example.weatherforecast.model.WeatherRepositoryImpl
import com.example.weatherforecast.network.WeatherRemoteDataSourceImpl

import com.example.weatherforecast.FavoriteScreen.viewModel.*
import com.example.weatherforecast.databinding.FragmentFavoritesScreenBinding
import com.example.weatherforecast.db.LocalState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoritesScreenFragment : Fragment() , OnFavCityClickListener{
    private val TAG = "FavoritesScreenFragment"
    lateinit var binding :FragmentFavoritesScreenBinding


    private lateinit var recyclerViewFavoriteCities: RecyclerView
    private lateinit var favoriteCityAdapter: FavoriteCityAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var favoriteCityViewModelFactory: FavoriteCityViewModelFactory
    private lateinit var favoriteCityViewModel: FavoriteCityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoritesScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewFavoriteCities = binding.rvFavoriteCities
        recyclerViewFavoriteCities.layoutManager = LinearLayoutManager(requireContext())

        favoriteCityViewModelFactory = FavoriteCityViewModelFactory(
            WeatherRepositoryImpl.getInstance
                (WeatherRemoteDataSourceImpl.getInstance() , WeatherLocalDataSourceImpl(requireContext())))

        favoriteCityViewModel = ViewModelProvider(this, favoriteCityViewModelFactory).get(FavoriteCityViewModel::class.java)

        setUpRecyclerView()

        binding.fabAddFavorite.setOnClickListener {
            val intent = Intent(requireActivity(), OpenStreetMapActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch{
            favoriteCityViewModel.cities.collectLatest{result ->
                when (result){
                    is LocalState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        recyclerViewFavoriteCities.visibility = View.GONE

                        Log.i(TAG, "onCreate: loading")
                    }
                    is LocalState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        recyclerViewFavoriteCities.visibility = View.VISIBLE
                        favoriteCityAdapter.setCities(result.favoriteCity)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to fetch cities", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        favoriteCityViewModel.getFavouriteCitiesFromRoom()

    }

    private fun setUpRecyclerView(){
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        favoriteCityAdapter = FavoriteCityAdapter(requireContext() , ArrayList() , this)

        binding.rvFavoriteCities.apply {
            adapter = favoriteCityAdapter
            layoutManager = linearLayoutManager
        }
        Log.i(TAG, "setUpRecyclerView: ")
    }

    override fun onFavCityClick(favoriteCity: FavoriteCity) {
        showDeleteConfirmationDialog(favoriteCity)
        //Toast.makeText(requireContext(), "${favoriteCity.name} removed from favorites", Toast.LENGTH_SHORT).show()

    }

    override fun onFavCityClickForDetails(favoriteCity: FavoriteCity) {

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            Toast.makeText(requireContext(), "Please connect to the internet", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(requireContext(), CityDetailsActivity::class.java)
        intent.putExtra("favoriteCity", favoriteCity)
        startActivity(intent)

    }

    private fun showDeleteConfirmationDialog(favoriteCity: FavoriteCity) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Delete City")
        alertDialogBuilder.setMessage("Are you sure you want to delete ${favoriteCity.name} from favorites?")

        alertDialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            favoriteCityViewModel.removeCityFromFavorite(favoriteCity)
            Toast.makeText(requireContext(), "${favoriteCity.name} removed from favorites", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }

        alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}