package holyhack.eldermind;

import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import holyhack.eldermind.DatabaseHandling.FeedReaderDbHelper;

public class LoginPage extends AppCompatActivity {

    private Button btnLogin;
    private Button btnCreateAccount;
    private Button btnForgotPassword;
    private TextView userEmail;
    private TextView password;
    private FeedReaderDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize UI elements
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnForgotPassword = findViewById(R.id.btnForgottenPassword);
        userEmail = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        db = new FeedReaderDbHelper(this);

        // Set up click listeners
        btnLogin.setOnClickListener(this::onBtnLogin);
        btnCreateAccount.setOnClickListener(this::onBtnCreateAccount);
        btnForgotPassword.setOnClickListener(this::onBtnForgottenPassword);
    }

    public void onBtnLogin(View caller) {
        String hashCodePassword = Integer.toString(password.getText().toString().hashCode());
        String email = userEmail.getText().toString();

        // Call `checkUser` function with a callback
        db.checkUser(email, hashCodePassword, new FeedReaderDbHelper.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(LoginPage.this, "Login Success", Toast.LENGTH_SHORT).show();

                // Fetch the username after successful login
                db.getUsername(email, new FeedReaderDbHelper.VolleyCallback() {
                    @Override
                    public void onSuccess(String username) {
                        Intent intent = new Intent(LoginPage.this, HomePage.class);
                        Bundle extras = new Bundle();
                        extras.putString("userName", username);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginPage.this, "Error fetching username", Toast.LENGTH_SHORT).show();
                        Log.e("LoginPage", "Error fetching username: " + error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginPage.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                Log.e("LoginPage", "Login failed: " + error);
            }
        });
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
