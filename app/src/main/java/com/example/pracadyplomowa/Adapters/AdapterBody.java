package com.example.pracadyplomowa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pracadyplomowa.Models.ModelBody;
import com.example.pracadyplomowa.R;

import java.util.List;

public class AdapterBody extends  RecyclerView.Adapter<AdapterBody.MyHolder> {

    Context context;
    List<ModelBody> bodyList;

    //konstruktor
    public AdapterBody(Context context, List<ModelBody> bodyList) {
        this.context = context;
        this.bodyList = bodyList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_body, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        String bodyId = String.valueOf(i);
        String weightValue = bodyList.get(i).getValue();
        String weightDate = bodyList.get(i).getDate();
        System.out.println(weightDate);
        myHolder.mIdTv.setText(bodyId);
        myHolder.mValueTv.setText(weightValue + "cm");
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
        return bodyList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView mValueTv, mDateTv, mIdTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mIdTv = itemView.findViewById(R.id.bodyIdTv);
            mValueTv = itemView.findViewById(R.id.bodyValueTv);
            mDateTv = itemView.findViewById(R.id.bodyDateTv);
        }
    }
}
