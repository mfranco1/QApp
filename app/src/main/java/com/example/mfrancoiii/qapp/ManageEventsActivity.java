package com.example.mfrancoiii.qapp;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ManageEventsActivity extends AppCompatActivity {

    JSONObject holder;
    String permusername;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);
        sharedPreferences = getSharedPreferences("QApp", Context.MODE_PRIVATE);
        permusername = sharedPreferences.getString("username",null);

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
                    JSONArray events = holder.getJSONArray("result");
                    for(int i=0; i<events.length(); i++) {
                        JSONObject event = (JSONObject) events.get(i);
                        TextView position1 = new TextView(getApplicationContext());
                        Button position2 = new Button(getApplicationContext());
                        TableRow newRow = new TableRow(getApplicationContext());

                        final String eventName = event.getString("EventName");
                        String eventID = event.getString("EventID");
                        String viewButton = "View";//event.getString("UserID");
                        String userid = event.getString("UserID");
                        if(!userid.contains(permusername)){
                            continue;
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("qapp"+eventName,eventID);
                        editor.commit();

                        position1.setText(eventName);
                        position2.setText(viewButton);
                        position1.setTextColor(Color.parseColor("#ef8402"));
                        position2.setTextColor(Color.parseColor("#ef8402"));
                        position1.setBackgroundColor(Color.TRANSPARENT);
                        position2.setBackgroundColor(Color.TRANSPARENT);
                        position1.setGravity(Gravity.CENTER);
                        position2.setGravity(Gravity.CENTER);
                        position2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("qapp_curr_line",eventName);
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), AdministerEvent.class));
                            }
                        });
                        newRow.addView(position1);
                        newRow.addView(position2);
                        imgContainer.addView(newRow);
                    }
                } catch(Exception e){
                }
            }
            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
                holder = response;
            }
        };
        try{
            client.post("http://10.0.2.2/qapp/h/searchevent", responseHandler);
        } catch(Exception e){
        }
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
