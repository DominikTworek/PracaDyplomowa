package com.example.pracadyplomowa;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn;
    TextView mAccountTv;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stwórz konto");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mAccountTv = findViewById(R.id.accountTv);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Rejestrowanie użytkownika...");

        mRegisterBtn.setOnClickListener((v) -> {

            String email = mEmailEt.getText().toString().trim();
            String password = mPasswordEt.getText().toString().trim();
            String upperCaseChars = "(.*[A-Z].*)";
            String lowerCaseChars = "(.*[a-z].*)";
            String numbers = "(.*[0-9].*)";
            String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
            //validacja
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                //error
                mEmailEt.setError("Błędny Email");
                mEmailEt.setFocusable(true);
            } else if (password.length() < 6) {
                mPasswordEt.setError("Hasło musi zawierać 6 znaków");
                mPasswordEt.setFocusable(true);
            } else if (!password.matches(upperCaseChars)) {
                mPasswordEt.setError("Hasło musi zawierać jedną dużą literę");
                mPasswordEt.setFocusable(true);
            } else if (!password.matches(lowerCaseChars)) {
                mPasswordEt.setError("Hasło musi zawierać jedną małą literę");
                mPasswordEt.setFocusable(true);
            } else if (!password.matches(numbers)) {
                mPasswordEt.setError("Hasło musi zawierać jedną cyfrę");
                mPasswordEt.setFocusable(true);
            } else if (!password.matches(specialChars)) {
                mPasswordEt.setError("Hasło musi zawierać jeden znak specjalny");
                mPasswordEt.setFocusable(true);
            } else {
                registerUser(email, password);
            }
        });
        //przejście jeśli masz konto
        mAccountTv.setOnClickListener((v) -> {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
        });
    }

    private void registerUser(String emailR, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailR, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Date date = new Date();
                        String modifiedDate= new SimpleDateFormat("dd-MM-yyyy").format(date);
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        String uid = user.getUid();
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);
                        hashMap.put("name", "Nick");
                        hashMap.put("image", "");
                        hashMap.put("cover", "");
                        hashMap.put("height", "0");
                        hashMap.put("weight", "0");
                        hashMap.put("sex", "");
                        hashMap.put("age", "");
                        HashMap<Object, String> hashMap2 = new HashMap<>();
                        hashMap2.put("email", email);
                        hashMap2.put("uid", uid);
                        hashMap2.put("weight", "0");
                        hashMap2.put("date", modifiedDate);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference2 = database.getReference("Weight");
                        reference2.child((uid)).setValue(hashMap2);
                        DatabaseReference reference = database.getReference("Users");
                        reference.child((uid)).setValue(hashMap);

                        Toast.makeText(RegisterActivity.this, "Stworzono...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //przejście do poprzedniej aktywności
        return super.onSupportNavigateUp();
    }
}
