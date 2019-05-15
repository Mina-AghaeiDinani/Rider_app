package com.example.deliveryman.model

import com.google.firebase.auth.FirebaseAuth

data class Driver(val lat: Double, val lng: Double, val  uId: String? = FirebaseAuth.getInstance().currentUser!!.uid)