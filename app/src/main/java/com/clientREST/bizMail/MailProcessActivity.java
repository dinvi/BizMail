package com.clientREST.bizMail;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class MailProcessActivity extends AppCompatActivity {

    private static final String TAG = "MailProcessActivity";
    private static final String QUERY_URL = "http://192.168.43.105:8080/mail/query";;
    private static final String ACTION_POST = "POST";
    private static final String ACTION_GET = "GET";

    private static final String SENDER = "sender";
    private static final String SUBJECT = "subject";
    private static final String TEXT = "text";

    private static final String TAG_STATE_QUERY = "state";
    private static final String TAG_OUTPUT_QUERY = "output";
    private static final String TAG_DATA_QUERY = "data";
    private static final String TAG_MAIL = "text/plain";

    private TextView _address;
    private TextView _subject;
    private TextView _text;
    private Button _get;

    private int idRequest = 0;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmail);

        Bundle extras = getIntent().getExtras();
        Mail mail = extras.getParcelable("mailObject");

        queryMail(mail);
        progress = ProgressDialog.show(this, "Send process request to server...", "Waiting For Results...", true);

        _address = (TextView) findViewById(R.id.mailAddress);
        _address.setText(mail.getSender());
        _subject = (TextView) findViewById(R.id.mailObject);
        _subject.setText(mail.getSubject());
        _text = (TextView) findViewById(R.id.mailText);
        _text.setText(mail.getText());

        _get = (Button) findViewById(R.id.btn_get);

        _get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryRequest();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(receiver, new IntentFilter(ACTION_POST));
        //this.registerReceiver(receiver, new IntentFilter(ACTION_GET));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }

    private void queryMail(Mail mail) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(new URI(QUERY_URL));
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(SENDER, mail.getSender());
            jsonObject.accumulate(SUBJECT, mail.getSubject());
            jsonObject.accumulate(TEXT, mail.getText());
            String json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            RestTask task = new RestTask(this, ACTION_POST);
            task.execute(httpPost);
        } catch (Exception e) {
            Log.e("sessionRequest", e.getMessage());
        }

    }


    private void queryRequest() {
        try {
            HttpGet httpGet = new HttpGet(new URI(QUERY_URL + idRequest ));
            RestTask task = new RestTask(this, ACTION_GET);
            task.execute(httpGet);
            progress = ProgressDialog.show(this, "Getting status mail ...", "Waiting For Classification...", true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * Our Broadcast Receiver. We get notified that the data is ready, and then we
     * put the content we receive (a string) into the TextView.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Broadcast Receiver");

            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            String output = null;
            JSONObject jsonObj = null;

            if(intent.getAction().equalsIgnoreCase(ACTION_POST)) {
                try {
                    jsonObj = new JSONObject(response);
                    output = jsonObj.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                idRequest = Integer.parseInt(output);
                Toast.makeText(context, "Processing request id:" + output, Toast.LENGTH_LONG).show();
            }else if(intent.getAction().equalsIgnoreCase(ACTION_GET)){
                try {
                    jsonObj = new JSONObject(response);
                    output = jsonObj.getString(TAG_STATE_QUERY);
                    if(output.equalsIgnoreCase("available")) {
                        output = jsonObj.getJSONObject(TAG_OUTPUT_QUERY)
                                .getJSONObject(TAG_DATA_QUERY)
                                .getString(TAG_MAIL);
                        output = output.substring(0, output.indexOf('\n'));
                        Toast.makeText(context, "Mail classification: " + output, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "Mail classification not yet ready", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            Log.i(TAG, "RESPONSE = " + response);
        }
    };

}
