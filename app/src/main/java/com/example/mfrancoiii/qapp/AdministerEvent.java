package com.example.mfrancoiii.qapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AdministerEvent extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String permusername;
    JSONObject holder;
    String eventNum;
    String univEID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administer_event);
        sharedPreferences = getSharedPreferences("QApp", Context.MODE_PRIVATE);
        permusername = sharedPreferences.getString("username",null);
        String currLine = sharedPreferences.getString("qapp_curr_line",null);
        univEID = sharedPreferences.getString("qapp"+currLine,null);
        Button processQ = (Button) findViewById(R.id.process_button);
        processQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processQueue();
            }
        });

        final TextView topHeader = (TextView) findViewById(R.id.eventLabel);
        final TableLayout imgContainer = (TableLayout) findViewById(R.id.listContainer);
        AsyncHttpClient client = new AsyncHttpClient();
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onStart(){

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try{
                    holder = response;
                    JSONArray eventInfo = holder.getJSONArray("event");
                    JSONObject eventInfo2 = eventInfo.getJSONObject(0);
                    String eventName = eventInfo2.getString("EventName");
                    eventNum = eventInfo2.getString("EventID");
                    topHeader.setText(eventName);
                } catch(Exception e){
                }
            }
            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
            }
        };
        try{
            RequestParams params = new RequestParams();
            params.put("event",eventNum);
            client.post("http://10.0.2.2/qapp/h/event",params, responseHandler);
        } catch(Exception e){
        }

        JsonHttpResponseHandler responseHandler2 = new JsonHttpResponseHandler(){
            @Override
            public void onStart(){

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try{
                    holder = response;
                    JSONArray peopleList = holder.getJSONArray("peoplejoined");
                    for(int i=0; i<peopleList.length(); i++){
                        JSONObject person = (JSONObject) peopleList.get(i);
                        TextView position1 = new TextView(getApplicationContext());
                        TableRow newRow = new TableRow(getApplicationContext());

                        String personName = person.getString("UserID");

                        position1.setText(personName);
                        position1.setTextColor(Color.parseColor("#ef8402"));
                        position1.setBackgroundColor(Color.TRANSPARENT);
                        position1.setGravity(Gravity.CENTER);
                        newRow.addView(position1);
                        imgContainer.addView(newRow);
                    }
                } catch(Exception e){
                }
            }
            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
            }
        };
        try{
            RequestParams params = new RequestParams();
            params.put("eventno",eventNum);
            client.post("http://10.0.2.2/qapp/h/myevent", params,responseHandler2);
        } catch(Exception e){
        }
    }

    public void processQueue(){
        AsyncHttpClient client = new AsyncHttpClient();
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onStart(){

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try{
                    holder = response;
                    buildAlertMessageEventCreated("Top of queue has been processed!");
                } catch(Exception e){
                }
            }
            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
            }
        };
        RequestParams params = new RequestParams();
        params.put("eventid",eventNum);
        params.put("username",permusername);
        try{
            client.post("http://10.0.2.2/qapp/h/processed", responseHandler);
        } catch(Exception e){
        }
    }

    private void buildAlertMessageEventCreated(String s) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
