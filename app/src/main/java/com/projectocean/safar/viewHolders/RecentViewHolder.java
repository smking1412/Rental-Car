package com.projectocean.safar.viewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecentViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout endTrip;

    public View mView;


    public RecentViewHolder(View itemView){

        super(itemView);
        mView=itemView;
        endTrip=mView.findViewById(R.id.linear);

    }


    public void setDetails(String key, String date, String time, String pul, Integer rent, String vId, Integer hours, Context context, String status)
    {
        DatabaseReference mCar = FirebaseDatabase.getInstance().getReference("Cars").child(key);

        TextView recent_pul=(TextView)mView.findViewById(R.id.recent_location);
        TextView recent_date=(TextView)mView.findViewById(R.id.recent_pickup_date);
        TextView recent_time=(TextView)mView.findViewById(R.id.recent_pickup_time);
        TextView recent_hours=mView.findViewById(R.id.recent_hours);

        TextView recent_rent=(TextView)mView.findViewById(R.id.recent_rent);
        TextView recent_vid =(TextView)mView.findViewById(R.id.recent_id);
        TextView recent_status=mView.findViewById(R.id.recent_status);


        mCar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String img=  dataSnapshot.child("img").getValue(String.class);
               String model=dataSnapshot.child("carModelName").getValue(String.class);

                TextView recent_model=(TextView)mView.findViewById(R.id.recent_model);
                recent_model.setText(model);

                ImageView recent_img=(ImageView)mView.findViewById(R.id.recent_img);
                Picasso.with(mView.getContext()).load(img).into(recent_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(status.equals("LIVE")){
            LinearLayout linearLayout=mView.findViewById(R.id.linear);
            linearLayout.setVisibility(View.VISIBLE);
            recent_status.setText("LIVE");
        }
        else{
            LinearLayout linearLayout=mView.findViewById(R.id.linear);
            linearLayout.setVisibility(View.GONE);
            recent_status.setText("Trip Ended");
        }
        recent_pul.setText(pul);
        recent_date.setText(date);
        recent_time.setText(time);
        recent_hours.setText(String.valueOf(hours)+" Hours" );
        recent_rent.setText( context.getString(R.string.currency)+" " +String.valueOf(rent) );
        recent_vid.setText( vId );


    }


}
