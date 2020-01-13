package com.example.pracadyplomowa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pracadyplomowa.Models.ModelWeight;
import com.example.pracadyplomowa.R;

import java.util.List;

public class AdapterWeight extends  RecyclerView.Adapter<AdapterWeight.MyHolder> {

    Context context;
    List<ModelWeight> weightsList;

    //konstruktor
    public AdapterWeight(Context context, List<ModelWeight> weightsList) {
        this.context = context;
        this.weightsList = weightsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_weights, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String Id = String.valueOf(i);
        String weightValue = weightsList.get(i).getWeight();
        String weightDate = weightsList.get(i).getDate();
        myHolder.mIdTv.setText(Id);
        myHolder.mValueTv.setText(weightValue + "kg");
        myHolder.mDateTv.setText(weightDate);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+weightValue, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return weightsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView mValueTv, mDateTv, mIdTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mIdTv = itemView.findViewById(R.id.weightIdTv);
            mValueTv = itemView.findViewById(R.id.weightValueTv);
            mDateTv = itemView.findViewById(R.id.weightDateTv);
        }
    }
}
