package com.yoavgibri.myincome.FireBase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yoavgibri.myincome.BuildConfig;
import com.yoavgibri.myincome.Models.Client;
import com.yoavgibri.myincome.Models.Session;
import com.yoavgibri.myincome.Models.SessionType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.yoavgibri.myincome.App.database;

/**
 * Created by Yoav on 25/02/17.
 */

public class FBHelper {

    private static final String REF_SESSIONS = "sessions";
    private static final String REF_CLIENTS = "clients";
    private static final String REF_TYPES = "types";
    private static final String REF_TOTAL_AMOUNTS = "total_amounts";
    private static final String REF_TOTAL_UNPAID_AMOUNTS = "total_unpaid_amounts";
    private static final String REF_TOTAL_COUNTS = "total_counts";
    public final DatabaseReference refUser;
    public final DatabaseReference refTypes;
    public final DatabaseReference refClients;

    private Context context;
    public static String currentYear;
    public static String currentMonth;

    public ArrayList<Client> currentMonthClients;
    public ArrayList<SessionType> currentMonthTypes;
    public Map<String, Integer> currentYearTotalAmounts;
    public Map<String, Integer> currentYearTotalUnpaidAmounts;
    public HashMap<String, Integer> currentYearTotalCounts;
    public ArrayList<String> allClients;
    public ArrayList<String> allSessionTypes;
    private OnCurrentMonthSessionsValueEventListener listenerCurrentMonthSessions;
    private Calendar cal;
    private OnClientsValueEventListener listenerClientsValueEvent;
    private OnTypesValueEventListener listenerTypesValueEvent;

    public FBHelper(Context context) {
        this.context = context;

//        String user = BuildConfig.DEBUG ? "yoav_gibri" : "rotem_gibri";
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cal = Calendar.getInstance();
        refUser = database.getReference(user);
        refClients = refUser.child("clients");
        refTypes = refUser.child("types");

//        currentYear = String.valueOf(cal.get(Calendar.YEAR));
//        currentMonth = cal.get(Calendar.MONTH) < 9 ? "0" + (cal.get(Calendar.MONTH) + 1) : "" + (cal.get(Calendar.MONTH) + 1);

//        RefCurrentYear = refUser.child(currentYear);
//        RefCurrentMonth = RefCurrentYear.child(currentMonth);
//        refCurrentYearTotalAmounts = RefCurrentYear.child(REF_TOTAL_AMOUNTS);
//        refCurrentYearTotalCounts = RefCurrentYear.child(REF_TOTAL_COUNTS);
//        refCurrentMonthSessions = RefCurrentMonth.child(REF_SESSIONS);
//        refCurrentMonthClients = RefCurrentMonth.child(REF_CLIENTS);
//        refCurrentMonthTypes = RefCurrentMonth.child(REF_TYPES);

        initListeners();
        initValueEventsByDate(cal);
    }

    private DatabaseReference getYearRefByDate(Calendar cal) {
        return refUser.child(String.valueOf(cal.get(Calendar.YEAR)));
    }

    public DatabaseReference getYearTotalAmountsRefByDate(Calendar cal) {
        return getYearRefByDate(cal).child(REF_TOTAL_AMOUNTS);
    }

    public DatabaseReference getYearTotalUnpaidAmountsRefByDate(Calendar cal) {
        return getYearRefByDate(cal).child(REF_TOTAL_UNPAID_AMOUNTS);
    }

    public DatabaseReference getYearTotalCountsRefByDate(Calendar cal) {
        return getYearRefByDate(cal).child(REF_TOTAL_COUNTS);
    }


    private DatabaseReference getMonthRefByDate(Calendar cal) {
        return getYearRefByDate(cal).child(getCurrentMonthByDate(cal));
    }

    public DatabaseReference getMonthSessionsRefByDate(Calendar cal) {
        return getMonthRefByDate(cal).child(REF_SESSIONS);
    }

    public DatabaseReference getMonthClientsRefByDate(Calendar cal) {
        return getMonthRefByDate(cal).child(REF_CLIENTS);
    }

