package com.raisac.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Button google, facebook, twiter, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        google = findViewById(R.id.google);
        facebook = findViewById(R.id.facebook);
        twiter = findViewById(R.id.twitter);
        phone = findViewById(R.id.phonenumber);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.google) {
            Toast.makeText(this, "Google sign in", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.facebook) {
            Toast.makeText(this, "Facebook sign in", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.twitter) {
            Toast.makeText(this, "Twitter sign in", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.phonenumber) {
            startActivity(new Intent(SignUp.this, PhoneAuth.class));
        }

    }
}
