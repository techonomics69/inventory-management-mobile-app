//Charles Jenkins (jenkinch)
//CS496 - Final Project

package cj.cs496finalproj;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.app.Activity;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

//Activity that displays a logged in user warehouse's inventory and allows for addition, deletion
//and modification of inventory items using a barcode scanner
public class MainActivity extends Activity implements OnClickListener{

    private Button scanBtn;
    private TextView formatTxt, contentTxt, warehouseNameTxt, managerNameTxt, barcodeCell, nameCell, categoryCell, sizeCell, quantityCell, noInventory;
    public static String resultWarehouseName, resultManagerName, resultWarehouseKey;
    public String[][] resultInventory = new String[1000][5];
    public int resultInventoryLength = 0;
    public String warehouseInput, managerInput, emailInput, passwordInput, scanContent, scanFormat;
    public String resultMerchKey, resultMerchName, resultMerchCategory, resultMerchSize, resultMerchQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        warehouseInput = intent.getStringExtra("warehouseInput");
        managerInput = intent.getStringExtra("managerInput");
        emailInput = intent.getStringExtra("emailInput");
        passwordInput = intent.getStringExtra("passwordInput");
        boolean isCreation = intent.getBooleanExtra("isCreation", false);

        //If we received a message from the account creation activity then we need to post the new warehouse
        if(isCreation){
            try {
                Boolean success = new PostWarehouse().execute(emailInput).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        warehouseNameTxt = (TextView)findViewById(R.id.warehouse_name);
        managerNameTxt = (TextView)findViewById(R.id.manager_name);
        noInventory = (TextView)findViewById(R.id.noInventory);

        //Populates the screen's warehouse and inventory data
        try {
            Boolean success = new UpdateWarehouseInfo().execute(emailInput).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        warehouseNameTxt.setText("Warehouse: " + resultWarehouseName);
        managerNameTxt.setText("Manager: " + resultManagerName);

        //If there exists inventory, build its table, otherwise display a 'no inventory' message
        if(resultInventoryLength > 0) {
            noInventory.setVisibility(View.GONE);

            //Dynamically generate inventory table rows
            TableLayout ll = (TableLayout) findViewById(R.id.inventory);

            //Set table headers
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 5, 5, 5);
            row.setLayoutParams(lp);

            barcodeCell = new TextView(this);
            nameCell = new TextView(this);
            categoryCell = new TextView(this);
            sizeCell = new TextView(this);
            quantityCell = new TextView(this);

            barcodeCell.setText("Barcode");
            nameCell.setText("Name");
            categoryCell.setText("Category");
            sizeCell.setText("Size");
            quantityCell.setText("Quantity");

            barcodeCell.setBackgroundResource(R.drawable.cell_shape);
            nameCell.setBackgroundResource(R.drawable.cell_shape);
            categoryCell.setBackgroundResource(R.drawable.cell_shape);
            sizeCell.setBackgroundResource(R.drawable.cell_shape);
            quantityCell.setBackgroundResource(R.drawable.cell_shape);

            barcodeCell.setPadding(5, 5, 5, 5);
            nameCell.setPadding(5, 5, 5, 5);
            categoryCell.setPadding(5, 5, 5, 5);
            sizeCell.setPadding(5, 5, 5, 5);
            quantityCell.setPadding(5, 5, 5, 5);

            barcodeCell.setMinimumHeight(100);
            nameCell.setMinimumHeight(100);
            categoryCell.setMinimumHeight(100);
            sizeCell.setMinimumHeight(100);
            quantityCell.setMinimumHeight(100);

            row.addView(barcodeCell);
            row.addView(nameCell);
            row.addView(categoryCell);
            row.addView(sizeCell);
            row.addView(quantityCell);

            ll.addView(row, 0);

            //For each merch item, build a new row in the table
            int i = 0;
            int successfullyOutput = 0;
            while (successfullyOutput < resultInventoryLength) {
                if (resultInventory[i][0] == null) {
                    i++;
                    if (i < resultInventory.length)
                        continue;
                    else
                        break;
                }

                row = new TableRow(this);
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                barcodeCell = new TextView(this);
                nameCell = new TextView(this);
                categoryCell = new TextView(this);
                sizeCell = new TextView(this);
                quantityCell = new TextView(this);

                barcodeCell.setText(resultInventory[i][0]);
                nameCell.setText(resultInventory[i][1]);
                categoryCell.setText(resultInventory[i][2]);
                sizeCell.setText(resultInventory[i][3]);
                quantityCell.setText(resultInventory[i][4]);

                barcodeCell.setBackgroundResource(R.drawable.cell_shape);
                nameCell.setBackgroundResource(R.drawable.cell_shape);
                categoryCell.setBackgroundResource(R.drawable.cell_shape);
                sizeCell.setBackgroundResource(R.drawable.cell_shape);
                quantityCell.setBackgroundResource(R.drawable.cell_shape);

                barcodeCell.setPadding(5, 5, 5, 5);
                nameCell.setPadding(5, 5, 5, 5);
                categoryCell.setPadding(5, 5, 5, 5);
                sizeCell.setPadding(5, 5, 5, 5);
                quantityCell.setPadding(5, 5, 5, 5);

                barcodeCell.setMinimumHeight(100);
                nameCell.setMinimumHeight(100);
                categoryCell.setMinimumHeight(100);
                sizeCell.setMinimumHeight(100);
                quantityCell.setMinimumHeight(100);

                row.addView(barcodeCell);
                row.addView(nameCell);
                row.addView(categoryCell);
                row.addView(sizeCell);
                row.addView(quantityCell);

                ll.addView(row, successfullyOutput + 1);

                i++;
                successfullyOutput++;
            }
        }
        else{
            noInventory.setVisibility(View.VISIBLE);
        }

        scanBtn.setOnClickListener(this);
    }

    //Function to log out the user
    public void LogOut(View view) {
        Intent intent = new Intent(this, Login.class);
        MainActivity.this.startActivity(intent);
    }

    //Function to add a new warehouse account to the database
    private class PostWarehouse extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... emailInput2) {
            Boolean returnValue = false;

