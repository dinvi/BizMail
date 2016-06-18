package com.clientREST.bizMail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpamActivity extends AppCompatActivity {

    private static final String TAG = "SpamActivity";

    private static final String ACTION_UPDATE = "UPDATE_MODEL";
    private static final String ACTION_QUERY = "QUERY";

    private static final String CLASSIFICATION = "classification";

    private static final String ID_REQ = "id";
    private static final String TYPE_REQ = "type";
    private static final String STATE_REQ = "state";


    CustomAdapter adapter;
    List<Mail> spam_mail_list = new ArrayList<>();
    Request req;


    Mail m1 = new Mail("IA@rogers.com", "Market Internet Access",
            "Premium Internet Access for only $14.95 per month or less!" +
                    "Earn $1 per Subscriber per month Go To:" +
                    "http://new.isp.50megs.com", "SPAM");

    Mail m2 = new Mail("info@smokesdirect.com", "Cheap Fags",
            "If you are fed up of being 'ripped off' by the British government every time you buy your tobacco," +
                    " then you should visit our website, where you can now buy 4 cartons of cigarettes, " +
                    "or 40 pouches of rolling tobacco from as little as 170 Euros (approx 105 pounds), " +
                    "inclusive of delivery by registered air mail from our office in Spain.", "SPAM");

    Mail m3 = new Mail("safety33o@l5.newnamedns.com", "ADV: Lowest life insurance rates available!",
            "Lowest rates available for term life insurance! Take a moment and fill out" +
                    "our online form to see the low rate you qualify for. Save up to 70% " +
                    "from regular rates! Smokers accepted! http://www.newnamedns.com/termlife/" +
                    "Representing quality nationwide carriers. Act now!", "SPAM");

    Mail m4 = new Mail("giqq9dosuurty99@excite.com", "Play a Hilarious Phone Prank",
            "Wind up your mates today! Please visit http://ukprankcalls.com", "SPAM");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        spam_mail_list.add(m1);
        spam_mail_list.add(m2);
        spam_mail_list.add(m3);
        spam_mail_list.add(m4);

        ListView listView = (ListView) findViewById(R.id.listView);

        if (spam_mail_list.size() > 0) {
            adapter = new CustomAdapter(SpamActivity.this, R.layout.listview_layout, spam_mail_list);
            listView.setAdapter(adapter);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(receiver, new IntentFilter(ACTION_QUERY));
        this.registerReceiver(receiver, new IntentFilter(ACTION_UPDATE));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }

    /**
     * Our Broadcast Receiver. We get notified that the data is ready, and then we
     * put the content we receive (a string) into the TextView.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // clear the progress indicator
            if (adapter.progress != null) {
                adapter.progress.dismiss();
            }

            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            String classification = null;
            String state = null;
            JSONObject jsonObj = null;

            if(intent.getAction().equalsIgnoreCase(ACTION_QUERY)) {

                try {
                    jsonObj = new JSONObject(response);
                    //classification = jsonObj.getString(CLASSIFICATION);
                    req = new Request(
                            Integer.parseInt(jsonObj.getString(ID_REQ)),
                            jsonObj.getString(TYPE_REQ),
                            jsonObj.getString(STATE_REQ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(SpamActivity.this);
                builder.setTitle("Query Mail");
                builder.setMessage("Request received:\n"+req.toString());
                builder.setPositiveButton("BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                //Toast.makeText(context, "Mail Classification: " + classification, Toast.LENGTH_LONG).show();
            }else if(intent.getAction().equalsIgnoreCase(ACTION_UPDATE)){
                try {
                    jsonObj = new JSONObject(response);
                    state = jsonObj.getString(STATE_REQ);
                    if(state.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(context, "Update Model Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Update Model Failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            Log.i(TAG, "RESPONSE = " + response);

        }
    };
}