    public DatabaseReference getMonthTypesRefByDate(Calendar cal) {
        return getMonthRefByDate(cal).child(REF_TYPES);
    }


    private void initListeners() {
        if (context instanceof OnCurrentMonthSessionsValueEventListener) {
            listenerCurrentMonthSessions = (OnCurrentMonthSessionsValueEventListener) context;
        }
        if (context instanceof OnClientsValueEventListener) {
            listenerClientsValueEvent = (OnClientsValueEventListener) context;
        }
        if (context instanceof OnTypesValueEventListener) {
            listenerTypesValueEvent = (OnTypesValueEventListener) context;
        }
    }


    public void initValueEventsByDate(Calendar calendar) {
//        cal = calendar;
        currentMonth = getCurrentMonthByDate(calendar);

        getMonthSessionsRefByDate(calendar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Session> allSessions = new ArrayList<Session>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allSessions.add(snapshot.getValue(Session.class));
                }
                if (listenerCurrentMonthSessions != null)
                    listenerCurrentMonthSessions.OnCurrentMonthSessionsValueEvent(allSessions);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Jobs Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("JOBS QUERY", databaseError.getMessage());
            }
        });

        getMonthClientsRefByDate(calendar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentMonthClients = new ArrayList<Client>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentMonthClients.add(snapshot.getValue(Client.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "currentMontClients Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("CURRENT_M_CLIENTS QUERY", databaseError.getMessage());
            }
        });

        getMonthTypesRefByDate(calendar).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentMonthTypes = new ArrayList<SessionType>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentMonthTypes.add(snapshot.getValue(SessionType.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "currentMontTypes Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("CURRENT_M_TYPES QUERY", databaseError.getMessage());
            }
        });

        getYearTotalAmountsRefByDate(calendar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentYearTotalAmounts = new HashMap<String, Integer>(12);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentYearTotalAmounts.put(snapshot.getKey(), snapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "refCurrentYearTotalAmounts Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("CURRENT_Y_AMOUNTS QUERY", databaseError.getMessage());
            }
        });

        getYearTotalUnpaidAmountsRefByDate(calendar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentYearTotalUnpaidAmounts = new HashMap<String, Integer>(12);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentYearTotalUnpaidAmounts.put(snapshot.getKey(), snapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "refCurrentYearTotalAmounts Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("CURRENT_Y_AMOUNTS QUERY", databaseError.getMessage());
            }
        });

        getYearTotalCountsRefByDate(calendar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentYearTotalCounts = new HashMap<String, Integer>(12);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentYearTotalCounts.put(snapshot.getKey(), snapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "refCurrentYearTotalCounts Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("CURRENT_Y_COUNTS QUERY", databaseError.getMessage());
            }
        });

        refClients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allClients = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allClients.add(snapshot.getValue(String.class));
                }
                if (listenerClientsValueEvent != null)
                    listenerClientsValueEvent.OnClientsValueEvent(allClients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "refClients Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("ALL_CLIENTS QUERY", databaseError.getMessage());
            }
        });

        refTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSessionTypes = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allSessionTypes.add(snapshot.getValue(String.class));
                }
                if (listenerTypesValueEvent != null)
                    listenerTypesValueEvent.OnTypesValueEvent(allSessionTypes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "refTypes Query Has Failed", Toast.LENGTH_SHORT).show();
                Log.d("ALL_SESSION_TYPES QUERY", databaseError.getMessage());
            }
        });
    }

    @NonNull
    private String getCurrentMonthByDate(Calendar cal) {
        return cal.get(Calendar.MONTH) < 9 ? "0" + (cal.get(Calendar.MONTH) + 1) : "" + (cal.get(Calendar.MONTH) + 1);
    }


// region Interfaces Listeners


    public interface OnCurrentMonthSessionsValueEventListener {
        void OnCurrentMonthSessionsValueEvent(ArrayList<Session> sessions);
    }

    public interface OnClientsValueEventListener {
        void OnClientsValueEvent(ArrayList<String> allClients);
    }

    public interface OnTypesValueEventListener {
        void OnTypesValueEvent(ArrayList<String> allTypes);
    }


//endregion

}
