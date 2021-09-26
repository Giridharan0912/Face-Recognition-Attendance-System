package com.alphaverse.facerecognitionattendencesystem.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.ui.auth.AuthActivity;
import com.alphaverse.facerecognitionattendencesystem.ui.home.HomeActivity;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressBar progressBar;
    private FirebaseUser mUser;
    private String user;
    public static final int REQ_CODE = 11;
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = fireStore.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.splash_pb);
        if (ActivityCompat.checkSelfPermission(SplashScreenActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE);
        }

        authStateListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();

            if (mUser != null) {
                user = mUser.getUid();
                progressBar.setVisibility(View.VISIBLE);
                collectionReference.document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            try {
                                Log.d(TAG, "onEvent: " + task.getResult().get("userName"));
                                CurrentUserAPI currentUserAPI = CurrentUserAPI.getInstance();
                                currentUserAPI.setUserEmail(task.getResult().get("userEmail").toString());
                                currentUserAPI.setUserName(task.getResult().get("userName").toString());
                                currentUserAPI.setUserClassSection(task.getResult().get("userClassSection").toString());
                                currentUserAPI.setCurrentUserUid(task.getResult().get("userId").toString());
                                currentUserAPI.setUserYear(Integer.parseInt(task.getResult().get("userYear").toString()));
                                currentUserAPI.setUserNoOfStudents(Integer.parseInt(task.getResult().get("userNoOfStudents").toString()));
                                currentUserAPI.setUserTotalSubjects(Integer.parseInt(task.getResult().get("userNoOfSubjects").toString()));
                                CollectionReference subjectReference = collectionReference.document(user).collection("subjects");
                                subjectReference.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    ArrayList<Subject> subjects = new ArrayList<>();
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        Subject subject = snapshot.toObject(Subject.class);
                                                        subjects.add(subject);
                                                    }
                                                    currentUserAPI.setSubjectList(subjects);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }


                        }
                    }
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "onCreate: " + e.getMessage());
                });
            } else {
                startActivity(new Intent(SplashScreenActivity.this, AuthActivity.class));
                finish();
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SplashScreenActivity.this, "permitted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SplashScreenActivity.this, "not permitted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}