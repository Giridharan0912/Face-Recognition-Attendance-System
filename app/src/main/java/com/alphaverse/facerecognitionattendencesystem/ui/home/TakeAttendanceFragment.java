package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Student;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class TakeAttendanceFragment extends Fragment {
    private static final String TAG = "TakeAttendanceFragment";
    private View takeAttendanceView;
    private String date;
    private Subject subject;
    private ImageView takeImage;
    private HomeActionListener homeActionListener;
    ListView identifiedStudentsListView;
    private Bitmap takenImgBitmap;
    private String personGroupId;
    boolean isFirstAttendance = true;
    List<Student> identifiedStudents;
    ArrayList<String> allStudents = new ArrayList<>();
    ArrayList<Student> attendedStudents = new ArrayList<>();
    private
    boolean imageSelected = false;
    Drawable myDrawable;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference attendanceReference;
    private CollectionReference studentReference;
    private RecyclerView attendedRv;
    AttendedStudentListAdapter attendedStudentListAdapter;
    private boolean identifyFinished = false;
    HashMap<String, Boolean> hashMap = new HashMap<>();
    private Button savBtn;
    private Context context;

    public TakeAttendanceFragment() {
        // Required empty public constructor
    }

    public TakeAttendanceFragment(String date, Subject subject, Context context) {
        this.date = date;
        this.subject = subject;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDrawable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.attendance_logo, null);
        personGroupId = CurrentUserAPI.getInstance().getCurrentUserUid().toLowerCase();
        attendanceReference = firestore.collection("users").document(CurrentUserAPI.getInstance().getCurrentUserUid()).collection("subjects")
                .document(subject.getSubjectId()).collection("attendance").document(date);
        studentReference = firestore.collection("users").document(CurrentUserAPI.getInstance().getCurrentUserUid()).collection("students");
        Completable.fromAction(() -> getStudentsList()).observeOn(Schedulers.io()).subscribeOn(Schedulers.single())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: completed");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }


    public void setHomeActionListener(Context context) {
        this.homeActionListener = (HomeActionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        takeAttendanceView = inflater.inflate(R.layout.fragment_take_attendance, container, false);
        takeImage = takeAttendanceView.findViewById(R.id.takenImage);
        attendedRv = takeAttendanceView.findViewById(R.id.identifiedStudentsRecyclerView);
        savBtn = takeAttendanceView.findViewById(R.id.buttonSave);
        takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.INVISIBLE);
        takeImage.setOnClickListener(v -> {
            takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.VISIBLE);
            PickImageDialog.build(new PickSetup())
                    .setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult r) {
                            takenImgBitmap = r.getBitmap();
                            if (takenImgBitmap != null) {
                                imageSelected = true;
                                if (isFirstAttendance)
                                    identifiedStudents = new ArrayList<>();
                                ByteArrayOutputStream output = new ByteArrayOutputStream();
                                takenImgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                                ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
                                takeImage.setImageBitmap(takenImgBitmap);

                                new DetectionTask().execute(inputStream);


                                isFirstAttendance = false;
                            } else {
                                imageSelected = false;

                                takeImage.setImageDrawable(myDrawable);
                                takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.INVISIBLE);

                            }

                        }
                    })
                    .setOnPickCancel(new IPickCancel() {
                        @Override
                        public void onCancelClick() {
                            //TODO: do what you have to if user clicked cancel
                        }
                    }).show(getFragmentManager());
        });
        attendedStudentListAdapter = new AttendedStudentListAdapter(getContext());
        attendedRv.setAdapter(attendedStudentListAdapter);
        attendedRv.setLayoutManager(new LinearLayoutManager(getContext()));
        savBtn.setVisibility(View.INVISIBLE);
        savBtn.setOnClickListener(v -> {
            if (hashMap != null) {
                attendanceReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Success");
                        homeActionListener.attendanceCLicked(subject);
                    }
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "onCreateView: " + e.getMessage());
                });
            }
        });
        return takeAttendanceView;
    }

    List<UUID> faceIds;

    private class DetectionTask extends AsyncTask<InputStream, Void, Face[]> {
        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
            try {

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            if (faces != null) {
                if (faces.length == 0) {
                    Log.d(TAG, "No faces detected!");
                    Toast.makeText(getContext(), "No faces detected in the picture", Toast.LENGTH_SHORT).show();

                    takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);

                    takeImage.setImageDrawable(myDrawable);
                } else {
                    faceIds = new ArrayList<>();
                    for (Face face : faces) {
                        faceIds.add(face.faceId);
                    }

                    new TrainPersonGroupTask().execute(personGroupId);
                }
            } else {
                Toast.makeText(getContext(), "No faces detected in the picture", Toast.LENGTH_SHORT).show();

                takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                identifiedStudentsListView.setVisibility(View.VISIBLE);
                takeImage.setImageDrawable(myDrawable);
            }
        }
    }

    private class TrainPersonGroupTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
            try {
                publishProgress("Training person group...");

                faceServiceClient.trainLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Train:" + e.toString() + " " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.GONE);
                Toast.makeText(getContext(), "The Person Group could not be trained", Toast.LENGTH_SHORT).show();
                takeImage.setImageDrawable(myDrawable);
            } else {
                new IdentificationTask().execute(faceIds.toArray(new UUID[faceIds.size()]));
            }
        }
    }

    private class IdentificationTask extends AsyncTask<UUID, Void, IdentifyResult[]> {
        @Override
        protected IdentifyResult[] doInBackground(UUID... uuids) {
            Log.d(TAG, "Request: Identifying faces ");

            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
            try {

                TrainingStatus trainingStatus = faceServiceClient.getLargePersonGroupTrainingStatus(personGroupId);

                if (!trainingStatus.status.toString().equals("Succeeded")) {
                    return null;
                }
                System.out.println("PERSON GROUP ID: " + personGroupId);
                return faceServiceClient.identityInLargePersonGroup(
                        personGroupId,     /* personGroupId */
                        uuids,                  /* faceIds */
                        1);                      /* maxNumOfCandidatesReturned */
            } catch (Exception e) {
                Log.d("", e.getMessage());
                e.printStackTrace();
                Log.d(TAG, "doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            takeImage.setImageDrawable(myDrawable);
            if (identifyResults != null) {
                List<String> personIdsOfIdentified = new ArrayList<>();
                String logString = "Response: Success. ";
                int numberOfUnidentifiedFaces = 0;
                for (IdentifyResult identifyResult : identifyResults) {
                    if (!identifyResult.candidates.isEmpty())
                        personIdsOfIdentified.add(identifyResult.candidates.get(0).personId.toString());

                    if (identifyResult.candidates.size() == 0) {
                        numberOfUnidentifiedFaces++;
                    }

                    logString += "Face " + identifyResult.faceId.toString() + " is identified as "
                            + (identifyResult.candidates.size() > 0
                            ? identifyResult.candidates.get(0).personId.toString()
                            : "Unknown Person")
                            + ". ";
                }
                if (numberOfUnidentifiedFaces > 0)
                    Toast.makeText(getContext(), numberOfUnidentifiedFaces + " face(s) cannot be recognized", Toast.LENGTH_SHORT).show();

                Log.d(TAG, logString);
                //TODO: add attendance data to firebase
                for (int i = 0; i < personIdsOfIdentified.size(); i++) {
                    for (String student : allStudents) {
                        if (student.equals(personIdsOfIdentified.get(i))) {
                            studentReference.document(personIdsOfIdentified.get(i)).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Student addStudent = documentSnapshot.toObject(Student.class);
                                    attendedStudents.add(addStudent);
                                    Log.d(TAG, "onSuccess:Reg " + addStudent.getRegNo());
                                    attendedStudentListAdapter.setStudents(attendedStudents);
                                    hashMap.put(addStudent.getRegNo(), true);
                                    
                                }

                            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
                            break;
                        }
                    }
                }
                takeAttendanceView.findViewById(R.id.takeAttendanceProgress).setVisibility(View.INVISIBLE);
                savBtn.setVisibility(View.VISIBLE);
            }


        }

    }

    private void getStudentsList() {
        studentReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {

                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String personId = snapshot.getString("personId");
                    allStudents.add(personId);
                }

            }
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    }


}