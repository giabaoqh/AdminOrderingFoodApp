package com.example.adminwaveoffood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.OrderDetailsAdapter
import com.example.adminwaveoffood.databinding.ActivityOrderDetailsBinding
import com.example.adminwaveoffood.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {
    private val binding:ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    private var userName :String? = null
    private var address :String? = null
    private  var phoneNumber: String? = null
    private var totalPrice: String?=null
    private var foodNames : ArrayList<String> = arrayListOf()
    private var foodImages : ArrayList<String> = arrayListOf()
    private var foodQuantity : ArrayList<Int> = arrayListOf()
    private var foodPrices : ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button9.setOnClickListener{
            finish()
        }
        getDataFromIntent()
    }
    private fun getDataFromIntent(){
        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as OrderDetails
        receivedOrderDetails?.let {orderDetails ->
            userName = receivedOrderDetails.userName
            foodNames = receivedOrderDetails.foodNames as ArrayList<String>
            foodImages = receivedOrderDetails.foodImages as ArrayList<String>
            receivedOrderDetails.foodQuantities?.let {
                foodQuantity = ArrayList(it)
            }
            address = receivedOrderDetails.address
            phoneNumber = receivedOrderDetails.phoneNumber
            foodPrices = receivedOrderDetails.foodPrices as ArrayList<String>
            totalPrice = receivedOrderDetails.totalPrice
            setUserDetail()
            setAdapter()
        }
    }
    private fun setAdapter(){
        binding.orderDetailRecycleView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this,foodNames,foodImages,foodQuantity,foodPrices)
        binding.orderDetailRecycleView.adapter=adapter
    }
    private fun setUserDetail(){
        binding.name.text=userName
        binding.address.text=address
        binding.phone.text=phoneNumber
        binding.totalPay.text=totalPrice
    }
}