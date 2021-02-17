package com.redsponge.gltest.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.redsponge.gltest.R;

import java.util.Objects;

public class AuthLoginFragment extends Fragment {

    private final FirebaseAuth auth;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    private Animation shakeAnimation;

    public AuthLoginFragment() {
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_error_shake);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener((v) -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        ((AuthActivity) Objects.requireNonNull(getActivity())).tryLogIn();
                    }
                    else {
                        String emailError = null;
                        String passwordError = null;

                        if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                            emailError = "There is no user with this email!";
                        }
                        else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            String errorCode = ((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_INVALID_EMAIL": {
                                    emailError = "Invalid email address!";
                                } break;
                                default: {
                                    Log.e("AuthLoginFragment", "Not handled error code on login fail: " + errorCode);
                                } break;
                            }
                        }

                        if(emailError != null) {
                            etEmail.setError(emailError);
                            etEmail.startAnimation(shakeAnimation);
                        }
                        if(passwordError != null) {
                            etPassword.setError(passwordError);
                            etPassword.startAnimation(shakeAnimation);
                        }
                    }
                }
            });

        });
    }
}
