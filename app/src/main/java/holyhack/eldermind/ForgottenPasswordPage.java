package holyhack.eldermind;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import holyhack.eldermind.DatabaseHandling.FeedReaderDbHelper;

public class ForgottenPasswordPage extends AppCompatActivity {

    private Button btnNext;
    private TextView email;
    private TextView name;
    private TextView birthdate;
    private TextView postal;
    private TextView password;
    private Button dateButton;
    private FeedReaderDbHelper db;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_page);

        // Initialize UI elements
        btnNext = findViewById(R.id.btnChangePassword);
        email = findViewById(R.id.emailForgotten);
        name = findViewById(R.id.nameForgotten);
        birthdate = findViewById(R.id.dateForgotten);
        postal = findViewById(R.id.postalCodeForgotten);
        password = findViewById(R.id.newPassword);
        dateButton = findViewById(R.id.dateButtonForgotten);
        db = new FeedReaderDbHelper(this);

        // Date picker button click listener
        dateButton.setOnClickListener(v -> openDialog());

        // Get current date for DatePicker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) ->
                birthdate.setText(day + "/" + (month + 1) + "/" + year), year, month, day);
        dialog.show();
    }

    public void onBtnChangePassword(View caller) {
        String username = name.getText().toString();
        String emailUser = email.getText().toString();
        int passwordCode = password.getText().toString().hashCode();
        String birthday = birthdate.getText().toString();
        String address = postal.getText().toString();

        // Check identity using MySQL before changing the password
        db.checkIdentity(username, emailUser, new FeedReaderDbHelper.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                // If identity is verified, change the password
                db.changePassword(username, passwordCode, new FeedReaderDbHelper.VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(ForgottenPasswordPage.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgottenPasswordPage.this, LoginPage.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ForgottenPasswordPage.this, "Error changing password", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ForgottenPasswordPage.this, "Please provide the correct user information", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
