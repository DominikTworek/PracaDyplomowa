package com.example.pracadyplomowa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pracadyplomowa.Adapters.AdapterWorkout;
import com.example.pracadyplomowa.Models.ModelWorkout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ExercisesActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView exerciseIv;
    TextView nameExeTv, typeExeTv, instructionTv, maxTv;
    Button addExerciseBt;
    RecyclerView recyclerView;

    AdapterWorkout adapterWorkout;
    List<ModelWorkout> workoutList;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;


    String exerciseId;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        toolbar = findViewById(R.id.toolbarExercise);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        recyclerView = findViewById(R.id.workout_recyclerView);
        exerciseIv = findViewById(R.id.exerciseIv);
        nameExeTv = findViewById(R.id.nameExeTv);
        typeExeTv = findViewById(R.id.typeExeTv);
        instructionTv = findViewById(R.id.instructionTv);
        addExerciseBt = findViewById(R.id.AddExerciseBt);
        maxTv = findViewById(R.id.exerciseMaxTv);




        instructionTv.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        exerciseId = intent.getStringExtra("exerciseId");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Exercises");
        databaseReference2 = firebaseDatabase.getReference("Workout");
        Query exerciseQuery = databaseReference.orderByChild("id").equalTo(exerciseId);
        exerciseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String name =""+ ds.child("name").getValue();
                    String type =""+ ds.child("type").getValue();
                    String image =""+ ds.child("image").getValue();
                    String instruction = ""+ ds.child("Instr").getValue();
                    nameExeTv.setText(name);
                    typeExeTv.setText(type);
                    instructionTv.setText(instruction);
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_default_exercise).into(exerciseIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_default_exercise).into(exerciseIv);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

    });

        addExerciseBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAddExercise();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();
        getAllWorkout();
    }

    private void getAllWorkout() {
        ArrayList<Integer> weightValues = new ArrayList<>();
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workoutList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelWorkout modelWorkout = ds.getValue(ModelWorkout.class);
                    String weight = "" + ds.child("weight").getValue();
                    if(modelWorkout.getUid().equals(user.getUid()) && modelWorkout.getExerciseId().equals(exerciseId)){
                        weightValues.add(Integer.valueOf(weight));
                        workoutList.add(modelWorkout);
                    }
                    adapterWorkout = new AdapterWorkout(ExercisesActivity.this,workoutList);
                    recyclerView.setAdapter(adapterWorkout);
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
                    maxTv.setText(String.valueOf(max) + "kg");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void viewAddExercise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj Serie");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.setOrientation((LinearLayout.VERTICAL));
        EditText editText = new EditText(this);
        editText.setHint("Ciężar");
        editText.setWidth(10);
        editText.setMaxWidth(10);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText editText2 = new EditText(this);
        editText2.setHint("Wpisz ilość powtórzeń");
        editText2.setWidth(10);
        editText2.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText editText3 = new EditText(this);
        editText3.setHint("Wpisz ilość serii");
        editText3.setWidth(10);
        editText3.setInputType(InputType.TYPE_CLASS_NUMBER);

        linearLayout.addView(editText);
        linearLayout.addView(editText2);
        linearLayout.addView(editText3);

        builder.setView(linearLayout);
        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String weight = editText.getText().toString().trim();
            String repeats = editText2.getText().toString().trim();
            String series = editText3.getText().toString().trim();
            addToDatabase(weight,repeats,series);
            dialog.dismiss();
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void addToDatabase(String weight,String repeats, String series) {
        HashMap<String, Object> result = new HashMap<>();
        Date date = new Date();
        String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        result.put("exerciseId", exerciseId);
        result.put("weight", weight);
        result.put("repeats", repeats);
        result.put("series", series);
        result.put("uid", user.getUid());
        result.put("email", user.getEmail());
        result.put("date", modifiedDate);
        databaseReference2.child(user.getUid() + modifiedDate+exerciseId).setValue(result)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Aktualizacja...", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void checkUserLogin() {
        //pobieranie aktywnego usera
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            myUid = firebaseUser.getUid();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

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
