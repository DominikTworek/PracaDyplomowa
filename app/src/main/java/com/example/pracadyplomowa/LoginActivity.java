package com.example.pracadyplomowa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    EditText mEmailEt, mPasswordEt;
    TextView mRegisterTv, mRecoveryPassTv;
    Button mLoginBt;
    SignInButton mGoogleLoginBtn;

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterTv = findViewById(R.id.registerTv);
        mRecoveryPassTv = findViewById(R.id.recoveryPassTv);
        mLoginBt = findViewById(R.id.loginBtn);
        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn);

        //przycisk zaloguj
        mLoginBt.setOnClickListener(v -> {
            //init dane
            String email = mEmailEt.getText().toString();
            String password = mPasswordEt.getText().toString();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmailEt.setError("Błędny Email");
                mEmailEt.setFocusable(true);
            }
            else {
                loginUser(email, password);
            }
        });
        //Brak konta
        mRegisterTv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        //Zapomniałem hasła
        mRecoveryPassTv.setOnClickListener(v -> showRecoverPasswordDialog());

        //google logowanie
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder ADBuilder = new AlertDialog.Builder(this);
        ADBuilder.setTitle("Odzyskiwanie Hasła");

        LinearLayout linearLayout = new LinearLayout(this);

        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(15);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        ADBuilder.setView(linearLayout);

        ADBuilder.setPositiveButton("Odzyskaj", (dialog, which) -> {
            String email = emailEt.getText().toString().trim();
            Recovery(email);
        });
        ADBuilder.setNegativeButton("Anuluj", (dialog, which) -> dialog.dismiss());

        ADBuilder.create().show();
    }

    private void Recovery(String email) {
        progressDialog.setMessage("Wysyłanie Emaila...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if(task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Email został wysłany", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginActivity.this, "Błąd...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser(String email, String password) {
        progressDialog.setMessage("Logowanie...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Date date = new Date();
                            String modifiedDate= new SimpleDateFormat("dd-MM-yyyy").format(date);

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
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

                            }
                            /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                            HashMap<Object, String> hashMap3 = new HashMap<>();
                            hashMap3.put("name", "");
                            hashMap3.put("type", "");
                            hashMap3.put("image", "");
                            DatabaseReference reference3 = database.getReference("Exercises");
                            reference3.child("exe10").setValue(hashMap3);*/
                            Toast.makeText(LoginActivity.this, ""+user.getEmail(), Toast.LENGTH_SHORT);
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Logowanie zakończone niepowodzeniem...", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
