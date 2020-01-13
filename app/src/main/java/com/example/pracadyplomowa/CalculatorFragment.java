package com.example.pracadyplomowa;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalculatorFragment extends Fragment {

    Button maxBt, caloryBt, sylBt;

    public CalculatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        maxBt = view.findViewById(R.id.maxBt);
        caloryBt = view.findViewById(R.id.caloryBt);
        sylBt = view.findViewById(R.id.sylBt);

        maxBt.setOnClickListener(v -> {
            WeightFragment weightFragment = new WeightFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, weightFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        caloryBt.setOnClickListener(v -> {
            CaloriesFragment maxFragment = new CaloriesFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, maxFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        sylBt.setOnClickListener(v -> {
            BodyFragment bodyFragment = new BodyFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, bodyFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }

}
