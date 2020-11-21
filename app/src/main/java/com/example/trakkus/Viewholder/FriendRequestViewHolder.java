package com.example.trakkus.Viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trakkus.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {


    public TextView txt_friend_email;
    public Button btn_accept, btn_decline;
    public CircleImageView friend_request_image;


    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_friend_email = (TextView) itemView.findViewById(R.id.txt_friend_email);

        btn_accept =  itemView.findViewById(R.id.accept_friend_request);
        btn_decline =  itemView.findViewById(R.id.cancel_friend_request);
        friend_request_image = (CircleImageView) itemView.findViewById(R.id.recycler_friend_image);


    }


}
