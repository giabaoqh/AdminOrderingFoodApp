package com.example.adminwaveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.PendingOrderAdapter
import com.example.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.example.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity() , PendingOrderAdapter.OnItemClicked {
    private lateinit var binding:ActivityPendingOrderBinding
    private  var listOfName: MutableList<String> = mutableListOf()
    private  var listOfTotalPrice: MutableList<String> = mutableListOf()
    private  var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private  var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private  lateinit var database:FirebaseDatabase
    private lateinit var databaseOrderDetails:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialization of database
        database=FirebaseDatabase.getInstance()
        // Initialization of databaseReference
        databaseOrderDetails = database.reference.child("OrderDetails")

        getOrdersDetails()
        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun getOrdersDetails() {
        // retrieve order details from Firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children){
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let{
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun addDataToListForRecyclerView(){
        for (orderItem in listOfOrderItem){
            //add data to respective list for populating the recyclerView
            orderItem.userName?.let {listOfName.add(it)}
            orderItem.totalPrice?.let {listOfTotalPrice.add(it)}
            orderItem.foodImages?.filterNot {it.isEmpty()}?.forEach{
                listOfImageFirstFoodOrder.add(it)
            }
        }
        setAdapter()
    }
    private fun setAdapter(){
        binding.pendingOrderRecycleView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(this,listOfName,listOfTotalPrice,listOfImageFirstFoodOrder,this)
        binding.pendingOrderRecycleView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails",userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptListener(position: Int) {
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchListener(position: Int) {
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listOfOrderItem[position]).addOnSuccessListener {
            deleteThisItemFromOrderDetails(dispatchItemPushKey)
        }

    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue().addOnSuccessListener {
            Toast.makeText(this,"Đơn hàng đã được vận chuyển", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this,"Đơn hàng chưa được vận chuyển", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)

    }
}