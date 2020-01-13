package com.example.pracadyplomowa;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment {
    private FloatingActionButton fab;

    TextView bodyKlataTv, bodyKarkTv, bodyBicepsTv, bodyPrzedramieTv, bodyTaliaTv, bodyPasTv, bodyBiodraTv, bodyUdoTv, bodyLydkaTv;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;

    public WorkoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference2 = firebaseDatabase.getReference("Body");

        fab = view.findViewById(R.id.fab);
        bodyBicepsTv = view.findViewById(R.id.bodyBicepsTv);
        bodyBiodraTv = view.findViewById(R.id.bodyBiodraTv);
        bodyKarkTv = view.findViewById(R.id.bodyKarkTv);
        bodyKlataTv = view.findViewById(R.id.bodyKlataTv);
        bodyLydkaTv = view.findViewById(R.id.bodyLydkaTv);
        bodyPasTv = view.findViewById(R.id.bodyPasTv);
        bodyPrzedramieTv = view.findViewById(R.id.bodyPrzedramieTv);
        bodyTaliaTv = view.findViewById(R.id.bodyTaliaTv);
        bodyUdoTv = view.findViewById(R.id.bodyUdoTv);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String biceps = "" + ds.child("biceps").getValue();
                    String biodra = "" + ds.child("biodra").getValue();
                    String kark = "" + ds.child("kark").getValue();
                    String klatka = "" + ds.child("klatka").getValue();
                    String lydka = "" + ds.child("lydka").getValue();
                    String pas = "" + ds.child("pas").getValue();
                    String przedramie = "" + ds.child("przedramie").getValue();
                    String talia = "" + ds.child("talia").getValue();
                    String udo = "" + ds.child("udo").getValue();

                    bodyBicepsTv.setText(biceps + "cm");
                    bodyBiodraTv.setText(biodra + "cm");
                    bodyKarkTv.setText(kark + "cm");
                    bodyKlataTv.setText(klatka + "cm");
                    bodyLydkaTv.setText(lydka + "cm");
                    bodyPasTv.setText(pas + "cm");
                    bodyPrzedramieTv.setText(przedramie + "cm");
                    bodyTaliaTv.setText(talia + "cm");
                    bodyUdoTv.setText(udo + "cm");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(v -> showEditBodySize());

        return view;
    }

    private void showEditBodySize() {
        String options[] = {"Łydka", "Udo", "Biodra", "Pas", "Talia", "Klatka", "Kark", "Biceps", "Przedramię"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edytuj rozmiar w cm");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showEditTextDialog("lydka");
                } else if (which == 1) {
                    showEditTextDialog("udo");
                } else if (which == 2) {
                    showEditTextDialog("biodra");
                } else if (which == 3) {
                    showEditTextDialog("pas");
                } else if (which == 4) {
                    showEditTextDialog("talia");
                } else if (which == 5) {
                    showEditTextDialog("klatka");
                } else if (which == 6) {
                    showEditTextDialog("kark");
                } else if (which == 7) {
                    showEditTextDialog("biceps");
                } else if (which == 8) {
                    showEditTextDialog("przedramie");
                }
            }
        });
        builder.create().show();
    }

    private void showEditTextDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Aktualizuj " + key);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        linearLayout.setOrientation((LinearLayout.VERTICAL));
        editText.setHint("Wpisz obwód w cm");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(editText);
        builder.setPositiveButton("Aktualizuj", (dialog, which) -> {
            String value = editText.getText().toString().trim();
            addToDatabase(key, value);
            addToDatabaseBody(key, value);
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.dismiss());
        builder.setView(linearLayout);
        builder.create().show();
    }

    private void addToDatabaseBody(String key, String value) {
        Date date = new Date();
        String modifiedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", key);
        result.put("key", value);
        result.put("value", value);
        result.put("uid", user.getUid());
        result.put("email", user.getEmail());
        result.put("date", modifiedDate);
        databaseReference2.child(user.getUid()+key+modifiedDate).updateChildren(result)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Aktualizacja...", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void addToDatabase(String key, String value) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(key, value);
        databaseReference.child(user.getUid()).updateChildren(result)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Aktualizacja...", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


}
