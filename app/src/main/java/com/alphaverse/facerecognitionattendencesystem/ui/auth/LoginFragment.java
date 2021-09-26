package com.alphaverse.facerecognitionattendencesystem.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.ui.home.HomeActivity;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LoginFragment extends Fragment {
    private View loginView;
    private EditText emailIdEt, passwordEt;
    private AppCompatButton loginBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = fireStore.collection("users");
    private final String TAG = getClass().getSimpleName();
    private ActionListener actionListener;
    private ProgressBar progressBar;
    private TextView tvSignUp;
    private ArrayList<String> emailArray = new ArrayList<>();

    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot.exists()) {
                        emailArray.add((String) documentSnapshot.get("userEmailId"));
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginView = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn = loginView.findViewById(R.id.btn_login);
        emailIdEt = loginView.findViewById(R.id.et_login_email);
        passwordEt = loginView.findViewById(R.id.et_login_password);
        progressBar = loginView.findViewById(R.id.login_progress);
        progressBar.setVisibility(View.INVISIBLE);
        tvSignUp = loginView.findViewById(R.id.tv_signUp);
        tvSignUp.setOnClickListener(v -> {
            actionListener.onSignUpClicked();
        });
        loginBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            signIn();
        });

        return loginView;
    }

    private void signIn() {
        String emailText = emailIdEt.getText().toString().trim();
        String passText = passwordEt.getText().toString().trim();

        if (emailText.isEmpty()) {
            emailIdEt.setError("Please enter an Email Address");
            emailIdEt.requestFocus();
            return;
        }

        if (passText.isEmpty()) {
            passwordEt.setError("Please enter a Password");
            passwordEt.requestFocus();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(emailText, passText).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            currentUser = task.getResult().getUser();
                            String user = currentUser.getUid();
                            collectionReference.document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onEvent: " + task.getResult().get("userName"));
                                        CurrentUserAPI currentUserAPI = CurrentUserAPI.getInstance();
                                        currentUserAPI.setUserEmail(task.getResult().get("userEmail").toString());
                                        currentUserAPI.setUserName(task.getResult().get("userName").toString());
                                        currentUserAPI.setUserClassSection(task.getResult().get("userClassSection").toString());
                                        currentUserAPI.setCurrentUserUid(task.getResult().get("userId").toString());
                                        currentUserAPI.setUserYear(Integer.parseInt(task.getResult().get("userYear").toString()) );
                                        currentUserAPI.setUserNoOfStudents(Integer.parseInt(task.getResult().get("userNoOfStudents").toString()));
                                        currentUserAPI.setUserTotalSubjects(Integer.parseInt(task.getResult().get("userNoOfSubjects").toString()));
                                        CollectionReference subjectReference = collectionReference.document(user).collection("subjects");
                                        subjectReference.get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if (!queryDocumentSnapshots.isEmpty()) {
                                                            ArrayList<Subject> subjects=new ArrayList<>();
                                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                                Subject subject =snapshot.toObject(Subject.class);
                                                                    subjects.add(subject);
                                                            }
                                                            currentUserAPI.setSubjectList(subjects);
                                                        }
                                                    }
                                                });
                                        progressBar.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(getActivity(), HomeActivity.class));
                                        getActivity().finish();
                                    }
                                }
                            }).addOnFailureListener(e -> {
                                
                                Toast.makeText(getContext(), "Error in db task successful", Toast.LENGTH_SHORT).show();
                            });

                        } else {
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error in failure of sign in" + e.getMessage());
                    }

                });
    }

    public void setActionListener(Context context) {
        this.actionListener = (ActionListener) context;
    }

}