package com.example.pracadyplomowa;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CaloriesFragment extends Fragment {

    private TextView bmrTv;
    PieChart pieChart;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    public CaloriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calories, container, false);

        bmrTv = view.findViewById(R.id.bmrTv);
        pieChart = view.findViewById(R.id.pieChart);
        CalcaulateBMR();
        return view;
    }

    private void CalcaulateBMR() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String weight = "" + ds.child("weight").getValue();
                    String height = "" + ds.child("height").getValue();
                    String age = "" + ds.child("age").getValue();
                    String sex = "" + ds.child("sex").getValue();
                    if(weight.equals("") || height.equals("") || age.equals("") || sex.equals("")){
                        bmrTv.setText("Wprowadź dane w profilu");
                    }
                    Double bmrValue = 0.00;
                    if(sex.equals("M")){
                        bmrValue = (Double.valueOf(weight) * 24)
                                + (6.25 * Double.valueOf(height))
                                - (4.92 * Double.valueOf(age))
                                + 5;

                    }else {
                        bmrValue = (Double.valueOf(weight) * 24)
                                + (6.25 * Double.valueOf(height))
                                - (4.92 * Double.valueOf(age))
                                - 161;
                    }
                    bmrTv.setText(String.valueOf(bmrValue));
                    createPie(bmrValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void createPie(Double callory){
        Description desc = new Description();
        desc.setText("Wysokobiałkowa");
        desc.setTextSize(20f);

        pieChart.setDescription(desc);

        pieChart.setHoleRadius(20f);
        List<Double> values = new ArrayList<Double>();
        values.add(callory*0.54 /4);
        values.add(callory*0.20 /4);
        values.add(callory*0.26 /9);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(values.get(0).intValue(), "Weglowodany"));
        value.add(new PieEntry(values.get(1).intValue(), "Bialko"));
        value.add(new PieEntry(values.get(2).intValue(), "Tluszcze"));
        PieDataSet pieDataSet = new PieDataSet(value, "Dieta");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextSize(25f);
        pieDataSet.setValueTextColor(Color.parseColor("#A100C7"));
        pieChart.animateXY(1500, 1500);
    }
}
