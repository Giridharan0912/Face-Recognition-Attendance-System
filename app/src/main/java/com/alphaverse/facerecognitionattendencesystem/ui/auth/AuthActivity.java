package com.alphaverse.facerecognitionattendencesystem.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alphaverse.facerecognitionattendencesystem.R;

public class AuthActivity extends AppCompatActivity implements ActionListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        fragmentManager = getSupportFragmentManager();
        initLogin();

    }

    private void initLogin() {
        fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setActionListener(this);
        fragmentTransaction.replace(R.id.auth_frameLayout, loginFragment).commit();
    }

    private void initSignUp() {
        fragmentTransaction = fragmentManager.beginTransaction();
        SignupFragment signUpFragment = new SignupFragment();
        signUpFragment.setActionListener(this);
        fragmentTransaction.isAddToBackStackAllowed();
        fragmentTransaction.replace(R.id.auth_frameLayout, signUpFragment).commit();
    }


    @Override
    public void onSignUpNextClicked(Bundle bundle) {
        fragmentTransaction = fragmentManager.beginTransaction();
        CreateAccountFragment createAccountFragment = new CreateAccountFragment(bundle);
        fragmentTransaction.replace(R.id.auth_frameLayout, createAccountFragment).commit();
    }

    @Override
    public void onSignUpClicked() {
        initSignUp();
    }

    @Override
    public void onLoginClicked() {
        initLogin();
    }
}