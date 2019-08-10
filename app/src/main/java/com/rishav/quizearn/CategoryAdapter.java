package com.rishav.quizearn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    Context context;
    ArrayList<CategoryModel> categoryModels;
    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels){
        this.context=context;
        this.categoryModels=categoryModels;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category,null);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
       final  CategoryModel model = categoryModels.get(position);

        holder.textView.setText(model.getCategoryName());
        Glide.with(context)
                .load(model.getCategoryImage())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected())){
                    Intent intent = new Intent(context,Quiz.class);
                    Bundle extras = new Bundle();
                    extras.putString("catId",model.getCategoryId());
                    extras.putString("catName",model.getCategoryName());
                    intent.putExtras(extras);
                    context.startActivity(intent);

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
                    View view = LayoutInflater.from(context).inflate(R.layout.no_internet,null);
                    builder.setView(view);
                    Button connect;
                    connect=view.findViewById(R.id.tryAgain);
                    final AlertDialog alertDialog = builder.create();
                    connect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //alertDialog.dismiss();
                            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                            if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected())){
                                alertDialog.dismiss();
                                Intent intent = new Intent(context,Quiz.class);
                                Bundle extras = new Bundle();
                                extras.putString("catId",model.getCategoryId());
                                extras.putString("catName",model.getCategoryName());
                                intent.putExtras(extras);
                                context.startActivity(intent);
                            }
                        }
                    });

                    if (alertDialog.getWindow() != null){
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    alertDialog.show();

                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.imagecategory);
            textView = itemView.findViewById(R.id.category);
        }
    }
}
