//Parul
package com.example.trakkus.Viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trakkus.Interface.IRecycItemListerner;
import com.example.trakkus.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CircleImageView all_friends_profile_image;

    public TextView all_friends_txt_user_email, Delete_User;
    IRecycItemListerner iRecycItemListerner;

    public void setiRecycItemListerner(IRecycItemListerner iRecycItemListerner) {
        this.iRecycItemListerner = iRecycItemListerner;
    }

    public AllFriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        all_friends_profile_image = (CircleImageView) itemView.findViewById(R.id.all_friends_profile_image);

        all_friends_txt_user_email = (TextView) itemView.findViewById(R.id.all_friends_txt_user_email);
        
        Delete_User = itemView.findViewById(R.id.delete_user);
        
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        iRecycItemListerner.onItemClickListener(view, getAdapterPosition());

    }
}
