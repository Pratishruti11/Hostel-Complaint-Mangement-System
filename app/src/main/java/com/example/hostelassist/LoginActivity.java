package com.example.hostelassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    CheckBox rememberMe;
    PreferenceManager loginPref;
    String signInEmail;
    String signInPass;
    private ProgressDialog mProgressDialog;
    private ImageView login_logo;

    private static final String TAG = LoginActivity.class.getSimpleName();


    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private ConstraintLayout constraintLayout;

    private long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        initLogin();
        loginPref=new PreferenceManager(this);


        //Login button onClick
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                signInEmail = mEmail.getText().toString().trim();
                signInPass = mPassword.getText().toString().trim();


                if ((signInEmail == null) || signInEmail.equals("") || TextUtils.isEmpty(signInEmail) || (signInEmail.length() == 0)) {
                    Toast.makeText(getApplicationContext(), "Enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (signInPass.equals("") || (signInPass == null) || TextUtils.isEmpty(signInPass) || signInPass.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!validateEmail(signInEmail)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialog();

                (fAuth.signInWithEmailAndPassword(signInEmail, signInPass))
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    checkEmailVerification();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("error",e.getLocalizedMessage());
                                hideProgressDialog();

                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }


                        });

            }

        });

        //SignUp button onClick
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in we will close this activity and take the user to profile activity

        if (loginPref.isLoggedIn()) {
            Log.d("Current User","active");
            startActivity(new Intent(this, HomeActivity.class));
            Toast.makeText(getApplicationContext(),"Signed In as " + Objects.requireNonNull(fAuth.getCurrentUser()).getDisplayName(),Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public void initLogin(){

        login_logo=findViewById(R.id.login_logo);
        mEmail =findViewById(R.id.Email);
        mPassword =findViewById(R.id.password);
        rememberMe=findViewById(R.id.remember_me_chk_box);
        mLoginBtn=findViewById(R.id.login_Btn);
        mCreateBtn=findViewById(R.id.createAccount);
        mEmail.setText("");
        mPassword.setText("");
        rememberMe.setChecked(false);
        fAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef= firebaseDatabase.getReference();

    }

    //validateEmailMethod
    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }


    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        Boolean emailFlag = firebaseUser.isEmailVerified();


        if (emailFlag){
            if(rememberMe.isChecked()){
                loginPref.setIsLoggedIn(true);
                loginPref.setLoginCredentials(signInEmail,signInPass);
            }
            String testEmail=loginPref.getPrefEmail();
            Log.d("LoginEmail",testEmail);
            Toast.makeText(LoginActivity.this, " Signed In as " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            hideProgressDialog();
            finish();

        }else {
            hideProgressDialog();
            Toast.makeText(this, "Email verification pending", Toast.LENGTH_SHORT).show();
            fAuth.signOut();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            loginPref.setIsLoggedIn(true);
                            final FirebaseUser user = fAuth.getCurrentUser();
                            assert user != null;
                            String email = user.getEmail();
                            assert email != null;

                            boolean isNewUser;
                            if(task.getResult() != null)
                                isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            else
                                isNewUser = true;

                            if(isNewUser){

                                Log.d("FirstGoogleLogin","Reaching");
                                loginPref.setIsFirstGoogleLogin(false);
                                userRef = rootRef.getRef().child("Users").child(encodeUserEmail(user.getEmail())).getRef();
                                userRef.setValue(new UserClass(user.getEmail(), user.getDisplayName())).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, " Account failed .\n Try again.", Toast.LENGTH_SHORT).show();
                                        loginPref.setIsFirstGoogleLogin(false);
                                        hideProgressDialog();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loginPref.setIsFirstGoogleLogin(false);
                                        Toast.makeText(LoginActivity.this, " Signed In as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        hideProgressDialog();
                                        finish();
                                    }
                                });

                            }else {
                                userRef = rootRef.child("Users").child(encodeUserEmail(user.getEmail())).child("name");
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String name = (String) dataSnapshot.getValue();
                                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();
                                        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(LoginActivity.this, " Signed In as " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                hideProgressDialog();
                                                finish();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {


                                    }
                                });
                            }

                        } else {
                            // If sign in fails
                            hideProgressDialog();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
        } else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


    public void showProgressDialog() {
        if (isNetworkAvailable()){
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
                mProgressDialog.setMessage("Logging In ....");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCanceledOnTouchOutside(false);
            }

            mProgressDialog.show();
            new Handler().postDelayed(() -> {
                if (!isNetworkAvailable()){
                    hideProgressDialog();
                    Snackbar.make(constraintLayout,"No Internet Connection",Snackbar.LENGTH_LONG).show();
                }
            },15000);
        }else {
            Snackbar.make(constraintLayout,"No Internet Connection", Snackbar.LENGTH_LONG).show();
        }


    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}