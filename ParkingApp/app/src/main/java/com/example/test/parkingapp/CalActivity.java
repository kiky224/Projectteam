package com.example.test.parkingapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.TestLooperManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalActivity extends AppCompatActivity {

    Button btnPay, btnCharge;
    TextView tvTime, tvPrice, tvRemain;
    private static final String TAG_JSON = "test";
    private static final String TAG = "parkingapp_MainActivity";
    private static final String CAR_ID = "carId";
    private static final String OUTTIME = "outTime";
    private static final String POINT = "point";
    ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();
    String myJson;
    ArrayList<String> carid;
    String intime;
    String IP_ADDRESS = "192.168.10.4";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);

        btnPay = findViewById(R.id.btnPay);
        tvPrice = findViewById(R.id.tvPrice);
        tvTime = findViewById(R.id.tvTime);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Intent intent = getIntent();

        String aa = intent.getStringExtra("inputcartime");

        SimpleDateFormat f = new SimpleDateFormat("YYYY-MM-dd HH:mm", Locale.KOREA);
        String ab = f.format(date);
        Date intime = null;
        Date outtime = null;

        try {
            outtime = f.parse(ab);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            intime = f.parse(aa);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long result = outtime.getTime() - intime.getTime();
        long si = (result / 1000) / 3600;
        long bun = ((result / 1000) % 3600) / 60;

        long total = (result / 1000) / 60;
        long total2 = (total / 10) * 10;
        tvTime.setText(+si + "시간" + "\n" + bun + "분 동안 주차하셨습니다." + "\n" + "총 이용시간은 " + total + "분입니다.");

        long bill = total2 * 1000;
        if (bill > 20000) {
            bill = 20000;
        }

        tvPrice.setText("지불하실 이용료는 :" + bill);
//        ArrayList<String> arrayList1 = intent.getStringArrayListExtra("arr1");
//        ArrayList<String> arrayList2 = intent.getStringArrayListExtra("arr2");


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = getIntent();
                String carId = intent1.getStringExtra("inputcarname");
                String payCheck = "1";

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", payCheck, carId);
            }
        });

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CalActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String payCheck = (String) params[1];
            String carId = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "payCheck=" + payCheck + "&carId=" + carId;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == httpURLConnection.HTTP_OK) { // HTTP_OK 는 200
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData:Error", e);
                return new String("Error: " + e.getMessage());
            }

        }
    }
}
