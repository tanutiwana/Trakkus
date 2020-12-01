//Parul
package com.example.trakkus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {

    ///veriables
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_activity);

        //hooks
        btn1 = findViewById(R.id.contact1_btn);
        btn2 = findViewById(R.id.contact2_btn);
        btn3 = findViewById(R.id.contact3_btn);
        btn4 = findViewById(R.id.contact4_btn);
        btn5 = findViewById(R.id.contact5_btn);
        btn6 = findViewById(R.id.contact6_btn);
        btn7 = findViewById(R.id.contact7_btn);
        btn8 = findViewById(R.id.contact8_btn);

    }

    //button for 1st contact person
    public void btn_contact1(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:102"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "call me Back", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(callIntent);


    }

    //button for contact 2 person
    public void btn_contact2(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:112"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "National help line number", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);


    }

    //3rd button
    public void btn_contact3(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:101"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Fire help line number", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);


    }

    //4th button
    public void btn_contact4(View view) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:1098"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Child Helpline number", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);
    }

    //5th button
    public void btn_contact5(View view) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:1091"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Women Helpline Number", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);
    }


    //6th button
    public void btn_contact6(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:100"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Call the police", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);

    }

    //7th button
    public void btn_contact7(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:1363"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Tourist Helpline", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);

    }

    //8th button
    public void btn_contact8(View view) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:139"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Railway enquiry", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);

    }
     //9th button
    public void btn_contact8(View view) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:911"));

        if (ActivityCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ContactActivity.this, "Canada's emergency", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(callIntent);

    }
}
