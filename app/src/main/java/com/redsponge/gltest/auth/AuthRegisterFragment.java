package com.redsponge.gltest.auth;

import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.redsponge.gltest.R;
import com.redsponge.gltest.utils.Utils;

import io.opencensus.internal.StringUtils;

public class AuthRegisterFragment extends Fragment {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    private Animation shakeAnimation;

    private final FirebaseAuth auth;

    public AuthRegisterFragment() {
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_error_shake);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this::tryRegister);
        // set stuff for view
    }

    private void tryRegister(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if(Utils.isBlankOrNull(email)) {
            showError(etEmail, "Email cannot be empty!");
            return;
        }
        if(Utils.isBlankOrNull(password)) {
            showError(etPassword, "Password cannot be empty!");
            return;
        }
        if(Utils.isBlankOrNull(confirmPassword)) {
            showError(etConfirmPassword, "Password confirmation cannot be empty!");
            return;
        }

        if(!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match!");
            etConfirmPassword.startAnimation(shakeAnimation);
            return;
        }
    }

    private void showError(EditText et, String error) {
        et.setError(error);
        et.startAnimation(shakeAnimation);
    }
}
