package com.rishav.quizearn;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.rishav.quizearn.databinding.FragmentLeaderboardBinding;

import java.util.ArrayList;

public class Leaderboard_Fragment extends Fragment {


    public Leaderboard_Fragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentLeaderboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLeaderboardBinding.inflate(inflater,container,false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final ArrayList<Users> users = new ArrayList<>();
        final LeaderBoardAdapter adapter = new LeaderBoardAdapter(getContext(), users);

        binding.recylerview.setAdapter(adapter);
        binding.recylerview.setLayoutManager(new GridLayoutManager(getContext(),1));

        database.collection("Users")
                .orderBy("coins", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                    Users user = snapshot.toObject(Users.class);
                    users.add(user);

                }
                adapter.notifyDataSetChanged();
                binding.progress.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();
    }
}