package holyhack.eldermind;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import holyhack.eldermind.DatabaseHandling.feedReaderDbHelper;

public class CreateAccountPage extends AppCompatActivity {
    private Button btnCreate;
    private Button btnBack;
    private TextView usernameCreate;
    private TextView emailCreate;
    private TextView passwordCreate;
    private TextView dateCreate;
    private TextView postalCodeCreate;

    private StringBuilder info;

    private feedReaderDbHelper db;

    private Button dateButton;

    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);


        btnCreate = findViewById(R.id.btnCreate);
        usernameCreate = findViewById(R.id.usernameCreate);
        emailCreate = findViewById(R.id.emailCreate);
        passwordCreate = findViewById(R.id.passwordCreate);
        dateCreate = findViewById(R.id.dateCreate);
        dateButton = findViewById(R.id.dateButton);
        postalCodeCreate = findViewById(R.id.postalCodeCreate);

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
                dateCreate.setText(String.valueOf(day)+ "/" + String.valueOf(month+1) + "/" + String.valueOf(year));
            }
        }, year, month, day);
        dialog.show();
    }


    public void onBtnCreate(View caller) {
        String usernameTXT = usernameCreate.getText().toString();
        String emailCreateTXT = emailCreate.getText().toString();
        int passwordCreateTXT = passwordCreate.getText().toString().hashCode();
        String dateCreateTXT = dateCreate.getText().toString();
        String postalCodeCreateTXT = postalCodeCreate.getText().toString();

        Boolean checkinsertdata = db.insertUserData(usernameTXT,emailCreateTXT,passwordCreateTXT,dateCreateTXT,postalCodeCreateTXT);

        if(checkinsertdata==true){
            Toast.makeText(CreateAccountPage.this, "Account created with sucess", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(CreateAccountPage.this, "Please provide correct data", Toast.LENGTH_SHORT).show();
        }

        db.close();

        Bundle extras = new Bundle();
        extras.putString("userName" , usernameTXT);
        Intent intent = new Intent (this, HomePage.class);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void onBtnBack(View caller){
        Intent intent = new Intent (this, LoginPage.class);
        startActivity(intent);
    }


    //getting the information
    public void getInfo(){
        Cursor res = db.getData();


        StringBuffer buffer = new StringBuffer();

        while(res.moveToNext()){
            buffer.append("Username:" + res.getString(0) + "\n");
            buffer.append("Email:" + res.getString(0) + "\n");
            buffer.append("Password:" + res.getInt(0) + "\n");
            buffer.append("Birthday:" + res.getString(0) + "\n");
            buffer.append("PostalCode:" + res.getString(0) + "\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountPage.this);
        builder.setCancelable(true);
        builder.setTitle("User Entries");
        builder.setMessage(buffer.toString());
        builder.show();

    }
}

