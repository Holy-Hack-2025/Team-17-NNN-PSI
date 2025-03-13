package holyhack.eldermind;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import holyhack.eldermind.DatabaseHandling.feedReaderDbHelper;



public class ForgottenPasswordPage extends AppCompatActivity {

    private Button btnNext;
    private TextView email;
    private TextView name;
    private TextView birthdate;
    private TextView postal;

    private TextView password;
    private Button dateButton;

    private feedReaderDbHelper db;
    private int year;
    private int month;
    private int day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotten_page);

        btnNext = findViewById(R.id.btnChangePassword);
        email = findViewById(R.id.emailForgotten);
        name = findViewById(R.id.nameForgotten);
        birthdate = findViewById(R.id.dateForgotten);
        postal = findViewById(R.id.postalCodeForgotten);
        password = findViewById(R.id.newPassword);
        dateButton = findViewById(R.id.dateButtonForgotten);

        db = new feedReaderDbHelper(this);


        // Set OnClickListener for dateCreate TextView
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        //calendar stuff

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    private void openDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                birthdate.setText(String.valueOf(day)+ "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        }, year, month, day);
        dialog.show();
    }


    public void onBtnChangePassword(View caller){
        String username = name.getText().toString();
        String emailUser = email.getText().toString();
        int passwordCode = password.getText().toString().hashCode();
        String birthday = birthdate.getText().toString();
        String address = postal.getText().toString();

        if (db.checkIdentity(username, emailUser,birthday, address)){
            db.changePassword(username, passwordCode);
            Toast.makeText(ForgottenPasswordPage.this, "Password changed with sucess", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ForgottenPasswordPage.this, "Please provide the correct user information", Toast.LENGTH_SHORT).show();
        }
        db.close();

        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);

    }
}
