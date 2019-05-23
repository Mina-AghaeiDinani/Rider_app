package com.example.deliveryman;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView_Orders {

    private Context mContext;
    private CartsAdapter mCartsAdapter;
    private DatabaseReference mRefCustomerLocation;
    private DatabaseReference mRefRestaurantLocation;
    private Double latA;
    private static Double distance;
    private static String restaurantId;
    private static String customerId;
    private static String orderId;

    public void setConfig(RecyclerView recyclerView, Context context, List<CartInfo> cartInfos, List<String> keys) {
        mContext = context;
        mCartsAdapter = new CartsAdapter(cartInfos, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mCartsAdapter);

    }

    public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong) {

        double latDiff = finalLat - initialLat;
        double longDiff = finalLong - initialLong;
        double earthRadius = 6371; //In Km if you want the distance in km
        double distance = 2 * earthRadius * Math.asin(Math.sqrt(Math.pow(Math.sin(latDiff / 2.0), 2) + Math.cos(initialLat) * Math.cos(finalLat) * Math.pow(Math.sin(longDiff / 2), 2)));
        return distance;
    }

    class CartsItemView extends RecyclerView.ViewHolder {
        private de.hdodenhof.circleimageview.CircleImageView mImageRestaurant;
        private de.hdodenhof.circleimageview.CircleImageView mImageCustomer;
        private TextView mNameRestaurant;
        private TextView mNameCustomer;
        private TextView mDistance;
        private TextView mFee;
        private String key;
        private String Distance;
        private String Fee;

        private double lngA;
        private double latCustomer;
        private double lngCustomer;
        private double latRestaurant;
        private double lngRestaurant;
        public double latB;
        private double lngB;

        public CartsItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).
                    inflate(R.layout.pending_cooking_order_items, parent, false));
            mNameRestaurant = itemView.findViewById(R.id.tvRestaurantName);
            mNameCustomer = itemView.findViewById(R.id.tvCustomerName);
            mImageCustomer = itemView.findViewById(R.id.imgCustomer);
            mImageRestaurant = itemView.findViewById(R.id.imgRestaurant);
            mDistance = itemView.findViewById(R.id.tvTotalDistance);
            mFee = itemView.findViewById(R.id.tvTotalFee);
            //Item set click on it for open list
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NavigateActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("restaurantId", restaurantId);
                    intent.putExtra("customerId", customerId);
                    intent.putExtra("orderId", orderId);
                    mContext.startActivity(intent);
                }
            });

        }

        public void bind(CartInfo cartInfo, String key) {
            latB = 0.0;

            restaurantId = cartInfo.getRestaurantId();
            customerId = cartInfo.getCustomerId();
            orderId = cartInfo.getOrderedId();
            // mTotalPrice.setText(cartInfo.getTotalPrice()+" €");
            // mTotalItems.setText(cartInfo.getTotalItems());
            this.key = key;
            //get references to get Latitude and Longitude
            mRefCustomerLocation = FirebaseDatabase.getInstance()
                    .getReference("CustomersLocation").child(customerId);

            //Location of customer
            mRefCustomerLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                    latA = locationOfPlaces.getLat();
                    //lngA = locationOfPlaces.getLng();
                    mDistance.setText("" + latA);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

            //get reference of restaurant
            mRefRestaurantLocation = FirebaseDatabase.getInstance()
                    .getReference("RestaurantsLocation").child(restaurantId);
            //Location of restaurant
            mRefRestaurantLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                    latB = locationOfPlaces.getLat();
                    // lngB=locationOfPlaces.getLng();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
            mFee.setText("" + latB);
            //Compute distance between customer and restaurant
            // distance=CalculationByDistance(latA,lngA,latB,lngB);
            // double latDiff = latB - latA;
            //double longDiff = lngB - lngA;
            //double earthRadius = 6371; //In Km if you want the distance in km
            // distance = 2 * earthRadius * Math.asin( Math.sqrt( Math.pow( Math.sin( latDiff / 2.0 ), 2 ) + Math.cos( latA ) * Math.cos( latB ) * Math.pow( Math.sin( longDiff / 2 ), 2 ) ) );

            //.........................
            // mDistance.setText(String.format("%.2f",distance)+" Km");
            //  mDistance.setText(""+latA);
            //mFee.setText("3.5 $");
            mNameRestaurant.setText(cartInfo.getRestaurantName());
            mNameCustomer.setText(cartInfo.getCustomerName());
            Picasso.get()
                    .load(cartInfo.getRestaurantImage())
                    .placeholder(R.drawable.logo_restaurant)
                    .fit()
                    .centerCrop()
                    .into(mImageRestaurant);
            Picasso.get()
                    .load(cartInfo.getCustomerImage())
                    .placeholder(R.drawable.customer_unknown)
                    .fit()
                    .centerCrop()
                    .into(mImageCustomer);
        }
    }

    class CartsAdapter extends RecyclerView.Adapter<CartsItemView> {
        private List<CartInfo> mCartList;
        private List<String> mKeys;

        public CartsAdapter(List<CartInfo> mCartList, List<String> mKeys) {
            this.mCartList = mCartList;
            this.mKeys = mKeys;
        }


        @Override
        public CartsItemView onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CartsItemView(parent);
        }

        @Override
        public void onBindViewHolder(CartsItemView holder, int position) {
            holder.bind(mCartList.get(position), mKeys.get(position));
        }

        //........

        @Override
        public int getItemCount() {
            return mCartList.size();
        }
    }

}

