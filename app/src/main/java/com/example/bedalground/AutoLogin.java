package com.example.bedalground;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AutoLogin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(SavedSharedPreference.getUserEmail(AutoLogin.this).length()==0){
            Intent intent = new Intent(AutoLogin.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            Intent intent = new Intent(AutoLogin.this, MainActivity.class);
            intent.putExtra("STD_NUM", SavedSharedPreference.getUserEmail(this).toString());
            startActivity(intent);
            this.finish();
        }
    }
}