            //Prepare connection
            String POST_URL = "http://my-project-1509951488279.appspot.com/warehouses";

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(POST_URL);

            //Set post parameters and send request
            try {
                post.setHeader("Accept", "application/json");

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", warehouseInput));
                nameValuePairs.add(new BasicNameValuePair("manager", managerInput));
                nameValuePairs.add(new BasicNameValuePair("email", emailInput));
                nameValuePairs.add(new BasicNameValuePair("password", passwordInput));

                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }

    //Function that sends two GET requests (to /warehouses and to /merch) to populate screen
    private class UpdateWarehouseInfo extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... emailInput) {
            Boolean returnValue = false;

            //Get overall warehouse info
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

                //Get this user's specific data
                for(int i = 0; i < jsonEleList.size(); i++){
                    if(jsonEleList.get(i).get("email").equalsIgnoreCase(emailInput[0])){
                        resultWarehouseName = jsonEleList.get(i).get("name");
                        resultManagerName = jsonEleList.get(i).get("manager");
                        resultWarehouseKey = jsonEleList.get(i).get("key");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Get warehouse merch
            GET_URL = "http://my-project-1509951488279.appspot.com/merch";

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

                //Get this user's specific data
                for(int i = 0; i < jsonEleList.size(); i++){
                    if(jsonEleList.get(i).get("warehouse").equalsIgnoreCase(resultWarehouseKey)){
                        resultInventoryLength++;
                        resultInventory[i][0] = jsonEleList.get(i).get("barcode");
                        resultInventory[i][1] = jsonEleList.get(i).get("name");
                        resultInventory[i][2] = jsonEleList.get(i).get("category");
                        resultInventory[i][3] = jsonEleList.get(i).get("size");
                        resultInventory[i][4] = jsonEleList.get(i).get("quantity");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }

    //Handler to launch scanner
    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    //Handles returning from successfully scanning a barcode
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        //Proceed if we received a valid result
        if (scanningResult != null) {
            scanContent = scanningResult.getContents();
            scanFormat = scanningResult.getFormatName();

            //Limit acceptable codes to UPC_A for consistency
            if(scanContent != null) {
                if (!scanFormat.equals("UPC_A")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "You scanned a " + scanFormat + " code. This app currently only supports UPC_A!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                //Prepare intent to launch MerchDetails activity
                Intent intent2 = new Intent(this, MerchDetails.class);

                //If merch barcode exists in database, this is an edit request
                try {
                    Boolean merchDoesExist = new MerchExists().execute(scanContent).get();

                    //If scanned merch already exists, prepare data to prepopulate edit screen
                    if(merchDoesExist){
                        intent2.putExtra("nameToEdit", resultMerchName);
                        intent2.putExtra("categoryToEdit", resultMerchCategory);
                        intent2.putExtra("sizeToEdit", resultMerchSize);
                        intent2.putExtra("quantityToEdit", resultMerchQuantity);
                        intent2.putExtra("keyToEdit", resultMerchKey);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                //Prepare intent and redirect to add/edit screen
                intent2.putExtra("scannedBarcode", scanContent);
                intent2.putExtra("warehouseKey", resultWarehouseKey);
                intent2.putExtra("emailInput", emailInput);
                MainActivity.this.startActivity(intent2);
            }
            else{
                //finish();
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Function for checking if a piece of merch already exists
    private class MerchExists extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... scanContent2) {
            Boolean returnValue = false;

            //Get warehouse merch
            String GET_URL = "http://my-project-1509951488279.appspot.com/merch";

            try {
                //Connect to API
                URL obj = new URL(GET_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //Set request method
                con.setRequestMethod("GET");

                //Add request header
                con.setRequestProperty("Accept", "application/json");

                //Prepare variables to read in API response
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

                //Check if barcode/warehouse key combo exists (allows for different warehouses to have same barcode'd items
                for(int i = 0; i < jsonEleList.size(); i++){
                    if((jsonEleList.get(i).get("barcode")+jsonEleList.get(i).get("warehouse")).equalsIgnoreCase(scanContent+resultWarehouseKey)){
                        returnValue = true;
                        resultMerchKey = jsonEleList.get(i).get("key");
                        resultMerchName = jsonEleList.get(i).get("name");
                        resultMerchCategory = jsonEleList.get(i).get("category");
                        resultMerchSize = jsonEleList.get(i).get("size");
                        resultMerchQuantity = jsonEleList.get(i).get("quantity");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnValue;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
