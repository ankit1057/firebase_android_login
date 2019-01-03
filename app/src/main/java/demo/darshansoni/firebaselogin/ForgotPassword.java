package demo.darshansoni.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText mEmail;
    private MaterialButton mSendLink;
    private ProgressBar progressBar;
    private View lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmail = findViewById(R.id.emailText);
        mSendLink = findViewById(R.id.reset_pw_button);
        progressBar = findViewById(R.id.forgotPwProgress);
        lineView = findViewById(R.id.lineView);

        mSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideProgressVisibility(true);
                FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                decideProgressVisibility(false);
                                if (task.isSuccessful()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                                    builder.setMessage("Password reset link has been sent to your email address");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                                            finish();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                                else {
                                    Toast.makeText(ForgotPassword.this,"Failed to sent a reset password email:"+task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void decideProgressVisibility(boolean flag){
        if(flag){
            progressBar.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.INVISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            lineView.setVisibility(View.VISIBLE);
        }
    }
}
