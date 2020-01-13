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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.pracadyplomowa.Adapters.AdapterBody;
import com.example.pracadyplomowa.Models.ModelBody;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
public class BodyFragment extends Fragment {

    private RadioGroup radioGroup;
    RecyclerView recyclerView;
    List<ModelBody> bodyList;
    FirebaseAuth firebaseAuth;
    AdapterBody adapterBody;
    LineChart lineChart;

    public BodyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_body, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.body_recyclerView);
        lineChart = view.findViewById(R.id.lineChartBody);



        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.lydkaRb){
                    getAllExercises("lydka");
                }else if (checkedId == R.id.udoRb){
                    getAllExercises("udo");
                }else if (checkedId == R.id.biodraRb){
                    getAllExercises("biodra");
                }else if (checkedId == R.id.pasRb){
                    getAllExercises("pas");
                }else if (checkedId == R.id.taliaRb){
                    getAllExercises("talia");
                }else if (checkedId == R.id.klatkaRb){
                    getAllExercises("klatka");
                }else if (checkedId == R.id.karkRb){
                    getAllExercises("kark");
                }else if (checkedId == R.id.bicepsRb){
                    getAllExercises("biceps");
                }else if (checkedId == R.id.przedramieRb){
                    getAllExercises("przedramie");
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bodyList = new ArrayList<>();
        return view;
    }


    private void getAllExercises(String key) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Body");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bodyList.clear();
                ArrayList<Entry> values = new ArrayList<>();
                final float[] i = {0f};
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelBody modelBody = ds.getValue(ModelBody.class);
                    if(Objects.requireNonNull(modelBody).getUid().equals(Objects.requireNonNull(user).getUid()) && modelBody.getId().equals(key)){
                        String value = "" + ds.child("value").getValue();
                        float valueNumber = Float.parseFloat(value);
                        bodyList.add(modelBody);
                        values.add(new Entry(i[0], valueNumber));
                        i[0] = i[0] + 1f;
                    }
                    adapterBody = new AdapterBody(getActivity(), bodyList);
                    recyclerView.setAdapter(adapterBody);
                }
                buildChart(values);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buildChart(ArrayList<Entry> arrayList) {
        LineDataSet set1 = new LineDataSet(arrayList, "Waga");
        set1.setColor(Color.parseColor("#A100C7"));
        set1.setLineWidth(6f);
        set1.setValueTextSize(15f);
        set1.setCircleRadius(6);
        set1.setCircleColor(Color.GREEN);
        set1.setValueTextColor(Color.GREEN);
        set1.setFillColor(Color.RED);
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
