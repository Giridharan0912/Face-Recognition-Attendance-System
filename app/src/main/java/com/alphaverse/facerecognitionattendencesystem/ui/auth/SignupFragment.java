package com.alphaverse.facerecognitionattendencesystem.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alphaverse.facerecognitionattendencesystem.R;

import java.util.ArrayList;


public class SignupFragment extends Fragment {
    private EditText etSignUpName, etSignUpClassSection, etSignUpYear, etSignUpEmail, etSignUpNoOfStudents, etSignUpPassword, etSignUpConfirmPassword, etSignUpTotalClass;
    private TextView tvLogin;
    private Button btnNext;
    private View signUpView;
    private ActionListener actionListener;
    private final String TAG = getClass().getSimpleName();


    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        signUpView = inflater.inflate(R.layout.fragment_signup, container, false);
        etSignUpName = signUpView.findViewById(R.id.et_signup_name);
        etSignUpEmail = signUpView.findViewById(R.id.et_signup_email);
        etSignUpClassSection = signUpView.findViewById(R.id.et_signup_class_section);
        etSignUpYear = signUpView.findViewById(R.id.et_signup_year);
        etSignUpPassword = signUpView.findViewById(R.id.et_signup_password);
        etSignUpConfirmPassword = signUpView.findViewById(R.id.et_signup_confirm_password);
        etSignUpNoOfStudents = signUpView.findViewById(R.id.et_signup_students);
        tvLogin = signUpView.findViewById(R.id.tv_loginIn);
        btnNext = signUpView.findViewById(R.id.btn_next);
        etSignUpTotalClass = signUpView.findViewById(R.id.et_signup_totalClass);
        tvLogin.setOnClickListener(v -> {
            actionListener.onLoginClicked();
        });
        btnNext.setOnClickListener(v -> {
            nextPage();
        });
        return signUpView;
    }

    private void nextPage() {
        String email = etSignUpEmail.getText().toString().trim();
        String password = etSignUpPassword.getText().toString().trim();
        String confirmPassword = etSignUpConfirmPassword.getText().toString().trim();
        String name = etSignUpName.getText().toString().trim();
        String classSection = etSignUpClassSection.getText().toString().trim();
        int year = Integer.parseInt(etSignUpYear.getText().toString().trim());
        int students =Integer.parseInt(etSignUpNoOfStudents.getText().toString().trim());
        int classes=Integer.parseInt(etSignUpTotalClass.getText().toString().trim());

        if (email.isEmpty()) {
            etSignUpEmail.setError("Please enter an Email Address");
            etSignUpEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etSignUpPassword.setError("Please enter an Email Address");
            etSignUpPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            etSignUpConfirmPassword.setError("Please enter an Email Address");
            etSignUpConfirmPassword.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            etSignUpName.setError("Please enter an Email Address");
            etSignUpName.requestFocus();
            return;
        }
        if (classSection.isEmpty()) {
            etSignUpClassSection.setError("Please enter an Email Address");
            etSignUpClassSection.requestFocus();
            return;
        }


        if (password.equals(confirmPassword)) {
            Bundle bundle=new Bundle();
            bundle.putString("userName",name);
            bundle.putString("userEmail",email);
            bundle.putString("userClassSection",classSection);
            bundle.putInt("userYear",year);
            bundle.putInt("userNoOfStudents",students);
            bundle.putInt("userClasses",classes);
            bundle.putString("userPassword",password);
                actionListener.onSignUpNextClicked(bundle);
        }

    }

    public void setActionListener(Context context) {
        this.actionListener = (ActionListener) context;
    }


}