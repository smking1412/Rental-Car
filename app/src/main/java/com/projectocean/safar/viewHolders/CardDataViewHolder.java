package com.projectocean.safar.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.R;

public class CardDataViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    public ImageView chip;

    public CardDataViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
        chip=mView.findViewById(R.id.chip);
    }

    public void setDetails(String number,Integer mm,Integer yy,String name,Integer cvv){
        TextView textViewNumber = mView.findViewById(R.id.number);
        TextView textViewExp = mView.findViewById(R.id.validupto);
        TextView textViewName = mView.findViewById(R.id.name);
        TextView textViewCVV = mView.findViewById(R.id.cvv);

        textViewName.setText(name.toUpperCase());
        textViewNumber.setText(number);
        textViewCVV.setText("CVV "+cvv);
        textViewExp.setText("VALID UPTO "+mm+"/"+yy);


    }
}
