package com.example.pracadyplomowa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pracadyplomowa.Models.ModelWorkout;
import com.example.pracadyplomowa.R;

import java.util.List;

public class AdapterWorkout extends  RecyclerView.Adapter<AdapterWorkout.MyHolder> {

    private Context context;
    private List<ModelWorkout> workoutList;

    //konstruktor
    public AdapterWorkout(Context context, List<ModelWorkout> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_workout, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String seriesValue = workoutList.get(i).getSeries();
        String repeatsValue = workoutList.get(i).getRepeats();
        String weightValue = workoutList.get(i).getWeight();
        String weightDate = workoutList.get(i).getDate();
        System.out.println(seriesValue+repeatsValue+weightValue);
        myHolder.mSeriesTv.setText(seriesValue);
        myHolder.mRepeatsTv.setText(repeatsValue);
        myHolder.mValueTv.setText(weightValue + "kg");
        myHolder.mDateTv.setText(weightDate);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView mSeriesTv, mRepeatsTv, mValueTv, mDateTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mSeriesTv = itemView.findViewById(R.id.workoutSeriesTv);
            mRepeatsTv = itemView.findViewById(R.id.workoutRepeatsTv);
            mValueTv = itemView.findViewById(R.id.workoutValueTv);
            mDateTv = itemView.findViewById(R.id.workoutDateTv);
        }
    }
}
