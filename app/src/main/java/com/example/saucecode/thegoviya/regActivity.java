package com.example.saucecode.thegoviya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class regActivity extends AppCompatActivity implements View.OnClickListener{

    private Button regBtn;
    private EditText password;
    private EditText fName;
    private EditText nic;
    private EditText add;
    private EditText telNo;
    private TextView logInForm;
    private ProgressDialog progress;
    private List<String> list = new ArrayList<String>();
    private Spinner userType;

    private String nicNum;
    private String fullName;
    private String address;
    private String mobileNum;
    private int mobile;
    private String pass;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        progress = new ProgressDialog(this);
        fName = (EditText)findViewById(R.id.fName);
        nic = (EditText)findViewById(R.id.nic);
        regBtn = (Button)findViewById(R.id.regBtn);
        password = (EditText)findViewById(R.id.password);
        logInForm = (TextView)findViewById(R.id.txtViewRegd);
        add = (EditText)findViewById(R.id.add);
        telNo = (EditText)findViewById(R.id.telNo);

        regBtn.setOnClickListener(this);
        logInForm.setOnClickListener(this);

        userType = (Spinner)findViewById(R.id.userType);

        list.add("Farmer");
        list.add("Buyer");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(dataAdapter);

        userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = list.get(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == regBtn){
            registerUser();
        }

        if(v == logInForm){
            Intent loginIntent = new Intent(this,MainActivity.class);
            startActivity(loginIntent);
            this.finish();
        }
    }

    private void registerUser(){

        nicNum = nic.getText().toString();
        address = add.getText().toString();
        pass = password.getText().toString();
        mobile = Integer.parseInt(telNo.getText().toString());
        fullName = fName.getText().toString();
        if(type == "")type = "Farmer";

        if(TextUtils.isEmpty(nicNum)){
            //if email is empty
            Toast.makeText(this,"Please enter your nic number!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(fullName)){
            //password is empty
            Toast.makeText(this,"Please enter your full name!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mobile == 0){
            //if email is empty
            Toast.makeText(this,"Please enter your mobile number!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(pass)){
            //password is empty
            Toast.makeText(this,"Please enter your password!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(address)){
            //password is empty
            Toast.makeText(this,"Please enter your address!",Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Registering user...");
        progress.show();

        RegisterUser reg = new RegisterUser();
        reg.execute("");
    }


    public void loadHome(){
        Intent homeIntent = new Intent(this, homeActivity.class);
        startActivity(homeIntent);
        this.finish();
    }

    public void buyerHome(){
        Intent homeIntent = new Intent(this, BuyerHome.class);
        startActivity(homeIntent);
        this.finish();
    }

    public class RegisterUser extends AsyncTask<String, String,String> {
        Person person = new Person();

        public JSONObject getJSONObject(){

            person.setNicNumber(nicNum);
            person.setfName(fullName);
            person.setMobileNumber(mobile);
            person.setAddress(address);
            person.setType(type);

            JSONObject obj = new JSONObject();
            try {
                obj.put("NIC", nicNum);
                obj.put("fullName", fullName);
                obj.put("mobile", mobile);
                obj.put("address", address);
                obj.put("password", pass);

                System.out.println(obj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpConnection;
            URL mUrl;
            try {
                if(type.equalsIgnoreCase("Buyer")) mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Buyer");
                else mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Farmer");
                httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setRequestProperty("Content-length", "" + getJSONObject().toString().length() + "");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();
                DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
                os.writeBytes(getJSONObject().toString());
                os.flush();
                os.close();
                int responseCode = httpConnection.getResponseCode();
                if(responseCode == 204 || responseCode == RESULT_OK) return "success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "failed";
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equalsIgnoreCase("success")){
                progress.hide();
                Toast.makeText(regActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(regActivity.this, MainActivity.class);
                intent.putExtra("person", person);
                startActivity(intent);
                finish();

            }
        }
    }


}
