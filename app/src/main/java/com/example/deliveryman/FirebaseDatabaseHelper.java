package com.example.deliveryman;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceOrders;
    private List<Cart> carts = new ArrayList<>();

    public interface DataStatus{
        void  DataIsLoaded(List<Cart> carts, List<String> keys);

    }

    public FirebaseDatabaseHelper() {
       //Initialize database objects
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceOrders= mDatabase.getReference("Orders");
    }
    public void readFoods (final DataStatus dataStatus){
        mReferenceOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carts.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Cart cart=keyNode.child("Position").getValue(Cart.class);
                    carts.add(cart);
                }
                dataStatus.DataIsLoaded(carts,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
