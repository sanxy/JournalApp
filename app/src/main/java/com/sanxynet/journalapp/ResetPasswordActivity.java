package com.sanxynet.journalapp;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    EditText inputEmail;
    Button buttonReset;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Snackbar mSnackbar;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = findViewById(R.id.email);
        buttonReset = findViewById(R.id.reset_password_button);
        progressBar = findViewById(R.id.progressBar);
        layout = findViewById(R.id.layout);

        findViewById(R.id.reset_password_button).setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_password_button:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String email = inputEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showSnackbar( getString(R.string.enter_registered_email));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showSnackbar( getString(R.string.instructions_sent_to_email));
                        } else {
                            showSnackbar( getString(R.string.failed_to_send_reset_email));
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void showSnackbar(String text) {
        mSnackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        mSnackbar.setAction(R.string.action_close, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnackbar.dismiss();
            }
        });
        mSnackbar.show();
    }
}
