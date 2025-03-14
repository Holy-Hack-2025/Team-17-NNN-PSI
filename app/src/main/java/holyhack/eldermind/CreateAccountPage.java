package holyhack.eldermind;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import holyhack.eldermind.DatabaseHandling.FeedReaderDbHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccountPage extends AppCompatActivity {
    private Button btnCreate;
    private Button btnBack;
    private TextView usernameCreate;
    private TextView emailCreate;
    private TextView passwordCreate;
    private TextView dateCreate;
    private TextView postalCodeCreate;
    private FeedReaderDbHelper db;
    private Button dateButton;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize UI elements
        btnCreate = findViewById(R.id.btnCreate);
        usernameCreate = findViewById(R.id.usernameCreate);
        emailCreate = findViewById(R.id.emailCreate);
        passwordCreate = findViewById(R.id.passwordCreate);
        dateCreate = findViewById(R.id.dateCreate);
        dateButton = findViewById(R.id.dateButton);
        postalCodeCreate = findViewById(R.id.postalCodeCreate);

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
                dateCreate.setText(day + "/" + (month + 1) + "/" + year), year, month, day);
        dialog.show();
    }

    public void onBtnCreate(View caller) {
        String usernameTXT = usernameCreate.getText().toString();
        String emailCreateTXT = emailCreate.getText().toString();
        int passwordCreateTXT = passwordCreate.getText().toString().hashCode();
        String dateCreateTXT = dateCreate.getText().toString();
        String postalCodeCreateTXT = postalCodeCreate.getText().toString();

        // Insert user into MySQL database
        db.insertUserData(usernameTXT, emailCreateTXT, String.valueOf(passwordCreateTXT),
                new FeedReaderDbHelper.VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(CreateAccountPage.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateAccountPage.this, HomePage.class);
                        intent.putExtra("userName", usernameTXT);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(CreateAccountPage.this, "Please provide correct data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onBtnBack(View caller) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    // Retrieve all user information from MySQL
    public void getInfo() {
        db.getData(new FeedReaderDbHelper.JsonCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                StringBuilder buffer = new StringBuilder();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject user = response.getJSONObject(i);
                        buffer.append("Username: ").append(user.getString("usernameCreate")).append("\n");
                        buffer.append("Email: ").append(user.getString("emailCreate")).append("\n");
                        buffer.append("Password: ").append(user.getString("passwordCreate")).append("\n");
                        buffer.append("Birthday: ").append(user.getString("dateCreate")).append("\n");
                        buffer.append("PostalCode: ").append(user.getString("postalCodeCreate")).append("\n\n");
                    }
                } catch (JSONException e) {
                    buffer.append("Error parsing data");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountPage.this);
                builder.setCancelable(true);
                builder.setTitle("User Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreateAccountPage.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
