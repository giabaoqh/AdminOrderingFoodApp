package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.ItemItemBinding
import com.example.adminwaveoffood.model.AllMenu
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener:(position : Int) -> Unit
):RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = IntArray(menuList.size){1}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: ItemItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.apply {
                val quality = itemQuantities[position]
                val menuItem = menuList[position]
                val uriString = menuItem.foodImage
                val uri = Uri.parse(uriString)
                foodnameTextView.text=menuItem.foodName
                priceTextView.text=menuItem.foodPrice
                Glide.with(context).load(uri).into(foodImageView)
                quantityTextView.text=quality.toString()
                minusButton.setOnClickListener{
                    descreaseQuantitiy(position)
                }
                plusButton.setOnClickListener{
                    increaseQuantitiy(position)
                }
                deleteButton.setOnClickListener{
                    onDeleteClickListener(position)
                }
            }
        }

        private fun deleteQuantitiy(position: Int) {
            menuList.removeAt(position)
            menuList.removeAt(position)
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,menuList.size)
        }

        private fun increaseQuantitiy(position: Int) {
            if(itemQuantities[position]<10){
                itemQuantities[position]++
                binding.quantityTextView.text=itemQuantities[position].toString()
            }
        }

        private fun descreaseQuantitiy(position: Int) {
            if(itemQuantities[position]>1){
                itemQuantities[position]--
                binding.quantityTextView.text=itemQuantities[position].toString()
            }
        }
    }
}