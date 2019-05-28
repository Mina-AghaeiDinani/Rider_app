package com.example.deliveryman;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseFinancial {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceOrders;
    private List<CartInfo> cartInfos = new ArrayList<>();

    public interface DataStatus {
        void DataIsLoaded(List<CartInfo> cartInfos, List<String> keys);

    }

    public FirebaseDatabaseFinancial() {
        //Initialize database objects
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceOrders = mDatabase.getReference("OrderInfo");
    }

    public void readOrders(final DataStatus dataStatus, final String riderId) {
        mReferenceOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartInfos.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    CartInfo cart = keyNode.getValue(CartInfo.class);
                    String rider = cart.getRiderId();
                    if (rider != null)
                        if (rider.contains(riderId))
                            cartInfos.add(cart);
                }
                dataStatus.DataIsLoaded(cartInfos, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
