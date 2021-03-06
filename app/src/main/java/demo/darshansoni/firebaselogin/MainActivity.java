package demo.darshansoni.firebaselogin;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN = 100 ;
    private final String TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton mGoogle, mFb, mTweeter;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private TwitterAuthClient mTwitterAuthClient;
    private View lineView;
    private ProgressBar progressBar;
    private MaterialButton mLogin, mSignupNavigation, mForgotPassword;
    private TextInputEditText mEmail, mPassword;
    private String email, password, error="Required";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        initUi();
        initTwitter();
        setUpClickListener();

    }

    private void setUpClickListener() {
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        mGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                googleSignIn();

            }
        });

        mFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLogin(v);
            }
        });

        mTweeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweeterLogin();
            }
        });

        mSignupNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });
    }

    private void loginUser() {
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        if(email.equals("")){
            mEmail.setError(error);
        }
        else if(password.equals("")){
            mPassword.setError(error);
        }
        else {
            decideProgressVisibility(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            decideProgressVisibility(false);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                startDashboardActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed: "+task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void initTwitter() {
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.CONSUMER_KEY), getString(R.string.CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    private void initUi() {
        lineView = findViewById(R.id.lineView);
        progressBar = findViewById(R.id.pbProcessing);
        mLogin = findViewById(R.id.login_button);
        mSignupNavigation = findViewById(R.id.signupNavigation);
        mGoogle = findViewById(R.id.googleLogin);
        mFb = findViewById(R.id.fbLogin);
        mTweeter = findViewById(R.id.tweeterLogin);
        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);
        mForgotPassword = findViewById(R.id.forgot_password);
    }


    /* methods for handling login with google */
    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(MainActivity.this,"Sign in success",Toast.LENGTH_SHORT).show();
                            startDashboardActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this,"Sign in failed:"+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }
    /* methods for handling login with google end */


    /* methods for handling login with facebook */
    public void fbLogin(View view)
    {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList( "email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel()
                    {
                        // App code
                        Toast.makeText(MainActivity.this,"Facebook login failed",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        // App code
                        Toast.makeText(MainActivity.this,"Facebook login failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            startDashboardActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed:"+task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    /* methods for handling login with facebook end*/


    /* methods for handling login with twitter */
    private void tweeterLogin(){
        mTwitterAuthClient = new TwitterAuthClient();
        mTwitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                // Success
                handleTwitterSession(twitterSessionResult.data);
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });


    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            startDashboardActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed:"+task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    /* methods for handling login with twitter end*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(MainActivity.this,"Sign in failed:"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
        if(mTwitterAuthClient!=null){
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startDashboardActivity(){
        startActivity(new Intent(MainActivity.this,DashboardActivity.class));
        finish();
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
