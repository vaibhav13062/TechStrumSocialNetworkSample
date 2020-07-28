package com.techstrum.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText nameText;
    EditText emailText;
    EditText phoneText;
    EditText passwordText;
    EditText retypePassword;
    TextView alreadyReg;
    Button regButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar_reg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameText=findViewById(R.id.nameText);
        phoneText=findViewById(R.id.phoneText);
        emailText=findViewById(R.id.emailText);
        passwordText=findViewById(R.id.passwordText);
        retypePassword=findViewById(R.id.retypePassword);
        alreadyReg=findViewById(R.id.alreadyReg);
        regButton=findViewById(R.id.regButton);
        fAuth = FirebaseAuth.getInstance();
        progressBar_reg=findViewById(R.id.progressBar_reg);
        regButton.setClickable(true);


        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        alreadyReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressBar_reg.setVisibility(View.VISIBLE);
                String email =emailText.getText().toString();
                String password=passwordText.getText().toString();
                if(TextUtils.isEmpty(email)){
                    emailText.setError("Email Is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    passwordText.setError("Password Is Required");
                    return;
                }
                if (password.length()<6){
                    passwordText.setError("Password Must Be <=6");
                    return;
                }
                if(!password.equals(retypePassword.getText().toString())){
                    retypePassword.setError("Password Do Not Match");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        regButton.setClickable(false);
                        if(task.isSuccessful()){
                            User user=new User(nameText.getText().toString(),emailText.getText().toString(),phoneText.getText().toString(),null,null,null,null,null,null,0);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                            //send Verification Link
                                        FirebaseUser fUser=fAuth.getCurrentUser();
                                        fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "Verification Link Sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "Verification Link Not Sent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "NOT SAVED TO DATABASE", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Toast.makeText(RegisterActivity.this, "Add Your Information", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),UpdateActivity.class));
                            finish();
                            progressBar_reg.setVisibility(View.INVISIBLE);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}
