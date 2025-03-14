package holyhack.eldermind;

import android.util.Log;
import android.widget.Toast;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.util.Scanner;

import holyhack.eldermind.DatabaseHandling.feedReaderDbHelper;

public class LoginPage extends AppCompatActivity {

    private Button btnLogin;

    private Button btnCreateAccount;
    private Button btnGoBack;
    private Button btnForgotPassword;
    private TextView userEmail;
    private TextView password;

    private feedReaderDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Initialize UI elements
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnForgotPassword = findViewById(R.id.btnForgottenPassword);
        userEmail = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        db = new feedReaderDbHelper(this);

    }
    public void onBtnLogin(View caller) {
        String hashCodePassword = Integer.toString(password.getText().toString().hashCode());
        String email = userEmail.getText().toString();
        Intent intent;
        if (db.checkUser(hashCodePassword, email)) {
            Toast.makeText(LoginPage.this, "Login Success", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, HomePage.class);
            String name = db.getUsername(email);
            db.close();
            Bundle extras = new Bundle();
            extras.putString("userName" , name);
            intent.putExtras(extras);
        } else {
            Toast.makeText(LoginPage.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, LoginPage.class);
        }
        startActivity(intent);
    }



    public void onBtnCreateAccount(View caller) {
        Intent intent = new Intent(this, CreateAccountPage.class);
        startActivity(intent);
    }


    public void onBtnForgottenPassword(View caller) {
        Intent intent = new Intent(this, ForgottenPasswordPage.class);
        startActivity(intent);
    }
}
