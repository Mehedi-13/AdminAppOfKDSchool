package com.example.adminsofkdschool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText adminEmail, password;
    private TextView textView;
    private RelativeLayout loginBtn;

    private String  email,pass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedPreferences =this.getSharedPreferences("login",MODE_PRIVATE);
        editor= sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin","false").equals("yes")){
            openDash();
        }

        adminEmail = findViewById(R.id.adminMail);
        password = findViewById(R.id.password);
        textView = findViewById(R.id.show);
        loginBtn = findViewById(R.id.loginBtn);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getInputType()==144){
                    password.setInputType(129);
                    textView.setText("Hide");
                }else {
                    password.setInputType(144);
                    textView.setText("Show");
                }

                password.setSelection(password.getText().length());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void validateData() {
        email= adminEmail.getText().toString();
        pass= password.getText().toString();

        if (email.isEmpty()){
            adminEmail.setError("Required");
            adminEmail.requestFocus();
        }else if (pass.isEmpty()){
            password.setError("Required");
            password.requestFocus();

        }else if (email.equals("admin'sofkd@gmail.com") && pass.equals("12Admin90")){
            editor.putString("isLogin","yes");
            editor.commit();
            openDash();
        }else {
            Toast.makeText(this, "Please check Email and Password again!", Toast.LENGTH_LONG).show();
        }

    }

    private void openDash() {


        startActivity(new Intent(LoginActivity.this,Dashboard.class));
        finish();

    }
}