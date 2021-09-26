package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickClick;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class AddStudentsFragment extends Fragment implements IPickClick {
    private static final String TAG = "AddStudentsFragment";
    private HomeActionListener homeActionListener;
    String studentName;
    String regNo;
    private View addStudentsView;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference faceReference;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ImageView takenImageForStudent;
    private CollectionReference addStudentCollection;
    Bitmap bitmap;
    private EditText studentNameEt;
    private EditText studentRegNoEt;
    boolean imageTaken = false;
    private UploadTask uploadTask;
    private AppCompatButton addStudentBtn;
    private Context context;
    private Uri imgUri;
    private ArrayList<String> alreadyAddedStudents = new ArrayList<>();

    public AddStudentsFragment() {
        // Required empty public constructor
    }

    public AddStudentsFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addStudentCollection = firebaseFirestore.collection("users").document(CurrentUserAPI.getInstance().getCurrentUserUid()).collection("students");
        addStudentCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        alreadyAddedStudents.add(snapshot.get("regNo").toString());
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addStudentsView = inflater.inflate(R.layout.fragment_add_students, container, false);
        takenImageForStudent = addStudentsView.findViewById(R.id.takenImageForStudent);
        studentNameEt = addStudentsView.findViewById(R.id.name);
        studentRegNoEt = addStudentsView.findViewById(R.id.regNo);
        addStudentBtn = addStudentsView.findViewById(R.id.addStudent);
        takenImageForStudent.setOnClickListener(view -> {
            PickImageDialog.build(new PickSetup())
                    .setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult r) {
                            bitmap = r.getBitmap();
                            imgUri = r.getUri();
                            takenImageForStudent.setImageBitmap(bitmap);
                            imageTaken = true;
                            if (bitmap == null) {
                                takenImageForStudent.setImageDrawable(getResources().getDrawable(R.drawable.circle_icon));
                                imageTaken = false;
                            } else {
                                (addStudentsView.findViewById(R.id.rotate)).setVisibility(View.VISIBLE);
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

        addStudentBtn.setOnClickListener(v -> {
            studentName = studentNameEt.getText().toString();
            regNo = studentRegNoEt.getText().toString();
            if (!imageTaken) {
                Toast.makeText(getContext(), "Select an image for the student", Toast.LENGTH_SHORT).show();
            } else if (studentName.equals("")) {
                studentNameEt.setError("Please enter a Student Name");
                studentNameEt.requestFocus();
            } else if (regNo.equals("") || alreadyAddedStudents.contains(regNo)) {
                studentRegNoEt.setError("Please enter unique Registration Number");
                studentRegNoEt.requestFocus();
            } else if (alreadyAddedStudents.size() == CurrentUserAPI.getInstance().getUserNoOfStudents()) {
                Toast.makeText(context, "All the students are added", Toast.LENGTH_SHORT).show();
            } else {

                new AddPersonTask().execute(CurrentUserAPI.getInstance().getCurrentUserUid().toLowerCase(), studentName, regNo);
            }

        });

        return addStudentsView;
    }

    public void setHomeActionListener(Context context) {
        this.homeActionListener = (HomeActionListener) context;
    }


    @Override
    public void onGalleryClick() {

    }

    @Override
    public void onCameraClick() {


    }

    class AddPersonTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
            try {
                publishProgress("Syncing with server to add person...");
                Log.v("", "Request: Creating Person in person group" + params[0]);

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0], //personGroupID
                        params[1], //name
                        params[2]); //userData or regNo

                return createPersonResult.personId.toString();


            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.v("", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String personId) {

            if (personId != null) {
                Log.v("", "Response: Success. Person " + personId + " created.");

                //Toast.makeText(AddStudent.this, "Person with personId "+personId+" successfully created", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Student was successfully created", Toast.LENGTH_SHORT).show();


                new AddFaceTask().execute(personId);
            }
        }
    }

    private class AddFaceTask extends AsyncTask<String, String, String> {

        InputStream imageInputStream;

        @Override
        protected String doInBackground(String... params) {
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));

            try {
                Log.v("", "Adding face...");
                UUID personId = UUID.fromString(params[0]);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageInputStream = new ByteArrayInputStream(stream.toByteArray());
                AddPersistedFaceResult result = faceServiceClient.addPersonFaceInLargePersonGroup(
                        CurrentUserAPI.getInstance().getCurrentUserUid().toLowerCase(),
                        personId,
                        imageInputStream,
                        "",
                        null);

                return personId.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: " + e.getMessage());
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {

            faceReference = firebaseStorage.getReference().child(s);
            uploadTask = (UploadTask) faceReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri url = uriTask.getResult();
                    Log.d(TAG, "onSuccess: " + url.toString());
                    String imgUrl = url.toString();
                    HashMap<String, String> studentData = new HashMap<>();
                    studentData.put("name", studentName);
                    studentData.put("regNo", regNo);
                    studentData.put("personId", s);
                    studentData.put("imageUrl", imgUrl);
                    addStudentCollection.document(s).set(studentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            homeActionListener.studentsListClicked();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });

        }
    }

}