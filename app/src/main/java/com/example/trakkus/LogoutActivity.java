//tanveer kaur
package com.example.trakkus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LogoutActivity extends AppCompatActivity {

    //Variables
    TextView textView1;
    Button btn_loginAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        //Hooks
        textView1 = findViewById(R.id.thanks_txt);
        // hooks Image
        btn_loginAgain = findViewById(R.id.loginAgain_Button);


    }

    public void btn_loginAgain(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
