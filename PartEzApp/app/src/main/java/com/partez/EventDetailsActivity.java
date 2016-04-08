package com.partez;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.partez.DataWrapper.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class EventDetailsActivity extends AppCompatActivity
{

    private static final String TAG = "EventActivity";
    private static Result resultForNextScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final RadioButton isPublic = (RadioButton)findViewById(R.id.public_event);

        isPublic.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                if(isPublic.isChecked()) {
                    isPublic.setChecked(false);
                    return true;
                }
                return false;
            }
        });
    }

    public void addPollEditText(View view)
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pollEditTextGroupLayout);
        EditText editTextView = new EditText(this);
        editTextView.setGravity(Gravity.CENTER);

        linearLayout.addView(editTextView);
    }

    public void addItemEditText(View view)
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.itemEditTextGroupLayout);
        EditText editTextView = new EditText(this);
        editTextView.setGravity(Gravity.CENTER);

        linearLayout.addView(editTextView);
    }

    public void addInvitationEditText(View view)
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.inviteEditTextGroupLayout);
        EditText editTextView = new EditText(this);
        editTextView.setGravity(Gravity.CENTER);

        linearLayout.addView(editTextView);
    }

    private String getEmails()
    {
        String emails = "";
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.inviteEditTextGroupLayout);

        for(int i = 0; i < linearLayout.getChildCount(); i++)
        {
            View v = linearLayout.getChildAt(i);
            // get comma separated list of emails
            if (v instanceof EditText)
            {
                String email = ((EditText) v).getText().toString();
                emails += email + ",";
            }
        }

        // return emails without trailing comma
        if(emails.length() > 0)
            emails = emails.substring(0,emails.length()-1);

        return emails;
    }

    private JSONArray getJsonEditTextOutput(LinearLayout layout, String name) throws JSONException
    {
        JSONObject pair = new JSONObject();
        JSONArray result = new JSONArray();

        for(int i = 0; i < layout.getChildCount(); i++)
        {
            View v = layout.getChildAt(i);
            // get comma separated list of emails
            if (v instanceof EditText)
            {
                String element = ((EditText) v).getText().toString();

                pair.put(name, element);
                result.put(pair);
            }
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void submitEvent(View view) throws JSONException
    {
        String publicEvent = "0";
        String token = "Missing Token";
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            token = extras.getString("token");
        }

        RequestParams params = new RequestParams();

        LinearLayout pollInput = (LinearLayout) findViewById(R.id.pollEditTextGroupLayout);
        JSONArray polloptions = getJsonEditTextOutput(pollInput, "option");

        LinearLayout itemInput = (LinearLayout) findViewById(R.id.itemEditTextGroupLayout);
        JSONArray items = getJsonEditTextOutput(itemInput, "description");

        // LinkedHashMap event = new LinkedHashMap();
        JsonObject json = new JsonObject();

        JSONArray jsonArray = new JSONArray();
        JSONObject outerJson = new JSONObject();
        JSONObject event = new JSONObject();

        JSONArray invitees = new JSONArray();
        String emails = getEmails();
        if(emails.length() > 0)
            invitees.put(emails);

        String name   = ((EditText)findViewById(R.id.event_name)).getText().toString();
        String location   = ((EditText)findViewById(R.id.event_location)).getText().toString();
        String description   = ((EditText)findViewById(R.id.event_description)).getText().toString();

        DatePicker datePicker = ((DatePicker)findViewById(R.id.event_date));
        String eventDate = datePicker.getDayOfMonth() + "/" + datePicker.getMonth() + "/" + datePicker.getYear();

        TimePicker stimePicker = ((TimePicker)findViewById(R.id.stime_picker));
        String stime = stimePicker.getHour() + ":" + stimePicker.getMinute();

        TimePicker etimePicker = ((TimePicker)findViewById(R.id.etime_picker));
        String etime = etimePicker.getHour() + ":" + etimePicker.getMinute();

        Boolean isPublic = ((RadioButton)findViewById(R.id.public_event)).isChecked();

        if(isPublic)
            publicEvent = "1";

        resultForNextScreen = new Result();
        resultForNextScreen.name = name;
        resultForNextScreen.location = location;
        resultForNextScreen.description = description;
        resultForNextScreen.date = eventDate;
        resultForNextScreen.stime = stime;
        resultForNextScreen.etime = etime;
        resultForNextScreen.eventPublic = publicEvent;

        event.put("name", name);
        event.put("location", location);
        event.put("description", description);
        event.put("date", eventDate);
        event.put("stime", stime);
        event.put("etime", etime);
        event.put("public", publicEvent);

        jsonArray.put(event);
        outerJson.put("event", event);
        outerJson.put("polloptions", polloptions);
        outerJson.put("items", items);
        outerJson.put("emails", invitees);

        ByteArrayEntity entity = new ByteArrayEntity(outerJson.toString().getBytes());

        PartezRestClient.postEntity(getApplicationContext(), "api_submit_event", token, entity, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                // If the response is JSONObject instead of expected JSONArray
                Log.d(TAG, Arrays.toString(headers));
                Log.d(TAG, Integer.toString(statusCode));

                Toast.makeText(getApplicationContext(), "Success Object", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline)
            {
                // Do something with the response
                Log.d(TAG, Arrays.toString(headers));
                Log.d(TAG, Integer.toString(statusCode));

                Toast.makeText(getApplicationContext(), "Success Array", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response)
            {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, Arrays.toString(headers));
                Log.d(TAG, Integer.toString(statusCode));
                Log.d(TAG, response.toString());
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String someString, Throwable e)
            {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(TAG, Arrays.toString(headers));
                Log.d(TAG, Integer.toString(statusCode));
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.putExtra("eventToPass", resultForNextScreen);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Created Event", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
