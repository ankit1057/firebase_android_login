package demo.darshansoni.firebaselogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mDisplayName;
    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();

        mDisplayName = findViewById(R.id.displayName);
        mLogout = findViewById(R.id.logout);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                decideAction(mAuth.getCurrentUser());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        decideAction(currentUser);

    }

    private void decideAction(FirebaseUser currentUser){
        if(currentUser!=null){
            mDisplayName.setText(currentUser.getDisplayName());
        }
        else {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }

    }
}
