package com.example.pracadyplomowa;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.pracadyplomowa.Adapters.AdapterExercises;
import com.example.pracadyplomowa.Models.ModelExercises;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterExercises adapterExercises;
    List<ModelExercises> exerciseList;


    //firebase auth
    FirebaseAuth firebaseAuth;


    public ExercisesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.exercises_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapterExercises);
        exerciseList = new ArrayList<>();
        getAllExercises();


        return view;
    }

    private void getAllExercises() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Exercises");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exerciseList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelExercises modelExercises = ds.getValue(ModelExercises.class);
                    exerciseList.add(modelExercises);
                    adapterExercises = new AdapterExercises(getActivity(), exerciseList);
                    recyclerView.setAdapter(adapterExercises);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserLogin(){
        //pobieranie aktywnego usera
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            //mUserTv.setText(firebaseUser.getEmail());
        }
        else{
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s.trim())){
                    searchExercises(s);
                }else{
                    getAllExercises();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s.trim())){
                    searchExercises(s);
                }else{
                    getAllExercises();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchExercises(String query) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Exercises");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exerciseList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelExercises modelExercises = ds.getValue(ModelExercises.class);
                    if(modelExercises.getName().toLowerCase().contains(query.toLowerCase()) ||
                            modelExercises.getType().toLowerCase().contains(query.toLowerCase())){
                        exerciseList.add(modelExercises);
                    }
                    adapterExercises = new AdapterExercises(getActivity(), exerciseList);
                    adapterExercises.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterExercises);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //obsługa przycisków menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserLogin();
        }
        return super.onOptionsItemSelected(item);
    }

}
