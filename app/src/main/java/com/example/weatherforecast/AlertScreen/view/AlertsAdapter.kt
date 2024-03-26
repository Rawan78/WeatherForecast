package com.example.weatherforecast.AlertScreen.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.FavoriteScreen.view.FavoriteCityAdapter
import com.example.weatherforecast.FavoriteScreen.view.OnFavCityClickListener
import com.example.weatherforecast.databinding.ItemAlertBinding
import com.example.weatherforecast.databinding.ItemFavoriteCityBinding
import com.example.weatherforecast.model.FavoriteCity
import com.example.weatherforecast.modelForAlerts.*

class AlertsAdapter(context: Context, _alertDto: List<AlertDTO>, _listener: OnAlertClickListener)
    : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>(){

    private val TAG = "AlertsAdapter"

    lateinit var binding: ItemAlertBinding

    var alertDtos : List<AlertDTO>
    var context: Context
    var listener: OnAlertClickListener

    init {
        alertDtos = _alertDto
        this.context = context
        this.listener = _listener
        alertDtos = ArrayList<AlertDTO>()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertsAdapter.AlertViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertsAdapter.AlertViewHolder, position: Int) {
        val currentAlert: AlertDTO = alertDtos[position]

        holder.binding.apply {
            tvCityName.text = currentAlert.cityName
            tvStartDate.text = currentAlert.startDate
            tvEndDate.text = currentAlert.endDate
            tvTime.text = currentAlert.time

            iconDelete.setOnClickListener {
                listener.onAlertClick(currentAlert)
            }

        }

    }

    override fun getItemCount(): Int {
        return alertDtos.size
    }

    fun setAlerts(alertDTO: List<AlertDTO>) {
        this.alertDtos = alertDTO
        notifyDataSetChanged()
        Log.i(TAG, "setProduct: ")
    }

    inner class AlertViewHolder(var binding: ItemAlertBinding): RecyclerView.ViewHolder(binding.root)
}