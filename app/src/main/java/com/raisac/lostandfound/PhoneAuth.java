package com.raisac.lostandfound;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

public class PhoneAuth extends AppCompatActivity implements View.OnClickListener {
    EditText mPhoneNumberField;
    TextView mRegister;
    CountryCodePicker mPicker;
    private String mPhone;
    private Intent mGetNumber;
    private String mCountryCode;
    private String mFullPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_auth);

        mPicker = findViewById(R.id.ccp);
        mPhoneNumberField = findViewById(R.id.phoneNumberEdt);

        mCountryCode = mPicker.getSelectedCountryCode();
        mPicker.registerCarrierNumberEditText(mPhoneNumberField);



        mGetNumber = getIntent();
        if (mGetNumber.hasExtra("phone")) {
            mPhone = mGetNumber.getStringExtra("phone");
            mPhoneNumberField.setText(mPhone);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerbtn:
                final String phone = mPhoneNumberField.getText().toString();
                //ful phone number
                mFullPhone = "+"+mCountryCode + phone;
                mPicker.registerCarrierNumberEditText(mPhoneNumberField);

                if (TextUtils.isEmpty(phone)) {
                    alertDialog("Phone number Required", "Error");

                } else if (phone.length() < 10) {

                    alertDialog("Enter Valid Number", "Invalid Phone");
                } else {
                    AlertDialog.Builder confirmNumber = new AlertDialog.Builder(this);
                    confirmNumber.setTitle("Confirm Phone Number");
                    confirmNumber.setMessage("Is " + mFullPhone + " your phone number");
                    confirmNumber.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent sendPhone = new Intent(PhoneAuth.this, Verification.class);
                            sendPhone.putExtra("phone", mFullPhone);
                            startActivity(sendPhone);
                            dialog.cancel();
                        }
                    });
                    confirmNumber.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent edit = new Intent(getApplicationContext(), PhoneAuth.class);
                            edit.putExtra("phone", mFullPhone);
                            startActivity(edit);
                            finish();
                        }
                    });
                    confirmNumber.show();
                    break;
                }
        }
    }

    private void alertDialog(String message, String title) {
        AlertDialog.Builder error = new AlertDialog.Builder(this);
        error.setMessage(message);
        error.setTitle(title);
        error.show();
    }


}