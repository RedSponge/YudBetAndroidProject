package com.redsponge.carddeck.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.redsponge.carddeck.R;
import com.redsponge.carddeck.card.Constants;
import com.redsponge.carddeck.utils.Utils;

import java.util.Objects;
import java.util.Optional;

public class AuthRegisterFragment extends Fragment {

    private EditText etEmail, etPassword, etConfirmPassword, etName;
    private Button btnRegister;

    private Animation shakeAnimation;

    private final FirebaseAuth auth;
    private final FirebaseDatabase db;

    public AuthRegisterFragment() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
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
        etName = view.findViewById(R.id.etName);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this::tryRegister);
        // set stuff for view
    }

    private void tryRegister(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String name = etName.getText().toString();

        if(Utils.isBlankOrNull(email)) {
            showError(etEmail, "Email cannot be empty!");
            return;
        }
        if(Utils.isBlankOrNull(name)) {
            showError(etName, "Name cannot be empty!");
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

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Optional<String> emailError = Optional.empty();
                Optional<String> passwordError = Optional.empty();

                if(task.isSuccessful() && auth.getCurrentUser() != null) {
                    LoggedUser user = new LoggedUser(etName.getText().toString(), auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
                    db.getReference(Constants.USERS_REFERENCE).child(user.getUid()).setValue(user);
                    ((AuthActivity) Objects.requireNonNull(getActivity())).tryLogIn();
                } else {
                    final Exception e = task.getException();

                    if(e instanceof FirebaseAuthException) {
                        switch(((FirebaseAuthException) e).getErrorCode()) {
                            case "EMAIL_TAKEN":
                                emailError = Optional.of("The email is taken!");
                                break;
                            default:
                                Toast.makeText(getContext(), "Unhandled error: " + ((FirebaseAuthException) e).getErrorCode() + "!", Toast.LENGTH_LONG).show();
                                Log.e("AuthRegisterFragment", "Didn't handle error code " + ((FirebaseAuthException) e).getErrorCode() + "!");
                        }
                        emailError.ifPresent(s -> showError(etEmail, s));
                        passwordError.ifPresent(s -> showError(etPassword, s));
                    }
                }
            }
        });
    }

    private void showError(EditText et, String error) {
        et.setError(error);
        et.startAnimation(shakeAnimation);
    }
}
