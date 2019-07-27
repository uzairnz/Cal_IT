package com.zubairy.cal_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserInfo extends AppCompatActivity {

    private static final String TAG = UserInfo.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputName, inputEmail , inputPhone , inputAge , inputWeight , inputHeight , inputPassword;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;


    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        auth = FirebaseAuth.getInstance();

        // to get data from the activity

        inputName = (EditText) findViewById(R.id.edit1);
        inputEmail = (EditText) findViewById(R.id.edit2);
        inputPassword = (EditText) findViewById(R.id.edit7);
        inputPhone = (EditText) findViewById(R.id.edit3);
        inputAge = (EditText) findViewById(R.id.edit4);
        inputWeight = (EditText) findViewById(R.id.edit5);
        inputHeight = (EditText) findViewById(R.id.edit6);
        btnSave = (Button) findViewById(R.id.button1);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("Users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Cal_It");



        // Save / update the user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String phone = inputPhone.getText().toString();
                String age = inputAge.getText().toString();
                String weight = inputWeight.getText().toString();
                String height = inputHeight.getText().toString();
                createUser(name, email , password , phone , age , weight , height);

            }
        });
    }

    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name, String email , String password ,  String phone , String age , String weight , String height) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();

        }
        Users users = new Users(name, email, password, phone, age, weight, height);
        mFirebaseDatabase.child(userId).setValue(users);

        signIn(name, email,  password, phone, age, weight, height);

    }

   private void signIn(String name, String email, String password, String phone, String age, String weight, String height){

       String Email = email;
       String Password = password;
       String Name = name;
       String Phone = phone;
       String Age = age;
       String Weight = weight;
       String Height = height;

       if (TextUtils.isEmpty(Name)) {
           Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (TextUtils.isEmpty(Email)) {
           Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (TextUtils.isEmpty(Password)) {
           Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (TextUtils.isEmpty(Phone)) {
           Toast.makeText(getApplicationContext(), "Enter Phone Number!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (TextUtils.isEmpty(Age)) {
           Toast.makeText(getApplicationContext(), "Enter Age!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (TextUtils.isEmpty(Weight)) {
           Toast.makeText(getApplicationContext(), "Enter Weight!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (TextUtils.isEmpty(Height)) {
           Toast.makeText(getApplicationContext(), "Enter Height!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (Password.length() < 6) {
           Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
           return;
       }

       if (Phone.length() < 13) {
           Toast.makeText(getApplicationContext(), "Invalid Phone Number!", Toast.LENGTH_SHORT).show();
           return;
       }


       // progressBar.setVisibility(View.VISIBLE);
       //create user
       auth.createUserWithEmailAndPassword(email, password)
               .addOnCompleteListener(UserInfo.this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       Toast.makeText(UserInfo.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                       //progressBar.setVisibility(View.GONE);
                       // If sign in fails, display a message to the user. If sign in succeeds
                       // the auth state listener will be notified and logic to handle the
                       // signed in user can be handled in the listener.
                       if (!task.isSuccessful()) {
                           Toast.makeText(UserInfo.this, "Authentication failed." + task.getException(),
                                   Toast.LENGTH_SHORT).show();
                       }
                       else {
                           Toast.makeText(getApplicationContext(),"Sign_Up Successful",Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(UserInfo.this, MainActivity.class));
                           finish();
                       }
                   }
               });
   }
}