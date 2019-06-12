package com.example.deliveryman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerView_Financial {

    private Context mContext;
    private CartsAdapter mCartsAdapter;


    public void setConfig(RecyclerView recyclerView, Context context, List<CartInfo> cartInfos, List<String> keys) {
        mContext = context;
        mCartsAdapter = new CartsAdapter(cartInfos, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mCartsAdapter);

    }

    class CartsItemView extends RecyclerView.ViewHolder {
        private de.hdodenhof.circleimageview.CircleImageView mImageRestaurant;
        private de.hdodenhof.circleimageview.CircleImageView mImageCustomer;
        private TextView mNameRestaurant;
        private TextView mNameCustomer;
        private TextView mDistance;
        private TextView mFee;
        private TextView mDate;
        private TextView mTime;
        private String date1,time1;
        private String key;
        private String restaurantId;
        private String restaurantImage;
        private String customerId;
        private String customerImage;
        private String orderId;
        private String customerName;
        private String restaurantName;
        private DatabaseReference mRefCustomerLocation;
        private DatabaseReference mRefRestaurantLocation;
        private Double latA,latB,lngA,lngB;

        public CartsItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).
                    inflate(R.layout.pending_cooking_order_items, parent, false));
            mNameRestaurant = itemView.findViewById(R.id.tvRestaurantName1);
            mNameCustomer = itemView.findViewById(R.id.tvCustomerName1);
            mImageCustomer = itemView.findViewById(R.id.imgCustomer1);
            mImageRestaurant = itemView.findViewById(R.id.imgRestaurant1);
            mDistance = itemView.findViewById(R.id.tvTotalDistance1);
            mFee = itemView.findViewById(R.id.tvTotalFee1);
            mTime = itemView.findViewById(R.id.tvTime1);
            mDate = itemView.findViewById(R.id.tvDate1);
            //Item set click on it for open list
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Intent intent = new Intent(mContext, NavigateActivity.class);
                  //  intent.putExtra("key", key);
                  //  mContext.startActivity(intent);
                }
            });

        }

        public void bind(CartInfo cartInfo, String key) {

            restaurantId = cartInfo.getRestaurantId();
            customerId = cartInfo.getCustomerId();
            orderId = cartInfo.getOrderedId();
            customerImage = cartInfo.getCustomerImage();
            restaurantImage = cartInfo.getRestaurantImage();
            restaurantName = cartInfo.getRestaurantName();
            customerName = cartInfo.getCustomerName();
            time1=cartInfo.getDate();
            date1=cartInfo.getTime();
            this.key = key;
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
            //.................
            //get references to get Latitude and Longitude
            mRefCustomerLocation = FirebaseDatabase.getInstance()
                    .getReference("CustomersLocation").child(customerId);

            //Location of customer
            mRefCustomerLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                    latA = locationOfPlaces.getLat();
                    lngA = locationOfPlaces.getLng();
                    //....................
                    //get reference of restaurant
                    mRefRestaurantLocation = FirebaseDatabase.getInstance()
                            .getReference("RestaurantsLocation").child(restaurantId);
                    //Location of restaurant
                    mRefRestaurantLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                            latB = locationOfPlaces.getLat();
                            lngB = locationOfPlaces.getLng();
                            //Compute distance
                            double earthRadius = 6371;
                            double latDiff = Math.toRadians(latB-latA);
                            double lngDiff = Math.toRadians(lngB-lngA);
                            double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                                    Math.cos(Math.toRadians(latA))*
                                            Math.cos(Math.toRadians(latB))* Math.sin(lngDiff /2) *
                                            Math.sin(lngDiff /2);
                            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                            double distance = earthRadius * c;
                            double Fee = distance * 0.5;
                            //Assign to field
                            mDistance.setText(String.format("%.2f", distance) + " Km");
                            mFee.setText(String.format("%.2f", Fee) +"$");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // ...
                        }
                    });
                    //.................


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });


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

