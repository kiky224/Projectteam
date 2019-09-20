package com.example.test.parkingapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    Button btnCal;
    private static final String TAG_JSON = "test";
    private static final String TAG = "parkingapp_MainActivity";
    private static final String CAR_ID = "carId";
    private static final String INTIME = "inTime";
    ArrayList mArrayList = new ArrayList<>();
    String myJson;

    String carid, intime;
    boolean input = false;
    int selcar = -1;

    ArrayList<String> arrayList1 = new ArrayList<>();
    ArrayList<String> arrayList2 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCal = findViewById(R.id.btnCal);

        String data = "http://192.168.10.4/incar.php";
        getData(data);
        Log.d("abcdefg", data);


        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }


    protected void showList() {
        try {
            JSONObject jsonObject = new JSONObject(myJson);
            JSONArray cars = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < cars.length(); i++) {
                JSONObject object = cars.getJSONObject(i);

                String carId = object.getString(CAR_ID);
                String inTime = object.getString(INTIME);


                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(CAR_ID, carId);
                hashMap.put(INTIME, inTime);

                mArrayList.add(hashMap);

                Log.d("1234", hashMap.get(CAR_ID) + hashMap.get(INTIME));
                Log.d("23323", String.valueOf(mArrayList.get(i)));
                Log.d("12", hashMap.get(CAR_ID));


                carid = String.valueOf(hashMap.get(CAR_ID));
                intime = String.valueOf(hashMap.get(INTIME));
                arrayList1.add(carid);
                arrayList2.add(intime);
                Log.d("zx", carid);
                intime = hashMap.get(INTIME);

            }

        } catch (Exception e) {
            Log.d("error", "show result:", e);
        }
    }

    public void getData(String url) {

        @SuppressLint("StaticFieldLeak")
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }
                    bufferedReader.close();
                    Log.d("sb", sb.toString().trim());
                    return sb.toString().trim();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("qw", "qwer");
                }
                return uri;
            }

            protected void onPostExecute(String result) {
                myJson = result;
                showList();
                for (int i = 0; i < mArrayList.size(); i++) {
                    Intent intent = new Intent();
                    intent.putExtra("car_id", String.valueOf(arrayList1.get(i)));
                    Log.d("asdf", String.valueOf(arrayList1.get(i)));
                    intent.putExtra("in_time", String.valueOf(arrayList2.get(i)));
                    Log.d("asdfg", String.valueOf(arrayList2.get(i)));

                }

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    void show() {
        final EditText edittext = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), CalActivity.class);
                        String result = edittext.getText().toString();
                        for (int i = 0; i < arrayList1.size(); i++) {
                            if (result.equals(arrayList1.get(i))) {
                                selcar = i;
                                Log.d("selcar", "" + selcar);
                            }
                        }
                        Log.d("inputcatime", "" + arrayList2.get(selcar));
                        intent.putExtra("inputcartime", arrayList2.get(selcar));
                        intent.putExtra("inputcarname", arrayList1.get(selcar));
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}

