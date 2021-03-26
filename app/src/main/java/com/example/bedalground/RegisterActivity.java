package com.example.bedalground;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_email, et_pwd, et_pwdcheck, et_name;
    private Button btn_register;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwdcheck = findViewById(R.id.et_pwdcheck);
        et_name = findViewById(R.id.et_name);
        btn_register = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(et_email.getText().toString(), et_pwd.getText().toString(), et_pwdcheck.getText().toString(), et_name.getText().toString());
            }
        });
    }

    private void createUser(String email, String pwd, String pwdchk, String name) {
        if(!pwd.equals(pwdchk)) Toast.makeText(RegisterActivity.this, "비밀번호를 확인해주십시오.", Toast.LENGTH_LONG).show();
        else{
            mAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //닉네임 저장하기
                                FirebaseUser user = task.getResult().getUser();
                                String uid = user.getUid().toString();
                                mDatabase.child("users").child(uid).child("name").setValue(name);
                                Toast.makeText(RegisterActivity.this, "회원가입 성공했습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "회원가입 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}