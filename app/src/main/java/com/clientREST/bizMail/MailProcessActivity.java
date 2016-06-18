package com.clientREST.bizMail;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    private static final String URL = "http://192.168.1.17:8080/mail/";
    private static final String URL_QUERY = "http://192.168.1.17:8080/mail/query";
    //private static final String URL_QUERY = "http://192.168.43.105:8080/mail/query";;

    private static final String ACTION_QUERY = "QUERY";
    private static final String ACTION_GET = "GET";

    private static final String SENDER = "sender";
    private static final String SUBJECT = "subject";
    private static final String TEXT = "text";

    private static final String ID_REQ = "id";
    private static final String TYPE_REQ = "type";
    private static final String STATE_REQ = "state";

    private static final String CLASSIFICATION = "classification";

    private TextView _address;
    private TextView _subject;
    private TextView _text;
    private Button _get;
    private Button _query;

    Request req;
    private int idRequest;

    ProgressDialog progress;
    Mail mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmail);

        Bundle extras = getIntent().getExtras();
        mail = extras.getParcelable("mailObject");

        _address = (TextView) findViewById(R.id.mailAddress);
        _address.setText(mail.getSender());
        _subject = (TextView) findViewById(R.id.mailObject);
        _subject.setText(mail.getSubject());
        _text = (TextView) findViewById(R.id.mailText);
        _text.setText(mail.getText());

        _query = (Button) findViewById(R.id.btn_query);
        _get = (Button) findViewById(R.id.btn_get);
        _get.setVisibility(View.INVISIBLE);

        _query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryMail(mail);
                _get.setVisibility(View.VISIBLE);
            }
        });

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
        this.registerReceiver(receiver, new IntentFilter(ACTION_QUERY));
        this.registerReceiver(receiver, new IntentFilter(ACTION_GET));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }

    private void queryMail(Mail mail) {
        try {
            HttpPost httpPost = new HttpPost(new URI(URL_QUERY));
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(SENDER, mail.getSender());
            jsonObject.accumulate(SUBJECT, mail.getSubject());
            jsonObject.accumulate(TEXT, mail.getText());
            String json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            RestTask task = new RestTask(this, ACTION_QUERY);
            task.execute(httpPost);
            progress = ProgressDialog.show(this, "Send process request to server...", "Waiting For Results...", true);
        } catch (Exception e) {
            Log.e("sessionRequest", e.getMessage());
        }

    }


    private void queryRequest() {
        try {
            HttpGet httpGet = new HttpGet(new URI(URL + idRequest ));
            RestTask task = new RestTask(this, ACTION_GET);
            task.execute(httpGet);
            progress = ProgressDialog.show(this, "Getting status mail ...", "Waiting For Classification...", true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        String classification;

        @Override
        public void onReceive(Context context, Intent intent) {

            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            JSONObject jsonObj;
            if(intent.getAction().equalsIgnoreCase(ACTION_QUERY)) {

                try {
                    jsonObj = new JSONObject(response);
                    req = new Request(
                            Integer.parseInt(jsonObj.getString(ID_REQ)),
                            jsonObj.getString(TYPE_REQ),
                            jsonObj.getString(STATE_REQ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                idRequest = req.getId();
                Toast.makeText(context, "Request sended to server. ID request:" + req.getId(), Toast.LENGTH_LONG).show();

            }else if(intent.getAction().equalsIgnoreCase(ACTION_GET)){
            try {
                jsonObj = new JSONObject(response);
                classification = jsonObj.getString(CLASSIFICATION);
                if(classification!=null)
                    Toast.makeText(context, "Mail Classification: " + classification, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Mail Classification not ready!", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        }
    };


}
