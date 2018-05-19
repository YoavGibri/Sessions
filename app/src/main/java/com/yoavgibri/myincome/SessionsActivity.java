package com.yoavgibri.myincome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavgibri.myincome.Adapters.SessionsArrayAdapter;
import com.yoavgibri.myincome.FireBase.FBHelper;
import com.yoavgibri.myincome.Models.Session;

import java.util.ArrayList;
import java.util.Calendar;

import static com.yoavgibri.myincome.Fragments.StatsFragment.TIME_IN_MILLIS;

public class SessionsActivity extends AppCompatActivity {

    private FBHelper fbHelper;
    private SessionsArrayAdapter jobArrayAdapter;
    private ListView listViewJobs;
    private Calendar selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initMembers();
        initData();
        initEvents();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    private void initMembers() {
        listViewJobs = findViewById(R.id.listViewJobs);
    }

    private void initData() {
        selectedMonth = Calendar.getInstance();
        if (getIntent() != null) {
            long calMillis = getIntent().getLongExtra(TIME_IN_MILLIS, selectedMonth.getTimeInMillis());
            selectedMonth.setTimeInMillis(calMillis);
        }

        String title = getResources().getStringArray(R.array.months)[selectedMonth.get(Calendar.MONTH)]
//                + " " + selectedMonth.get(Calendar.YEAR)
                + "'s Sessions";
        setTitle(Utils.convert(this, title));

        jobArrayAdapter = new SessionsArrayAdapter(this, R.layout.sessions_list_row, new ArrayList());
        listViewJobs.setAdapter(jobArrayAdapter);

        fbHelper = new FBHelper(this);
    }


    private void initEvents() {

        fbHelper.getMonthSessionsRefByDate(selectedMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Session> allSessions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Session session = snapshot.getValue(Session.class);
                    if (session != null) {
                        allSessions.add(session);
                    }
                }

                jobArrayAdapter.updateList(allSessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SessionsActivity.this, "Jobs Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("JOBS QUERY", databaseError.getMessage());
            }
        });

    }

}
