//Charles Jenkins (jenkinch)
//CS496 - Final Project

package cj.cs496finalproj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

//Activity for directing to "log in to account" and "account creation" screens
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
    }

    public void toLogInToAccount(View view) {
        Intent intent = new Intent(this, LogInToAccount.class);
        Login.this.startActivity(intent);
    }

    public void toCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        Login.this.startActivity(intent);
    }
}
