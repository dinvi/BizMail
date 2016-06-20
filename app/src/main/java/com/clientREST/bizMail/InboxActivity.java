package com.clientREST.bizMail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    private static final String TAG = "InboxActivity";

    private static final String ACTION_UPDATE = "UPDATE_MODEL";
    private static final String ACTION_QUERY = "QUERY";

    private static final String CLASSIFICATION = "classification";

    private static final String ID_REQ = "id";
    private static final String TYPE_REQ = "type";
    private static final String STATE_REQ = "state";


    CustomAdapter adapter;
    List<Mail> ham_mail_list = new ArrayList<>();
    Request req;

    Mail m1 = new Mail("skip@pobox.com", "Speed",
            "If the frequency of my laptop's disk chirps are any indication, I'd say" +
                    "hammie is about 3-5x faster than SpamAssassin.", "HAM");
    Mail m2 = new Mail("skip@pobox.com", "SpamBayes Package",
            "Before we get too far down this road, what do people think of creating a" +
                    "spambayes package containing classifier and tokenizer?  This is just to" +
                    "minimize clutter in site-packages.", "HAM");
    Mail m3 = new Mail("skip@pobox.com", "Deleting duplicate spam before training?",
            "Because I get mail through several different email addresses, I frequently" +
                    "get duplicates (or triplicates or more-plicates) of various spam messages." +
                    "In saving spam for later analysis I haven't always been careful to avoid" +
                    "saving such duplicates.", "HAM");
    Mail m4 = new Mail("tim.one@comcast.net", "Ditching WordInfo",
            "> On my box the current system scores about 50 msgs per second (starting" +
                    "> in memory, of course)." +
                    "That was a guess.  Bothering to get a clock out, it was more like 80 per" +
                    "second.  See?  A 60% speedup without changing a thing <wink>.", "HAM");
    Mail m5 = new Mail("bkc@murkworks.com", "All Cap or Cap Word Subjects",
            "Just curious if subject line capitalization can be used as an indicator." +
                    "Either the percentage of characters that are caps.." +
                    "Or, percentage starting with a capital letter (if number of words > xx)", "HAM");
    Mail m6 = new Mail("d.invincibile@gmail.com", "Test", "Hi how are you?", "HAM");
    Mail m7 = new Mail("promo@youwin.com", "ReadMe", "Hi Daniele, visit our website www.promo.it for new promo. " +
            "Thank you and see you soon!", "HAM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ham_mail_list.add(m1);
        ham_mail_list.add(m2);
        ham_mail_list.add(m3);
        ham_mail_list.add(m4);
        ham_mail_list.add(m5);
        ham_mail_list.add(m6);
        ham_mail_list.add(m7);

        ListView listView = (ListView) findViewById(R.id.listView);


        if (ham_mail_list.size() > 0) {
            adapter = new CustomAdapter(InboxActivity.this, R.layout.listview_layout, ham_mail_list);
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
                    classification = jsonObj.getString(CLASSIFICATION);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, "Mail Classification: " + classification, Toast.LENGTH_LONG).show();
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
