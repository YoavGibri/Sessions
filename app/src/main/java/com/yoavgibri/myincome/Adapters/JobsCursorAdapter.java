package com.yoavgibri.myincome.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yoavgibri.myincome.R;
import com.yoavgibri.myincome.SQLite.ClientsDBHelper;
import com.yoavgibri.myincome.SQLite.JobsDBHelper;

/**
 * Created by Yoav on 16/12/16.
 */

public class JobsCursorAdapter extends CursorAdapter {

    public JobsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  LayoutInflater.from(context).inflate(R.layout.sessions_adapter_row, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView clientNameTextView = (TextView) view.findViewById(R.id.clientNameTextView);
        TextView amountTextView = (TextView) view.findViewById(R.id.amountTextView);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);

        String clientName = cursor.getString(cursor.getColumnIndex(ClientsDBHelper.CLIENT_NAME));
        String amount = cursor.getString(cursor.getColumnIndex(JobsDBHelper.PAYMENT_AMOUNT));
        String date = cursor.getString(cursor.getColumnIndex(JobsDBHelper.JOB_TIME));

        clientNameTextView.setText(clientName);
        amountTextView.setText(amount);
        dateTextView.setText(date);


    }
}
