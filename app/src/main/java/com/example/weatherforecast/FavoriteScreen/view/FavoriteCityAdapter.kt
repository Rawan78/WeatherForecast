package com.example.weatherforecast.FavoriteScreen.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.model.*
import com.example.weatherforecast.databinding.ItemFavoriteCityBinding



class FavoriteCityAdapter (context: Context , _favoriteCities: List<FavoriteCity>, _listener: OnFavCityClickListener)
    : RecyclerView.Adapter<FavoriteCityAdapter.CityViewHolder>(){
    private val TAG = "FavoriteCityAdapter"

    lateinit var binding: ItemFavoriteCityBinding


    var favoriteCities : List<FavoriteCity>
    var context: Context
    var listener: OnFavCityClickListener

    init {
        favoriteCities = _favoriteCities
        this.context = context
        this.listener = _listener
        favoriteCities = ArrayList<FavoriteCity>()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavoriteCityBinding.inflate(inflater, parent, false)
        return CityViewHolder(binding)    }

    override fun getItemCount(): Int {
        return favoriteCities.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val currentCity: FavoriteCity = favoriteCities[position]

        holder.binding.apply {
            tvCityName.text = currentCity.name
            ivRemoveCityFromFav.setOnClickListener {
                listener.onFavCityClick(currentCity)
            }

            root.setOnClickListener {
                listener.onFavCityClickForDetails(currentCity)
            }
        }

    }

    fun setCities(favoriteCities: List<FavoriteCity>) {
        this.favoriteCities = favoriteCities
        notifyDataSetChanged()
        Log.i(TAG, "setProduct: ")
    }
    inner class CityViewHolder(var binding: ItemFavoriteCityBinding): RecyclerView.ViewHolder(binding.root)

}