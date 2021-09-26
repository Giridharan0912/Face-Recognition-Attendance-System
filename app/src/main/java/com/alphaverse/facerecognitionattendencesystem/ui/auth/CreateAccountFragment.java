package com.alphaverse.facerecognitionattendencesystem.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.ui.home.HomeActivity;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class CreateAccountFragment extends Fragment implements AddSubjectDialogFragment.AddSubjectInterface {
    private RecyclerView createSubjectRV;
    private AppCompatButton createAccountBtn;
    private View createAccountView;
    private ArrayList<Subject> subjectList = new ArrayList<>();
    private Bundle userDetails;
    private FirebaseAuth.AuthStateListener authStateListener;
    private int totalSubjects;
    private ProgressBar createAccntProgressBar;
    SubjectsRecyclerViewAdapter subjectsRecyclerViewAdapter;
    public static final int REQ_CODE = 50;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = firestore.collection("users");
    private FirebaseUser currentUser;
    public static final String TAG = "CreateAccountFragment";
    String email;
    String name;
    String classSection;
    int year;
    int students;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    public CreateAccountFragment(Bundle bundle) {
        this.userDetails = bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalSubjects = userDetails.getInt("userClasses");
        email = userDetails.getString("userEmail");
        name = userDetails.getString("userName");
        classSection = userDetails.getString("userClassSection");
        year = userDetails.getInt("userYear");
        students = userDetails.getInt("userNoOfStudents");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        createAccountView = inflater.inflate(R.layout.fragment_create_account, container, false);
        createSubjectRV = createAccountView.findViewById(R.id.create_accnt_subject_rv);
        floatingActionButton = createAccountView.findViewById(R.id.floating_btn);
        createAccountBtn = createAccountView.findViewById(R.id.create_accnt_btn);
        createAccountBtn.setVisibility(View.INVISIBLE);
        createAccntProgressBar = createAccountView.findViewById(R.id.create_accnt_progress_bar);
        createAccntProgressBar.setVisibility(View.INVISIBLE);
        floatingActionButton.setOnClickListener(v -> {
            if (subjectList.size() < totalSubjects) {
                AddSubjectDialogFragment subjectDialogFragment = new AddSubjectDialogFragment();
                subjectDialogFragment.setTargetFragment(CreateAccountFragment.this, REQ_CODE);
                subjectDialogFragment.show(getFragmentManager(), "CreateSubjectDialog");
            } else {
                Toast.makeText(getContext(), "Just " + totalSubjects + " Can be created", Toast.LENGTH_SHORT).show();
            }
        });
        setSubjectList();

        createAccountBtn.setOnClickListener(v -> {
            if (subjectList.size() == totalSubjects) {
                createAccntProgressBar.setVisibility(View.VISIBLE);
                createAccountFirebase();
            }
        });
        return createAccountView;
    }

    private void setSubjectList() {
        subjectsRecyclerViewAdapter = new SubjectsRecyclerViewAdapter(getContext(), totalSubjects);
        createSubjectRV.setAdapter(subjectsRecyclerViewAdapter);
        createSubjectRV.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @Override
    public void subjectDialog(boolean b, Subject subject) {
        if (b) {
            subjectList.add(subject);
            subjectsRecyclerViewAdapter.setSubjectArrayList(subjectList);
        }
        if (subjectList.size() == totalSubjects) {
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    private void createAccountFirebase() {
        String password = userDetails.getString("userPassword");

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ClassData classData = new ClassData(classSection, year, name, students, totalSubjects);
                    currentUser = firebaseAuth.getCurrentUser();
                    String groupId = currentUser.getUid().toLowerCase();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
                            try {
                                faceServiceClient.createLargePersonGroup(groupId, name, new Gson().toJson(classData));
                                createFireStore();
                            } catch (ClientException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

    }


    private class ClassData {
        String classSection;
        int year;
        String classStaff;
        int noOfStudents;
        int noOfSubjects;

        public ClassData(String classSection, int year, String classStaff, int noOfStudents, int noOfSubjects) {
            this.classSection = classSection;
            this.year = year;
            this.classStaff = classStaff;
            this.noOfStudents = noOfStudents;
            this.noOfSubjects = noOfSubjects;
        }
    }

    private void createFireStore() {
        HashMap<String, Object> detailMap = new HashMap<>();
        detailMap.put("userId", currentUser.getUid());
        detailMap.put("userName", name);
        detailMap.put("userEmail", email);
        detailMap.put("userClassSection", classSection);
        detailMap.put("userYear", year);
        detailMap.put("userNoOfStudents", students);
        detailMap.put("userNoOfSubjects", totalSubjects);

        userCollection.document(currentUser.getUid()).set(detailMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void documentReference) {
                CollectionReference subjectsReference = userCollection.document(currentUser.getUid()).collection("subjects");
                for (int i = 0; i < totalSubjects; i++) {
                    subjectsReference.document(subjectList.get(i).getSubjectId()).set(subjectList.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            createAccntProgressBar.setVisibility(View.INVISIBLE);
                            CurrentUserAPI currentUserAPI = CurrentUserAPI.getInstance();
                            currentUserAPI.setUserName(name);
                            currentUserAPI.setUserEmail(email);
                            currentUserAPI.setUserYear(year);
                            currentUserAPI.setUserClassSection(classSection);
                            currentUserAPI.setCurrentUserUid(currentUser.getUid());
                            currentUserAPI.setUserNoOfStudents(students);
                            currentUserAPI.setUserTotalSubjects(totalSubjects);
                            currentUserAPI.setSubjectList(subjectList);
                            Toast.makeText(getContext(), "user created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                            getActivity().finish();

                        }
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "onSuccess: " + e.getMessage());
                    });
                }


            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onComplete: " + e.getMessage());
        });
    }

}