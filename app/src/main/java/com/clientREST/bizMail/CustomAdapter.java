package com.clientREST.bizMail;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.net.URI;
import java.util.List;

/**
 * Created by daniele on 06/06/16.
 */

public class CustomAdapter extends ArrayAdapter<Mail> {

    private int resource;
    private LayoutInflater inflater;
    Bundle bundle = new Bundle();
    Context mContext;

    //private static final String URL_QUERY = "http://192.168.1.17:8080/mail/query";;
    //private static final String URL_UPDATE_HAM = "http://192.168.1.17:8080/mail/updateHam";
    //private static final String URL_UPDATE_SPAM = "http://192.168.1.17:8080/mail/updateSpam";
    private static final String URL_QUERY = "http://192.168.43.105:8080/mail/query";;
    private static final String URL_UPDATE_HAM = "http://192.168.43.105:8080/mail/updateHam";
    private static final String URL_UPDATE_SPAM = "http://192.168.43.105:8080/mail/updateSpam";

    private static final String ACTION_UPDATE = "UPDATE_MODEL";
    private static final String ACTION_QUERY = "QUERY";

    private static final String SENDER = "sender";
    private static final String SUBJECT = "subject";
    private static final String TEXT = "text";

    public ProgressDialog progress;

    public CustomAdapter(Context context, int resourceId, List<Mail> objects) {
        super(context, resourceId, objects);
        this.mContext = context;
        resource = resourceId;
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View v, ViewGroup parent) {

        final Mail mail = getItem(position);

        final ViewHolder holder;

        if (v == null) {
            v = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();

            holder._sourceAddress = (TextView) v.findViewById(R.id.fromMail);
            holder._subject = (TextView) v.findViewById(R.id.object);
            holder._mailText = (TextView) v.findViewById(R.id.mailText);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder._sourceAddress.setEnabled(true);
        holder._sourceAddress.setText(mail.getSender());
        holder._subject.setEnabled(true);
        holder._subject.setText(mail.getSubject());
        holder._mailText.setEnabled(true);
        holder._mailText.setText(mail.getText());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Mail From:\n" + mail.getSender());
                builder.setMessage("Subject: " + mail.getSubject() + "\n" + mail.getText());
                builder.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        queryMail(mail);
                    }
                });
                if (mail.isSpam()) {
                    builder.setNegativeButton("SET AS HAM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateModel(mail);
                        }
                    });
                }else {
                    builder.setNegativeButton("SET AS SPAM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateModel(mail);
                        }
                    });
                }
                builder.show();
            }
        });
        return v;
    }


    private static class ViewHolder {
        TextView _sourceAddress;
        TextView _subject;
        TextView _mailText;
    }

    private void updateModel(Mail mail) {
        try {
            HttpPost httpPost;
            if(mail.isSpam())
                httpPost = new HttpPost(new URI(URL_UPDATE_HAM));
            else
                httpPost = new HttpPost(new URI(URL_UPDATE_SPAM));

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(SENDER, mail.getSender());
            jsonObject.accumulate(SUBJECT, mail.getSubject());
            jsonObject.accumulate(TEXT, mail.getText());
            String json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            RestTask task = new RestTask(mContext, ACTION_UPDATE);
            task.execute(httpPost);
            progress = ProgressDialog.show(mContext, "Update Model", "Waiting for response...", true);
        } catch (Exception e) {
            Log.e("sessionRequest", e.getMessage());
        }

    }

    //Tale metodo quando il server effettuerà il deploy su YARN
    //dovrà essere eliminato.
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
            RestTask task = new RestTask(mContext, ACTION_QUERY);
            task.execute(httpPost);
            progress = ProgressDialog.show(mContext, "Query Mail", "Waiting for classification...", true);
        } catch (Exception e) {
            Log.e("sessionRequest", e.getMessage());
        }

    }

}