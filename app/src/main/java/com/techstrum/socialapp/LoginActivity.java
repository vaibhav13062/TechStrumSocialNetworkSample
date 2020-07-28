package com.techstrum.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailText_login;
    EditText passwordText_login;
    TextView notReg;
    Button loginButton;
    FirebaseAuth fAuth;
    TextView forgotPassword;
    ProgressBar progressBar_login;
    ImageButton google_button;
    //String idToken,google_name,google_email;
    //private GoogleApiClient googleApiClient;
    //private static final String TAG = "MainActivity";
   // private static final int RC_SIGN_IN = 1;
   // private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText_login = findViewById(R.id.emailText_login);
        passwordText_login = findViewById(R.id.passwordText_login);
        loginButton = findViewById(R.id.loginButton);
        notReg = findViewById(R.id.notReg);
        forgotPassword=findViewById(R.id.forgotPassword);
        progressBar_login=findViewById(R.id.progressBar_login);
        fAuth=FirebaseAuth.getInstance();

        notReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        //GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               // .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
               // .requestEmail()
              //  .build();
       // googleApiClient=new GoogleApiClient.Builder(this)
             //  .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
             //   .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
             //   .build();
        google_button=findViewById(R.id.google_button);
      //  google_button.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View v) {
       //         Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
       //         startActivityForResult(intent,RC_SIGN_IN);
      //      }
      //  });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                progressBar_login.setVisibility(View.VISIBLE);
                String email1 = emailText_login.getText().toString();
                String password1 = passwordText_login.getText().toString();
                if (TextUtils.isEmpty(email1)) {
                    emailText_login.setError("Email Is Required");
                    return;
                }
                if (TextUtils.isEmpty(password1)) {
                    passwordText_login.setError("Password Is Required");
                    return;
                }
                if (password1.length() < 6 ) {
                    passwordText_login.setError("Password Must Be <=6");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar_login.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();


                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{

                            Toast.makeText(LoginActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail=new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog =new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter Your Email To Recieve Reset Link");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //yes

                        String mail= resetMail.getText().toString();
                        if(!mail.isEmpty()) {
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Reset Link Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "ERROR! Reset Link Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{

                        }
                    }
                });
                passwordResetDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no
                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            google_name = account.getDisplayName();
            google_email = account.getEmail();
            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        }else{
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. "+result);
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }
    private void firebaseAuthWithGoogle(AuthCredential credential){

        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        }else{
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }*/
}
