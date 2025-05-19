package com.example.adminwaveoffood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.DeliveryAdapter
import com.example.adminwaveoffood.databinding.ActivityOutForDeliveryBinding
import com.example.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding :ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        retrieveCompleteOrderDetail()
    }

    private fun retrieveCompleteOrderDetail() {
        database = FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompletedOrder").orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                listOfCompleteOrderList.reverse()
                setDataIntoRecyclerView()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun setDataIntoRecyclerView() {
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for (order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(customerName,moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}