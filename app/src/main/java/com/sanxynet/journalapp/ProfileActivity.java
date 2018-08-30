package com.sanxynet.journalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    FirebaseAuth.AuthStateListener authListener;
    FirebaseAuth auth;
    //get current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Snackbar mSnackbar;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        layout = findViewById(R.id.layout);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangeEmail = findViewById(R.id.change_email_button);
        btnChangePassword = findViewById(R.id.change_password_button);
        btnSendResetEmail = findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = findViewById(R.id.remove_user_button);
        changeEmail = findViewById(R.id.change_email);
        changePassword = findViewById(R.id.change_password);
        sendEmail = findViewById(R.id.send);
        remove = findViewById(R.id.remove);
        signOut = findViewById(R.id.sign_out);

        oldEmail = findViewById(R.id.old_email);
        newEmail = findViewById(R.id.new_email);
        password = findViewById(R.id.password);
        newPassword = findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        findViewById(R.id.change_email_button).setOnClickListener(this);
        findViewById(R.id.change_password_button).setOnClickListener(this);
        findViewById(R.id.sending_pass_reset_button).setOnClickListener(this);
        findViewById(R.id.remove_user_button).setOnClickListener(this);
        findViewById(R.id.change_email).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        findViewById(R.id.sign_out).setOnClickListener(this);
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_email_button:
                changeEmailButton();
                break;

            case R.id.change_password_button:
                changePasswordButton();
                break;

            case R.id.sending_pass_reset_button:
                sendingPassResetEmail();
                break;

            case R.id.remove_user_button:
                initRemoveUser();
                break;

            case R.id.change_email:
                initChangeEmail();
                break;

            case R.id.change_password:
                initChangePassword();
                break;

            case R.id.send:
                initSendEmail();
                break;

            case R.id.sign_out:
                signOut();
                break;
        }
    }

    private void initRemoveUser() {
        progressBar.setVisibility(View.VISIBLE);
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showSnackbar(getString(R.string.profile_deleted) );
                                startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
                                finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                showSnackbar(getString(R.string.failed_to_delete_account) );
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

        private void initSendEmail() {
            progressBar.setVisibility(View.VISIBLE);
            if (!oldEmail.getText().toString().trim().equals("")) {
                auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showSnackbar(getString(R.string.reset_password_sent) );
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    showSnackbar(getString(R.string.failed_to_send_reset_email) );
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            } else {
                oldEmail.setError(getString(R.string.enter_email));
                progressBar.setVisibility(View.GONE);
            }
        }

        private void sendingPassResetEmail () {
            oldEmail.setVisibility(View.VISIBLE);
            newEmail.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.GONE);
            changeEmail.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
            sendEmail.setVisibility(View.VISIBLE);
            remove.setVisibility(View.GONE);
        }

        private void initChangePassword() {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null && !newPassword.getText().toString().trim().equals("")) {
                if (newPassword.getText().toString().trim().length() < 6) {
                    newPassword.setError(getString(R.string.password_is_too_short));
                    progressBar.setVisibility(View.GONE);
                } else {
                    user.updatePassword(newPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showSnackbar(getString(R.string.password_updated) );
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        showSnackbar(getString(R.string.failed_to_updated_password) );
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            } else if (newPassword.getText().toString().trim().equals("")) {
                newPassword.setError(getString(R.string.enter_password));
                progressBar.setVisibility(View.GONE);
            }
        }

        private void initChangeEmail() {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null && !newEmail.getText().toString().trim().equals("")) {
                user.updateEmail(newEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showSnackbar(getString(R.string.email_updated) );
                                    signOut();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    showSnackbar(getString(R.string.failed_to_update_email) );
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            } else if (newEmail.getText().toString().trim().equals("")) {
                newEmail.setError(getString(R.string.enter_email));
                progressBar.setVisibility(View.GONE);
            }
        }

        private void changePasswordButton() {
            oldEmail.setVisibility(View.GONE);
            newEmail.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.VISIBLE);
            changeEmail.setVisibility(View.GONE);
            changePassword.setVisibility(View.VISIBLE);
            sendEmail.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        }

        private void changeEmailButton() {
            oldEmail.setVisibility(View.GONE);
            newEmail.setVisibility(View.VISIBLE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.GONE);
            changeEmail.setVisibility(View.VISIBLE);
            changePassword.setVisibility(View.GONE);
            sendEmail.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
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

