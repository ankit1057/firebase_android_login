package demo.darshansoni.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    private View lineView;
    private TextInputEditText mEmail, mPassword, mConfirmPassword;
    private String email, password, confirmPassword, error="Required";
    private MaterialButton mSignup,mNaviagteLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        initUi();
        setClickListeners();
    }

    private void setClickListeners() {

        mNaviagteLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finish();
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmPassword.getText().toString();

                if(email.equals("")){
                    mEmail.setError(error);
                }
                else if(password.equals("")){
                    mPassword.setError(error);
                }
                else if(confirmPassword.equals("")){
                    mConfirmPassword.setError(error);
                }
                else if(!password.equals(confirmPassword)){
                    mConfirmPassword.setError("Password and Confirm Password are not same");
                }
                else {
                    signupUser();
                }
            }
        });
    }

    private void signupUser() {
        decideProgressVisibility(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        decideProgressVisibility(false);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Signup failed: "+task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Signup failed: "+e.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initUi() {

        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);
        mConfirmPassword = findViewById(R.id.confirmPasswordText);
        mSignup = findViewById(R.id.signup_button);
        mNaviagteLogin = findViewById(R.id.login_nagiation);
        lineView = findViewById(R.id.lineView);
        mProgress = findViewById(R.id.signUpProgress);
    }

    private void decideProgressVisibility(boolean flag){
        if(flag){
            mProgress.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgress.setVisibility(View.INVISIBLE);
            lineView.setVisibility(View.VISIBLE);
        }
    }
}
