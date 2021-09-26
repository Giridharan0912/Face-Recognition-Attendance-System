package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AttendanceFragment extends Fragment {
    private static final String TAG = "AttendanceFragment";
    private View AttendanceView;
    private HomeActionListener homeActionListener;
    private AppCompatButton takeAttendanceBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Subject subject;
    private RecyclerView attendanceHistoryRv;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private ArrayList<String> dateArrayList;
    private AttendanceHistoryAdapter attendanceHistoryAdapter;
    private Context context;

    public AttendanceFragment(Subject subject,Context context) {
        this.subject = subject;
        this.context=context;
        collectionReference = firestore.collection("users")
                .document(CurrentUserAPI.getInstance().getCurrentUserUid()).collection("subjects").document(subject.getSubjectId())
                .collection("attendance");
    }

    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Completable.fromAction(this::getHistory).observeOn(Schedulers.io()).subscribeOn(Schedulers.single())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AttendanceView = inflater.inflate(R.layout.fragment_attendance, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        Toolbar toolbar = AttendanceView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        takeAttendanceBtn = AttendanceView.findViewById(R.id.takeAttendanceBtn);
        attendanceHistoryRv = AttendanceView.findViewById(R.id.attendance_history_rv);
        takeAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                homeActionListener.takeAttendanceClicked(date, subject);
            }
        });

        return AttendanceView;
    }


    public void setHomeActionListener(Context context) {
        this.homeActionListener = (HomeActionListener) context;
    }

    private void getHistory() {
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    dateArrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        dateArrayList.add(snapshot.getId());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.AHViewHolder> {
        private LayoutInflater layoutInflater;
        private Context context;
        private List<String> dates;

        public AttendanceHistoryAdapter(Context context, List<String> dates) {
            this.layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.dates = dates;
        }

        @NonNull
        @Override
        public AttendanceHistoryAdapter.AHViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_date, parent, false);
            return new AHViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AttendanceHistoryAdapter.AHViewHolder holder, int position) {
            String current = dates.get(position);
            holder.textView.setText(current);
        }

        @Override
        public int getItemCount() {
            if (dates != null)
                return dates.size();
            else return 0;
        }

        public class AHViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CardView cardView;
            private TextView textView;

            public AHViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.date_cardView);
                textView = itemView.findViewById(R.id.dateTv);
                cardView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.date_cardView) {
                    homeActionListener.goToAttendanceHistoryItem(dates.get(getAdapterPosition()),subject);
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                attendanceHistoryAdapter = new AttendanceHistoryAdapter(context, dateArrayList);
                attendanceHistoryRv.setAdapter(attendanceHistoryAdapter);
                attendanceHistoryRv.setLayoutManager(new LinearLayoutManager(context));
            }
        }, 2000);
    }
}