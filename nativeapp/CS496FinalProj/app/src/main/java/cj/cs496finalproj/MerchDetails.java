//Charles Jenkins (jenkinch)
//CS496 - Final Project

package cj.cs496finalproj;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//Activity for adding new merch or editing existing merch
public class MerchDetails extends AppCompatActivity {

    public String keyToEdit, warehouseKey, scannedBarcode, emailInput, nameToEdit, categoryToEdit, sizeToEdit, quantityToEdit;
    public EditText barcodeEditTxt, nameEditTxt, categoryEditTxt, sizeEditTxt, quantityEditTxt;
    public String nameToPost, categoryToPost, sizeToPost, quantityToPost;
    public Button postButton, putButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merch_details);

        //Get data and prepopulate fields appropriately
        Intent intent = getIntent();
        warehouseKey = intent.getStringExtra("warehouseKey");
        scannedBarcode = intent.getStringExtra("scannedBarcode");
        emailInput = intent.getStringExtra("emailInput");
        nameToEdit = intent.getStringExtra("nameToEdit");
        categoryToEdit = intent.getStringExtra("categoryToEdit");
        sizeToEdit = intent.getStringExtra("sizeToEdit");
        quantityToEdit = intent.getStringExtra("quantityToEdit");
        keyToEdit = intent.getStringExtra("keyToEdit");

        barcodeEditTxt = (EditText) findViewById(R.id.barcodeEditTxt);
        nameEditTxt = (EditText) findViewById(R.id.nameEditTxt);
        categoryEditTxt = (EditText) findViewById(R.id.categoryEditTxt);
        sizeEditTxt = (EditText) findViewById(R.id.sizeEditTxt);
        quantityEditTxt = (EditText) findViewById(R.id.quantityEditTxt);
        postButton = (Button) findViewById(R.id.button);
        putButton = (Button) findViewById(R.id.button2);
        deleteButton = (Button) findViewById(R.id.button3);

        barcodeEditTxt.setText(scannedBarcode);
        nameEditTxt.setText(nameToEdit);
        categoryEditTxt.setText(categoryToEdit);
        sizeEditTxt.setText(sizeToEdit);
        quantityEditTxt.setText(quantityToEdit);

        //Clear "empty" optional fields containing only spaces
        if(categoryToEdit != null) {
            if (categoryToEdit.equalsIgnoreCase(" ")) {
                categoryEditTxt.setText("");
            }
        }
        if(sizeToEdit != null) {
            if (sizeToEdit.equalsIgnoreCase(" ")) {
                sizeEditTxt.setText("");
            }
        }

        //Switch to button which corresponds to PUT/DELETE calls instead of POST if a merch key carried over
        if(keyToEdit != null){
            postButton.setVisibility(View.GONE);
            putButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    //Function to execute delete function asynchronously
    public void ExecuteDeleteMerch(View view){
        try {
            Boolean result = new DeleteMerch().execute(scannedBarcode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.putExtra("emailInput", emailInput);
        MerchDetails.this.startActivity(intent2);
    }

    //Function to send delete request to API for specific piece of merch
    private class DeleteMerch extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... scanContent2) {
            Boolean returnValue = false;

            //Prepare request
            String DELETE_URL = "http://my-project-1509951488279.appspot.com/merch/" + keyToEdit;

            HttpClient client = new DefaultHttpClient();
            HttpDelete delete = new HttpDelete(DELETE_URL);

            //Execute request
            try {
                delete.setHeader("Accept", "application/json");

                HttpResponse response = client.execute(delete);
                int status = response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }

    //Function to execute PutMerch function asynchronously
    public void ExecutePutMerch(View view){
        nameToPost = nameEditTxt.getText().toString();
        categoryToPost = categoryEditTxt.getText().toString();
        sizeToPost = sizeEditTxt.getText().toString();
        quantityToPost = quantityEditTxt.getText().toString();

        //Account for deleting the contents of these optional fields
        if(categoryToPost.isEmpty())
            categoryToPost = " ";
        if(sizeToPost.isEmpty())
            sizeToPost = " ";

        //Validate inputs
        boolean invalidInput = false;

        if(nameEditTxt.getText().toString().length() == 0 || !nameEditTxt.getText().toString().matches("[a-zA-Z0-9\\s]+")) {
            nameEditTxt.setError("Merch must have a name (alphanumeric and can include spaces)!");
            invalidInput = true;
        }

        if(quantityEditTxt.getText().toString().length() == 0 || !quantityEditTxt.getText().toString().matches("[0-9]+")) {
            quantityEditTxt.setError("Quantity is required and must be numeric!");
            invalidInput = true;
        }

        if(invalidInput)
            return;

        //Execute PutMerch to edit merch
        try {
            Boolean result = new PutMerch().execute(scannedBarcode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.putExtra("emailInput", emailInput);
        MerchDetails.this.startActivity(intent2);
    }

    //Function that sends PUT request to API to edit merch
    private class PutMerch extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... scanContent2) {
            Boolean returnValue = false;

            //Post new warehouse account
            String PUT_URL = "http://my-project-1509951488279.appspot.com/merch/" + keyToEdit;

            HttpClient client = new DefaultHttpClient();
            HttpPut put = new HttpPut(PUT_URL);

            //Prepare PUT parameters and send request to API
            try {
                put.setHeader("Accept", "application/json");

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("barcode", scannedBarcode));
                nameValuePairs.add(new BasicNameValuePair("name", nameToPost));
                nameValuePairs.add(new BasicNameValuePair("category", categoryToPost));
                nameValuePairs.add(new BasicNameValuePair("size", sizeToPost));
                nameValuePairs.add(new BasicNameValuePair("quantity", quantityToPost));

                put.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(put);
                int status = response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }

    //Function that executs PostMerch function asynchronously
    public void ExecutePostMerch(View view){
        nameToPost = nameEditTxt.getText().toString();
        categoryToPost = categoryEditTxt.getText().toString();
        sizeToPost = sizeEditTxt.getText().toString();
        quantityToPost = quantityEditTxt.getText().toString();

        //Validate inputs
        boolean invalidInput = false;

        if(nameEditTxt.getText().toString().length() == 0 || !nameEditTxt.getText().toString().matches("[a-zA-Z0-9\\s]+")) {
            nameEditTxt.setError("Merch must have a name (alphanumeric and can include spaces)!");
            invalidInput = true;
        }

        if(quantityEditTxt.getText().toString().length() == 0 || !quantityEditTxt.getText().toString().matches("[0-9]+")) {
            quantityEditTxt.setError("Quantity is required and must be numeric!");
            invalidInput = true;
        }

        if(invalidInput)
            return;

        //Execute PostMerch function
        try {
            Boolean result = new PostMerch().execute(scannedBarcode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Redirect to MainActivity, passing email to maintain logged in status
        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.putExtra("emailInput", emailInput);
        MerchDetails.this.startActivity(intent2);
    }

    //Function to send POST request to API to add new merch
    private class PostMerch extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... scanContent2) {
            Boolean returnValue = false;

            //Prepare POST request
            String POST_URL = "http://my-project-1509951488279.appspot.com/merch";

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(POST_URL);

            //Prepare POST parameters and send request
            try {
                post.setHeader("Accept", "application/json");

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("barcode", scannedBarcode));
                nameValuePairs.add(new BasicNameValuePair("name", nameToPost));
                nameValuePairs.add(new BasicNameValuePair("category", categoryToPost));
                nameValuePairs.add(new BasicNameValuePair("size", sizeToPost));
                nameValuePairs.add(new BasicNameValuePair("quantity", quantityToPost));
                nameValuePairs.add(new BasicNameValuePair("warehouse", warehouseKey));

                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }
}
