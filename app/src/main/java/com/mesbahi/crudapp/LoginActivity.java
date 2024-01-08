package com.mesbahi.crudapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Optional;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button btnlogin;
    Button gotosignin;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email1);
        password = (EditText) findViewById(R.id.password1);
        btnlogin = (Button) findViewById(R.id.btnsignin);
        DB = new DBHelper(this);
        gotosignin = (Button) findViewById(R.id.btnsignup1);
        btnlogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String emailValue = email.getText().toString();
                                            String pass = password.getText().toString();

                                            if( email.equals("") || pass.equals("") ){
                                                Toast.makeText(LoginActivity.this, "Veuillez saisir toutes les informations.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                try {
                                                    if (DB.checkUserCredentials(emailValue, pass)) {
                                                        Optional<User> userOptional = DB.getUserByEmailAndPassword(emailValue, pass);
                                                        if (userOptional.isPresent()) {
                                                            User user = userOptional.get();
                                                            // Store the logged-in user in the session
                                                            SessionManager.getInstance().loginUser(user);
                                                            // Now we have the logged user data
                                                            Toast.makeText(LoginActivity.this, "Connexion réussie.", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Connexion échoué.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else{
                                                        Toast.makeText(LoginActivity.this, "Identifiants invalides.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        }
                                    }
        );

        gotosignin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


}