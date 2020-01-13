package com.example.pracadyplomowa;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pracadyplomowa.Adapters.AdapterWeight;
import com.example.pracadyplomowa.Models.ModelWeight;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightFragment extends Fragment{
    RecyclerView recyclerView;
    AdapterWeight adapterWeight;
    List<ModelWeight> weightsList;
    LineChart lineChart;
    TextView maxWeightTv, minWeightTv;

    //firebase auth
    FirebaseAuth firebaseAuth;


    public WeightFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.weights_recyclerView);
        lineChart = view.findViewById(R.id.lineChart);
        maxWeightTv = view.findViewById(R.id.maxWeightTv);
        minWeightTv = view.findViewById(R.id.minWeightTv);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        weightsList = new ArrayList<>();
        getAllExercises();


        return view;
    }

    private void getAllExercises() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Weight");
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> xAxis = new ArrayList<>();
        final float[] i = {0f};
        ArrayList<Integer> weightValues = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weightsList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    ModelWeight modelWeight = ds.getValue(ModelWeight.class);
                    if(Objects.requireNonNull(modelWeight).getUid().equals(Objects.requireNonNull(user).getUid())) {
                        String weight = "" + ds.child("weight").getValue();
                        String date = "" + ds.child("date").getValue();
                        float weightNumber = Float.parseFloat(weight);
                        weightValues.add((int) weightNumber);
                        weightsList.add(modelWeight);
                        values.add(new Entry(i[0], weightNumber));
                        xAxis.add(date);
                        i[0] = i[0] + 1f;

                    }
                    adapterWeight = new AdapterWeight(getActivity(), weightsList);
                    recyclerView.setAdapter(adapterWeight);
                }
                Integer max = 0;
                Integer min = 999;
                for(int i = 0; i < weightValues.size() ; i++){
                    if(weightValues.get(i) < min){
                        min = weightValues.get(i);
                    }
                    if(weightValues.get(i) > max){
                        max = weightValues.get(i);
                    }
                }
                minWeightTv.setText(min+"kg");
                maxWeightTv.setText(max+"kg");
                buildChart(values, xAxis);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buildChart(ArrayList<Entry> arrayList, ArrayList<String> arrayList2){
        //lineChart.setOnChartGestureListener(WeightFragment.this);
        //lineChart.setOnChartValueSelectedListener(WeightFragment.this);


        LineDataSet set1 = new LineDataSet(arrayList, "Waga");
        set1.setColor(Color.parseColor("#A100C7"));
        set1.setLineWidth(6f);
        set1.setValueTextSize(15f);
        set1.setCircleRadius(6);
        set1.setCircleColor(Color.GREEN);
        set1.setValueTextColor(Color.GREEN);
        set1.setFillColor(Color.RED);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return arrayList2.get((int) value);
            }
        };

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setTextColor(Color.parseColor("#A100C7"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setValueFormatter(formatter);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);


    }


}
