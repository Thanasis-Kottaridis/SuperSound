package com.unipi.kottarido.supersound.supersound;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.oob.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    //Log TAGS
    private static final String SIGN_UP_SUCCEED = "Succeed Sign in";
    public static final String SIGN_UP_FAILED = "Failed Sign in";
    public static final String ACCOUNT_CREATED = "Account created";
    //orizw to instance tou FirebaseAuth
    FirebaseAuth mAuth;

    private EditText UsernameText;
    private EditText EmailText;
    private EditText PasswordText;
    private Button SignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialize to instace tis FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        UsernameText = findViewById(R.id.UsernameText_SignUp);
        EmailText = findViewById(R.id.EmailText_SignUp);
        PasswordText = findViewById(R.id.PasswordText_SignUp);
        SignUpButton = findViewById(R.id.SignUpButton_SignUp);

        //sto patima tou signUp button
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = UsernameText.getText().toString();
                String email = EmailText.getText().toString();
                String password = PasswordText.getText().toString();

                SignUpMethod(username, email, password);
            }
        });

    }

    //create account method
    private void SignUpMethod(final String username, String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(!task.isSuccessful()){
                           Log.d(SIGN_UP_FAILED,"Sign Up Failed "+task.getException());
                           Toast.makeText(getApplicationContext(),"Invalid Email Or Password", Toast.LENGTH_LONG).show();
                       }
                       else {
                           Log.d(SIGN_UP_SUCCEED,"Sing up Successful"+ task.isSuccessful());
                           updateUI(username);
                           Toast.makeText(getApplicationContext(),"Account created!",Toast.LENGTH_SHORT).show();
                           Log.d(ACCOUNT_CREATED,"Account Created");
                       }
                    }
                });
    }

    private void updateUI(String username){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d("TESTING", "Profile updated successful");
                            finish();
                        }
                    });
        }
    }
}
