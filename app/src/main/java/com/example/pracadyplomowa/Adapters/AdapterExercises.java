package com.example.pracadyplomowa.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pracadyplomowa.ExercisesActivity;
import com.example.pracadyplomowa.Models.ModelExercises;
import com.example.pracadyplomowa.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterExercises extends  RecyclerView.Adapter<AdapterExercises.MyHolder> {

    Context context;
    List<ModelExercises> exerciseList;

    //konstruktor
    public AdapterExercises(Context context, List<ModelExercises> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_exercises, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get data
        final String exerciseId = exerciseList.get(i).getId();
        String exerciseImage = exerciseList.get(i).getImage();
        String exerciseName = exerciseList.get(i).getName();
        String exerciseType = exerciseList.get(i).getType();
        System.out.println(exerciseName);
        //set data
        myHolder.mNameTv.setText(exerciseName);
        myHolder.mTypeTv.setText(exerciseType);
        try{
           Picasso.get().load(exerciseImage).placeholder(R.drawable.ic_default_exercise).into(myHolder.mExerciseIv);
        }catch (Exception e){
        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+exerciseName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ExercisesActivity.class);
                intent.putExtra("exerciseId", exerciseId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mExerciseIv;
        TextView mNameTv, mTypeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mExerciseIv = itemView.findViewById(R.id.exerciseIv);
            mNameTv = itemView.findViewById(R.id.nameExerciseTv);
            mTypeTv = itemView.findViewById(R.id.typeExerciseTv);
        }
    }
}
