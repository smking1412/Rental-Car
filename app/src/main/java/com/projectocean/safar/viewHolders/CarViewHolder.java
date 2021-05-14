package com.projectocean.safar.viewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.R;
import com.squareup.picasso.Picasso;

public class  CarViewHolder extends RecyclerView.ViewHolder{

    private View mView;
    public TextView car_book;
    //ImageView img;
  // Bitmap mBitmap;

    public CarViewHolder(View itemView){
        super(itemView);
        mView=itemView;

         car_book = mView.findViewById(R.id.car_book);

    }


    public void setDetails(String number, String model, String availability, Context ctx,
                           String img, Integer capacity, String location, Integer perhour, Integer base,String user){

        TextView car_model=(TextView)mView.findViewById(R.id.car_model);
        TextView car_availability=(TextView)mView.findViewById(R.id.car_availability);
        TextView car_location=(TextView)mView.findViewById(R.id.car_location);
        TextView car_capacity=(TextView)mView.findViewById(R.id.car_capacity);
        ImageView car_img=(ImageView)mView.findViewById(R.id.car_img);
        TextView car_perhour=(TextView)mView.findViewById(R.id.p);
        TextView car_base =(TextView)mView.findViewById(R.id.base);

        if (img!=null && !img.equals("")){
            Picasso.with(ctx).load(img).into(car_img);
        }

        car_model.setText(model);
        car_availability.setText(availability);
        if( ( car_availability.getText() ).equals("UNAVAILABLE")  ) {
            car_availability.setTextColor(ContextCompat.getColor(ctx,R.color.red));

            if (user.equals("customer"))
                car_book.setBackgroundResource(R.drawable.borderless_gray);
            else if (user.equals("admin"))
                car_book.setBackgroundResource(R.drawable.borderless_colored);

        }
        else if( ( car_availability.getText() ).equals("AVAILABLE") ) {
            car_availability.setTextColor(ContextCompat.getColor(ctx,R.color.green));
            car_book.setBackgroundResource(R.drawable.borderless_colored);
        }
        car_capacity.setText(capacity+" seater");
        car_location.setText(location);
        car_perhour.setText( "Per Hour- "+ ctx.getString(R.string.currency)+" " +perhour );
        car_base.setText( "Initial- "+ ctx.getString(R.string.currency)+" " +base );

    }


}
