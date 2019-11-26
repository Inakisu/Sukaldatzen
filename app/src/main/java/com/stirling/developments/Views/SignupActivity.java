package com.stirling.developments.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.stirling.developments.R;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String TAG = "Tag verificación Email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.nombreReg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Introduce una dirección de correo",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Introduce una contraseña"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Introduzca una contraseña de " +
                            "mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new
                                OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this,
                                        "createUserWithEmail:onComplete:" + task.isSuccessful(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) { //En caso de error o correo existente
                                    Toast.makeText(SignupActivity.this,
                                            "Authentication"+" failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.i("Response","Failed to create user: "
                                            +task.getException().getMessage());

                                } else {
                                    //I: Registro correcto --> enviar email verificación
                           /*         user = auth.getCurrentUser();
                                    enviarVerif(); //Llamada a método para enviar email verificación*/
                                    /*if(!user.isEmailVerified()) {//I: revisar este if
                                        Toast.makeText(SignupActivity.this,
                                                "Verifique el correo", Toast.LENGTH_SHORT).show();
                                        auth.getInstance().signOut();
                                        startActivity(new Intent(SignupActivity.this,
                                                SignupActivity.class));
                                       // finish();
                                    }else{
                                        startActivity(new Intent(SignupActivity.this,
                                                MainUserActivity.class));
                                        finish();
                                    }*/
                                }
                            }
                        });

            }

        });
    }
    /*private void enviarVerif(){ //método para enviar email de verificación
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Email de verificación enviado a: "
                            + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.d("Verificación","Email de verificación enviado a: "+
                            user.getEmail());
                }else{
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(getApplicationContext(), "Fallo al enviar email verificación",
                            +Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}