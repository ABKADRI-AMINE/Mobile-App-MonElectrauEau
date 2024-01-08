package com.mesbahi.crudapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText lname,fname,password,repassword,phone,email;
    Button signup, signin;

    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        lname = (EditText) findViewById(R.id.lname);
        fname = (EditText) findViewById(R.id.fname);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.btnsignup);
        signin = (Button) findViewById(R.id.btnsignin);
        DB = new DBHelper(this);
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                String emailvalue = email.getText().toString();
                String numTel = phone.getText().toString();

                if(firstname.equals("") || lastname.equals("") || pass.equals("") || repass.equals("") || emailvalue.equals("")){
                    Toast.makeText(RegisterActivity.this,"Veuillez remplir tous les champs.",Toast.LENGTH_SHORT).show();
                }else{
                    if(pass.equals(repass)){
                        if(DB.emailUnique(emailvalue)){
                            User user = new User(firstname,lastname,pass,emailvalue,numTel);
                            boolean userSaved = DB.save(user);
                            if(userSaved){
                                Toast.makeText(RegisterActivity.this,"Inscription réussie ! Veuillez vous connecter.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);

                            }else{
                                Toast.makeText(RegisterActivity.this,"Erreur lors de l'enregistrement des données.",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this,"Adresse e-mail déjà existante.",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this,"Les mots de passe ne correspondent pas",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}