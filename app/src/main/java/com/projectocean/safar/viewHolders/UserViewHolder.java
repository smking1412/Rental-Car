package com.projectocean.safar.viewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private View mView;


    public UserViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

    }


    public void setDetails(String name, String email) {

        TextView textViewName = mView.findViewById(R.id.name);
        TextView textViewEmail = mView.findViewById(R.id.email);

        textViewName.setText(name);
        textViewEmail.setText(email);

    }


}
