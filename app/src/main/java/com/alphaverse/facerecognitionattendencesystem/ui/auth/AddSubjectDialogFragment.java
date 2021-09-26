package com.alphaverse.facerecognitionattendencesystem.ui.auth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;

import static com.alphaverse.facerecognitionattendencesystem.ui.auth.CreateAccountFragment.REQ_CODE;

public class AddSubjectDialogFragment extends DialogFragment {
    private final String TAG = getClass().getSimpleName();
    private View view;
    private AddSubjectInterface subjectInterface;
    private EditText subjectId, subjectName, subjectStaff, subjectTotalClass;
    private AppCompatButton createSubjectSaveBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            subjectInterface = (AddSubjectInterface) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialog_subject, container, false);
        subjectId = view.findViewById(R.id.et_course_id);
        subjectName = view.findViewById(R.id.et_course_name);
        subjectStaff = view.findViewById(R.id.et_staff_name);
        subjectTotalClass = view.findViewById(R.id.et_total_classes);
        createSubjectSaveBtn = view.findViewById(R.id.save_subject_btn);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        switch (getTargetRequestCode()) {
            case REQ_CODE:
                createSubjectSaveBtn.setOnClickListener(v -> {
                    Subject subject = new Subject();
                    subject.setSubjectId(subjectId.getText().toString());
                    subject.setSubjectName(subjectName.getText().toString());
                    subject.setSubjectStaff(subjectStaff.getText().toString());
                    subject.setTotalClasses(subjectTotalClass.getText().toString());
                    subjectInterface.subjectDialog(true, subject);
                    getDialog().dismiss();
                });

                break;
        }
        return view;
    }


    public interface AddSubjectInterface {
        void subjectDialog(boolean b, Subject subject);
    }

}
