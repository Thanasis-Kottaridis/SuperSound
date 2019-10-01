package com.unipi.kottarido.supersound.supersound;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    //Log TAGS
    private static final String SIGN_IN_SUCCEED = "Succeed Sign in";
    public static final String SIGN_IN_FAILED = "Failed Sign in";

    //orizw to instance tou FirebaseAuth
    private FirebaseAuth mAuth;

    private EditText EmailText;
    private EditText PasswordText;
    private Button LogInButton;
    private TextView CreateAccount;
    private TextView ForgotPassword;
    private ImageView FacebookImage;
    private ImageView GoogleImage;
    private ImageView InstagramImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //initialize to instance tis FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //vriskei ta views apo ta id tous
        EmailText = findViewById(R.id.EmailText_LogIn);
        PasswordText = findViewById(R.id.PasswordText_LogIn);
        LogInButton = findViewById(R.id.LogInButton_LogIn);
        CreateAccount = findViewById(R.id.CreateAccount_LogIn);
        ForgotPassword = findViewById(R.id.ForgotPassword_LogIn);
        FacebookImage = findViewById(R.id.FacebookImage_LogIn);
        GoogleImage = findViewById(R.id.GoogleImage_LogIn);
        InstagramImage = findViewById(R.id.InstagramImage_LogIn);

        //elenxos an o user einai eidi sindedemenos diladi currentuser != null
        if(mAuth.getCurrentUser() != null){
            //tote paei sto mainActivity;
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        //sto patima tou koumpiou log in
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailText.getText().toString();
                String password = PasswordText.getText().toString();

                //kalei tin methodo gia SignIn
                SignInMethod(email , password);
            }
        });


        //sto onClick tou Create Account
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
    }

    private void SignInMethod(String email, String password){

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(SIGN_IN_SUCCEED,"SignInWithEmail:succeed");
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(SIGN_IN_FAILED, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
