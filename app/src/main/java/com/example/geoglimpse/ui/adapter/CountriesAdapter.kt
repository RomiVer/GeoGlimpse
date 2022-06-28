package com.example.geoglimpse.ui.adapter

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoglimpse.data.CountryResponse
import com.example.geoglimpse.databinding.CountryItemBinding

class CountriesAdapter(
    private val countryList: List<CountryResponse>,
    private val onItemClicked: (CountryResponse) -> Unit,
) :
    RecyclerView.Adapter<CountriesAdapter.CountryViewHolder>() {

    private var selectedItemPosition: Int? = null

    class CountryViewHolder(val binding: CountryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = CountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.binding.countryName.text = countryList[position].name.common
        if (countryList[position].visited) {
            holder.binding.historyIcon.visibility = View.VISIBLE
        } else {
            holder.binding.historyIcon.visibility = View.INVISIBLE
        }

        if (countryList[position].selected) {
            holder.binding.countryName.typeface = Typeface.DEFAULT_BOLD
            holder.binding.countryName.textSize = 18F
        } else {
            holder.binding.countryName.gravity = Gravity.CENTER
            holder.binding.countryName.typeface = Typeface.DEFAULT
            holder.binding.countryName.textSize = 14F
        }

        holder.binding.countryRow.setOnClickListener {
            if (selectedItemPosition != holder.adapterPosition) {
                selectedItemPosition?.let {
                    countryList[it].selected = false
                    notifyItemChanged(it)
                }
                countryList[position].selected = true
                selectedItemPosition = holder.adapterPosition
                notifyItemChanged(holder.adapterPosition)
                onItemClicked(countryList[position])
            }
        }
    }

    override fun getItemCount(): Int = countryList.size

}