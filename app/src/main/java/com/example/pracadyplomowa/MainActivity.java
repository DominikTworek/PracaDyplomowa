package com.example.pracadyplomowa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //widoki
    Button RejestracjaBtn, LoginBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init widoki
        RejestracjaBtn = findViewById(R.id.register_btn);
        LoginBtn = findViewById(R.id.login_btn);

        //akcja po naciśnięciu przycisku rejestracji
        RejestracjaBtn.setOnClickListener(view -> {
            //Rejestracja
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
        LoginBtn.setOnClickListener(view -> {
            //Rejestracja
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

    }

}
