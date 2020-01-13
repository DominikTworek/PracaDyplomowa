package com.example.pracadyplomowa;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_Imgs/";

    private ImageView userIv, coverIv;
    private TextView nameTv, emailTv, heightTv, weightTv, ageTv, sexTv;

    private FloatingActionButton fab;

    //private ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private String cameraPermissions[];
    private String storagePermissions[];

    private Uri image_uri;

    private String userOrCoverPhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        databaseReference2 = firebaseDatabase.getReference("Weight");
        storageReference = getInstance().getReference();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        userIv = view.findViewById(R.id.userIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        heightTv = view.findViewById(R.id.heightTv);
        weightTv = view.findViewById(R.id.weightTv);
        ageTv = view.findViewById(R.id.ageTv);
        sexTv = view.findViewById(R.id.sexTv);
        fab = view.findViewById(R.id.fab);
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    String height = "" + ds.child("height").getValue();
                    String weight = "" + ds.child("weight").getValue();
                    String age = "" + ds.child("age").getValue();
                    String sex = "" + ds.child("sex").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    heightTv.setText(height+"cm");
                    weightTv.setText(weight+"kg");
                    ageTv.setText(age);
                    sexTv.setText(sex);
                    try {
                        Picasso.get().load(image).into(userIv);
                    } catch (Exception ex) {
                        Picasso.get().load(R.drawable.ic_add_image).into(userIv);
                    }
                    try {
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception ex) {
                        Picasso.get().load(R.drawable.ic_add_image).into(coverIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        return view;
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        String options[] = {"Edytuj Zdjęcie profilowe", "Edytuj zdjęcie w tle", "Zmień nazwę", "Zmień wagę", "Zmień wzrost", "Zaktualizuj Płeć", "Zaktualizuj Wiek"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wybierz pole");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //edycja profilu
                    //pd.setMessage("Zaktualizuj Zdjęcie profilowe");
                    userOrCoverPhoto = "image";
                    showImagePicDialog();
                } else if (which == 1) {
                    //edycja zdjecia w tle
                    //pd.setMessage("Zaktualizuj Zdjęcie w tle");
                    userOrCoverPhoto = "cover";
                    showImagePicDialog();
                } else if (which == 2) {
                    //edycja nazwy
                    //pd.setTooltipText("Zaktualizuj nazwę");
                    showEditTextDialog("name");
                } else if (which == 3) {
                    //edycja wagi
                    //pd.setMessage("Zaktualizuj wage");
                    showEditTextDialog("weight");
                } else if (which == 4) {
                    //edycja wzrostu
                    //pd.setMessage("Zaktualizuj wzrost");
                    showEditTextDialog("height");
                } else if (which == 5) {
                    //edycja płci
                    //pd.setMessage("Zaktualizuj swoją płeć");
                    showEditTextDialog("sex");
                } else if (which == 6) {
                    //edycja płci
                    //pd.setMessage("Zaktualizuj swoją płeć");
                    showEditTextDialog("age");
                }
            }
        });
        builder.create().show();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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

    private void addToDatabaseWeight(String value) {
        HashMap<String, Object> result = new HashMap<>();
        Date date = new Date();
        String modifiedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        result.put("weight", value);
        result.put("uid", user.getUid());
        result.put("email", user.getEmail());
        result.put("date", modifiedDate);
        databaseReference2.child(user.getUid() + modifiedDate).setValue(result)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Aktualizacja...", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditTextDialog(String key) {
        //pd.show();
        String title = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (key.equals("name")) title = " Nick";
        if (key.equals("weight")) title = " Wagę";
        if (key.equals("height")) title = " Wysokość";
        if (key.equals("sex")) title = " Płeć";
        if (key.equals("age")) title = " Wiek";

        builder.setTitle("Aktualizuj" + title);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        if (key.equals("sex")) {
            linearLayout.setOrientation((LinearLayout.HORIZONTAL));
            TextView text = new TextView(getActivity());
            text.setText("Wybierz swoją płeć przyciskiem poniżej");
            text.setPadding(10, 10, 10, 10);
            linearLayout.addView(text);
            builder.setPositiveButton("Kobieta", (dialog, which) -> {
                addToDatabase(key, "K");
                dialog.dismiss();
            });
            builder.setNegativeButton("Męzczyzna", (dialog, which) -> {
                addToDatabase(key, "M");
                dialog.dismiss();
            });
        } else {
            if(key.equals("weight") ||key.equals("height") ){
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            linearLayout.setOrientation((LinearLayout.VERTICAL));
            editText.setHint("Wpisz" + title);
            linearLayout.addView(editText);
            builder.setPositiveButton("Aktualizuj", (dialog, which) -> {
                String value = editText.getText().toString().trim();
                if (key.equals("weight") || key.equals("height")) {
                    if (isNumeric(value)) {
                        if (!TextUtils.isEmpty(value)) {
                            if (key.equals("weight")) {
                                addToDatabaseWeight(value);
                                addToDatabase(key, value);
                            } else {
                                addToDatabase(key, value);
                            }
                        } else {
                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Wpisane dane muszą być liczbą", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    if (!TextUtils.isEmpty(value)) {
                        addToDatabase(key, value);
                    } else {
                        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.setView(linearLayout);
        builder.create().show();
    }

    private void showImagePicDialog() {
        String options[] = {"Aparat", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wybierz zdjęcie z");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                //aparat
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (which == 1) {
                //galeria
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Proszę włącz uprawnienia do używania aparatu oraz danych", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Proszę włącz uprawnienia do używania danych", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //metoda ta jest wywoływana po pobraniu zdjęćia z galleri/aparatu
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                uploadUserCoverPhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadUserCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadUserCoverPhoto(Uri uri) {
        //pd.show();
        String filePathAndName = storagePath + "" + userOrCoverPhoto + "_" + user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();

            if (uriTask.isSuccessful()) {
                HashMap<String, Object> results = new HashMap<>();
                results.put(userOrCoverPhoto, downloadUri.toString());
                databaseReference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Aktualizacja zdjęcia...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Błąd aktualizacji zdjęcia...", Toast.LENGTH_SHORT).show();

                });
            } else {
                Toast.makeText(getContext(), "Nieznany błąd", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Tymaczosowy tytuł");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Tymczasowy opis");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUserLogin() {
        //pobieranie aktywnego usera
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            //mUserTv.setText(firebaseUser.getEmail());
        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //obsługa przycisków menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserLogin();
        }
        return super.onOptionsItemSelected(item);
    }

}
