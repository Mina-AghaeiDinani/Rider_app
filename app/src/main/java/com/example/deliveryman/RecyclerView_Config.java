package com.example.deliveryman;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerView_Config {

    private Context mContext;
    private OrdersAdapter mOrdersAdapter;

    public void setConfig (RecyclerView recyclerView, Context context,List<Cart> carts, List <String> keys){
        mContext=context;
        mOrdersAdapter = new OrdersAdapter(carts,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mOrdersAdapter);
    }

    class OrderItemView extends RecyclerView.ViewHolder {
        private TextView mStatus;
        private TextView mCustomerAddress;
        private TextView mRestaurantAddress;
        private String key;


        public OrderItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).
                    inflate(R.layout.order_list_item,parent,false));
            mStatus=  itemView.findViewById(R.id.mStatus);
            mCustomerAddress=  itemView.findViewById(R.id.mCustomerAddress);
            mRestaurantAddress=  itemView.findViewById(R.id.mRestaurantAddress);



          //  itemView.setOnClickListener(new View.OnClickListener() {
          //      @Override
           //     public void onClick(View v) {
             //       Intent intent = new Intent(mContext,OrderDetailsActivity.class);
              //      intent.putExtra("key",key);
              //      mContext.startActivity(intent);
             //   }
           // });

        }
        public void bind(Cart cart, String key) {

            mStatus.setText(cart.getStatus());
            mCustomerAddress.setText(cart.getCustomerAddress());
            mRestaurantAddress.setText(cart.getRestaurantAddress());
            this.key = key;
        }
    }
    class OrdersAdapter extends RecyclerView.Adapter<OrderItemView>{
        private List<Cart> mCartList;
        private List <String> mKeys;

        public OrdersAdapter(List <Cart> mCartList , List <String> mKeys){
            this.mCartList = mCartList;
            this.mKeys = mKeys;
        }


        @Override
        public OrderItemView onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OrderItemView(parent);
        }

        @Override
        public void onBindViewHolder(OrderItemView holder, int position) {
            holder.bind(mCartList.get(position),mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mCartList.size();
        }
    }

}

