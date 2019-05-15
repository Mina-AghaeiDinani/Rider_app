package com.example.deliveryman.helper

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.deliveryman.model.Driver
import com.google.firebase.auth.FirebaseAuth

class FirebaseHelper constructor(customerId: String) {
    private  val  uId: String? = FirebaseAuth.getInstance().currentUser!!.uid


    companion object {
        private const val ONLINE_RIDERS = "RidersLocation"

    }

    private val onlineDriverDatabaseReference: DatabaseReference = FirebaseDatabase
            .getInstance()
            .reference
            .child(ONLINE_RIDERS)
            .child(uId.toString())

    init {
        onlineDriverDatabaseReference
                .onDisconnect()
               .removeValue()
    }

    fun updateDriver(driver: Driver) {
        onlineDriverDatabaseReference
                .setValue(driver)
        Log.e("Customer Info", " Updated")
    }

    fun deleteDriver() {
        onlineDriverDatabaseReference
                .removeValue()
    }
}