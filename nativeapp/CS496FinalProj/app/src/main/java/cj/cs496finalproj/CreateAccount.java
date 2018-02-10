//Charles Jenkins (jenkinch)
//CS496 - Final Project

package cj.cs496finalproj;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

//Activity for account creation
public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Intent intent = getIntent();
    }

    //Validate and send credentials for access to MainActivity
    public void sendCredentials(View view) {
        boolean invalidInput = false;

        EditText warehouseEditTxt = (EditText) findViewById(R.id.warehouseEditTxt);
        EditText managerEditTxt = (EditText) findViewById(R.id.managerEditTxt);
        EditText emailEditTxt = (EditText) findViewById(R.id.emailEditTxt);
        EditText passwordEditTxt = (EditText) findViewById(R.id.passwordEditTxt);

        //Validate inputs
        if(warehouseEditTxt.getText().toString().length() == 0 || !warehouseEditTxt.getText().toString().matches("[a-zA-Z0-9\\s]+")) {
            warehouseEditTxt.setError("A warehouse name containing only alphanumeric characters and spaces is required.");
            invalidInput = true;
        }

        if(managerEditTxt.getText().toString().length() == 0 || !managerEditTxt.getText().toString().matches("[a-zA-Z\\s]+")) {
            managerEditTxt.setError("A manager name containing only alphabetic characters and spaces is required.");
            invalidInput = true;
        }

        if(emailEditTxt.getText().toString().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditTxt.getText().toString()).matches()) {
            emailEditTxt.setError("A valid email address.");
            invalidInput = true;
        }

        if(passwordEditTxt.getText().toString().length() == 0) {
            passwordEditTxt.setError("Password is required!");
            invalidInput = true;
        }

        if(invalidInput)
            return;

        //Create intent directed at MainActivity
        Intent intent = new Intent(this, MainActivity.class);

        String warehouseInput = warehouseEditTxt.getText().toString();
        String managerInput = managerEditTxt.getText().toString();
        String emailInput = emailEditTxt.getText().toString();
        String passwordInput = passwordEditTxt.getText().toString();

        //Check if an account already exists with that email
        try {
            Boolean accountDoesExist = new AccountExists().execute(emailInput).get();

            if(accountDoesExist){
                Toast toast = Toast.makeText(getApplicationContext(),
                    "An account with that email already exists!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Prepare intent and redirect to MainActivity
        intent.putExtra("warehouseInput", warehouseInput);
        intent.putExtra("managerInput", managerInput);
        intent.putExtra("emailInput", emailInput);
        intent.putExtra("passwordInput", passwordInput);
        intent.putExtra("isCreation", true);

        CreateAccount.this.startActivity(intent);
    }

    //Functions for checking whether an account with a specified email already exists
    private class AccountExists extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... emailInput) {
            String TAG = "GetActivity";
            Boolean returnValue = false;

            String GET_URL = "http://my-project-1509951488279.appspot.com/warehouses";

            try {
                //Connect to API
                URL obj = new URL(GET_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //Set request method
                con.setRequestMethod("GET");

                //Add request header
                con.setRequestProperty("Accept", "application/json");

                //Prepare variables to read in response from API
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                //Append opening list bracket
                response.append("[");

                //Read in response from API
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);

                    if(inputLine != null)
                        //Append delimiting comma
                        response.append(",");
                }
                in.close();

                String responseString = response.toString();

                //Trim off last stray comma
                responseString = responseString.substring(0, responseString.length()-1);

                //Append closing list bracket
                responseString = responseString + "]";

                //Parse result
                JsonElement json = new JsonParser().parse(responseString);

                //Map JSON to hashmap
                Gson gson = new Gson();
                Type listType = new TypeToken<List<HashMap<String, String>>>(){}.getType();
                List<HashMap<String, String>> jsonEleList = gson.fromJson(json, listType);

                for(int i = 0; i < jsonEleList.size(); i++){
                    if(jsonEleList.get(i).get("email").equalsIgnoreCase(emailInput[0])){
                        returnValue = true;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }
}