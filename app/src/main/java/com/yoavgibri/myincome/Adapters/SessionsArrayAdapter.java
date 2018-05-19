package com.yoavgibri.myincome.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yoavgibri.myincome.Models.Session;
import com.yoavgibri.myincome.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Yoav on 25/02/17.
 */

public class SessionsArrayAdapter extends ArrayAdapter<Session> {

    public SessionsArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Session currentSession = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.sessions_list_row, parent, false);

            viewHolder.clientName = convertView.findViewById(R.id.clientName);
            viewHolder.type = convertView.findViewById(R.id.sessionType);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.amount = convertView.findViewById(R.id.amount);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.clientName.setText(currentSession.clientName);
        viewHolder.type.setText(currentSession.type);

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(currentSession.insertTime);
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        String date = formatter.format(calendar.getTime());

        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
        String currentMonth = "0" + (calendar.get(Calendar.MONTH) + 1);
        String date = currentSession.dayOfMonth + "/" + currentMonth + "/" + currentYear;
        viewHolder.date.setText(date);

        viewHolder.amount.setText("₪" + currentSession.amount);

        return convertView;

    }

    public void updateList (List<Session> sessions) {
        if (sessions != null) {
            clear();
            addAll(sessions);
        }
    }

    private static class ViewHolder {
        TextView clientName;
        TextView type;
        TextView date;
        TextView amount;
    }


}
