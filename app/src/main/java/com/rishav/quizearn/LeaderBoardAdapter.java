package com.rishav.quizearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.auth.User;
import com.rishav.quizearn.databinding.RowLeadboardsBinding;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderViewHolder> {

    Context context;
    ArrayList<Users> users;

    public LeaderBoardAdapter(Context context, ArrayList<Users> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public LeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leadboards,null);
        return new LeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderViewHolder holder, int position) {
        Users user = users.get(position);
        holder.binding.name.setText(user.getName());
        holder.binding.coins.setText(String.valueOf(user.getCoins()));
        holder.binding.index.setText(String.format("#%d",position+1));
        Glide.with(context)
                .load(user.getProfile())
                .into(holder.binding.image);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class LeaderViewHolder extends RecyclerView.ViewHolder{
         RowLeadboardsBinding binding;
        public LeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLeadboardsBinding.bind(itemView);
        }
    }
}
