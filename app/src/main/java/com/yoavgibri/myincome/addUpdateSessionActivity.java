package com.yoavgibri.myincome;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yoavgibri.myincome.FireBase.FBHelper;
import com.yoavgibri.myincome.SessionDatePickerDialogFragment.OnDateSelectListener;
import com.yoavgibri.myincome.Models.Client;
import com.yoavgibri.myincome.Models.Session;
import com.yoavgibri.myincome.Models.SessionType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addUpdateSessionActivity extends AppCompatActivity implements OnDateSelectListener, FBHelper.OnClientsValueEventListener,
        FBHelper.OnTypesValueEventListener {

    private static final String TAG = "addUpdateSessionActivity";
    public static final String DATE_PIKER_FRAGMENT_TAG = "DatePikerFragment";

//    private final int STATE_NEW_JOB = 1;
//    private final int STATE_FROM_CALENDAR = 2;
//    private final int STATE_EDIT_JOB = 3;

    private AutoCompleteTextView acTextViewClientName;
    private AppCompatSpinner spinnerSessionTypes;
    private TextInputEditText editTextComment;
    private TextInputEditText editTextPaymentAmount;
    private TextView textViewJobTimeDate;
    private CheckBox checkBoxIsPaid;
    private SessionDatePickerDialogFragment datePickerFragment;

    //DB HELPERS:
//    private JobsDBHelper jobsHelper;
//    private ClientsDBHelper clientsHelper;
//    private JobTypesDBHelper jobTypesHelper;

    private Calendar selectedDate;
    private SimpleDateFormat formatter;
    private FBHelper fbHelper;
    private TextInputEditText editTextSessionTypes;
    private ImageButton imageButtonSessionTypes;
    private TextInputLayout layoutSpinnerSessionTypes;
    private ArrayAdapter<String> arrayAdapterClientName;
    private ArrayAdapter<String> arrayAdapterSessionTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_session);
        setTitle(Utils.convert(this, getIntent().getStringExtra(MainActivity.SESSION_ACTIVITY_STATE)));

        initMembers();
        initData();
        initEvents();
    }

    private void initMembers() {
        acTextViewClientName = findViewById(R.id.acTextViewClientName);
        editTextComment = findViewById(R.id.editTextComment);
        editTextPaymentAmount = findViewById(R.id.editTextPaymentAmount);
        textViewJobTimeDate = findViewById(R.id.TextViewJobTimeDate);

        spinnerSessionTypes = findViewById(R.id.spinnerSessionTypes);
        editTextSessionTypes = findViewById(R.id.editTextSessionTypes);
        layoutSpinnerSessionTypes = findViewById(R.id.layoutSpinnerSessionTypes);
        checkBoxIsPaid = findViewById(R.id.checkBoxIsPaid);
        imageButtonSessionTypes = findViewById(R.id.imageButtonSessionTypes);
    }

    private void initData() {
        //init DataBase:
//        DBHelper dbHelper = new DBHelper(this);
//        clientsHelper = new ClientsDBHelper(this, dbHelper);
//        jobTypesHelper = new JobTypesDBHelper(this, dbHelper);
//        jobsHelper = new JobsDBHelper(dbHelper);

        fbHelper = new FBHelper(this);

        formatter = new SimpleDateFormat("dd.MM.yy");

        arrayAdapterClientName = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, new ArrayList<String>());
        acTextViewClientName.setAdapter(arrayAdapterClientName);

        arrayAdapterSessionTypes = new ArrayAdapter<String>(addUpdateSessionActivity.this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        arrayAdapterSessionTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSessionTypes.setAdapter(arrayAdapterSessionTypes);

        datePickerFragment = new SessionDatePickerDialogFragment();
//        if (getIntent().hasExtra(CalendarSessions.EXTRA_CLIENT_NAME) || getIntent().hasExtra(CalendarSessions.EXTRA_DATE)) {
        String action = getIntent().getAction();
        if (action != null && !action.equals("")) {
            int index = action.indexOf(",");
            String clientName = "";
            if (index != -1) {
                clientName = action.substring(0, index);
                String payment = action.substring(index + 1, action.length());
                payment = payment.trim();
                editTextPaymentAmount.setText(payment);
            } else {
                clientName = action;
            }
            acTextViewClientName.setText(clientName);
        }
        OnDateSelect(Calendar.getInstance()); //set date to edittext
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Toast.makeText(this, "on new intent", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> makeSpinnerList(ArrayList<String> allSessionTypes) {
        ArrayList<String> newAllSessionTypes = new ArrayList<>();
        if (allSessionTypes != null) newAllSessionTypes.addAll(allSessionTypes);

        newAllSessionTypes.add(0, Utils.convert(this, R.string.session_type));
        newAllSessionTypes.add(Utils.convert(this, R.string.new_session_type));
        return newAllSessionTypes;
    }

    private void initEvents() {

        acTextViewClientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, "acTextView onItemSelected: " + "adapterView " + adapterView + ", view " + view + ", i " + i + ", l " + l);
            }
        });

        acTextViewClientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((TextInputLayout) findViewById(R.id.textInputLayoutClientName)).setErrorEnabled(false);
            }
        });

        editTextPaymentAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((TextInputLayout) findViewById(R.id.textInputLayoutPaymentAmount)).setErrorEnabled(false);
            }
        });

        spinnerSessionTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextInputLayout) findViewById(R.id.layoutSpinnerSessionTypes)).setErrorEnabled(false);
                TextView textview = spinnerSessionTypes.getSelectedView().findViewById(android.R.id.text1);
                textview.setTextColor(Color.BLACK);
                if (textview.getText().toString().equals(getString(R.string.new_session_type))) {
                    //  switch Spinner to EditText:
                    layoutSpinnerSessionTypes.setHint(getString(R.string.new_session_type));
                    spinnerSessionTypes.setVisibility(View.GONE);
                    editTextSessionTypes.setVisibility(View.VISIBLE);
                    imageButtonSessionTypes.setVisibility(View.VISIBLE);
                    editTextSessionTypes.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        textViewJobTimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerFragment.show(getSupportFragmentManager(), DATE_PIKER_FRAGMENT_TAG);
            }
        });

        imageButtonSessionTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  switch EditText to Spinner:
                layoutSpinnerSessionTypes.setHint("");
                editTextSessionTypes.setVisibility(View.GONE);
                imageButtonSessionTypes.setVisibility(View.GONE);
                spinnerSessionTypes.setVisibility(View.VISIBLE);
                spinnerSessionTypes.setSelection(0);
            }
        });

    }

    @Override
    public void OnDateSelect(Calendar selectedDate) {
        textViewJobTimeDate.setText(formatter.format(selectedDate.getTime()));
        this.selectedDate = selectedDate;
        fbHelper.initValueEventsByDate(selectedDate);
    }

    @Override
    public void OnClientsValueEvent(ArrayList<String> allClients) {
//maybe its better to don't pass the arrayList, and get it from fBHelper... maybe
        if (arrayAdapterClientName.getCount() == 0) {
            arrayAdapterClientName.addAll(fbHelper.allClients);
        }
        arrayAdapterClientName.notifyDataSetChanged();
    }

    @Override
    public void OnTypesValueEvent(ArrayList<String> allTypes) {
        if (arrayAdapterSessionTypes.getCount() == 0) {
            arrayAdapterSessionTypes.addAll(makeSpinnerList(fbHelper.allSessionTypes));
        }
        arrayAdapterSessionTypes.notifyDataSetChanged();
    }

    private Session getSessionDetailsFromForm() {
        boolean isMissing = false;
        if (acTextViewClientName.getText().toString().equals("")) {
            ((TextInputLayout) findViewById(R.id.textInputLayoutClientName)).setError(getString(R.string.missing_client));
            isMissing = true;
        }
        if (spinnerSessionTypes.getVisibility() == View.VISIBLE && spinnerSessionTypes.getSelectedItemPosition() == 0
                || editTextSessionTypes.getVisibility() == View.VISIBLE && editTextSessionTypes.getText().toString().trim().length() == 0) {
            layoutSpinnerSessionTypes.setError(getString(R.string.missing_job_type));
            ((TextView) spinnerSessionTypes.getSelectedView().findViewById(android.R.id.text1)).setTextColor(Color.RED);
            isMissing = true;
        }
        if (editTextPaymentAmount.getText().toString().equals("")) {
            ((TextInputLayout) findViewById(R.id.textInputLayoutPaymentAmount)).setError(getString(R.string.missing_amount));
            isMissing = true;
        }

        if (isMissing)
            return null;

//        Cursor c = (Cursor) spinnerSessionTypes.getSelectedItem();

        Session newJob = new Session();
        newJob.clientName = acTextViewClientName.getText().toString();
        newJob.type = spinnerSessionTypes.getVisibility() == View.VISIBLE ?
                (String) spinnerSessionTypes.getSelectedItem() : editTextSessionTypes.getText().toString();
        newJob.dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);
        newJob.comment = editTextComment.getText().toString();
        newJob.amount = Integer.parseInt(editTextPaymentAmount.getText().toString());
        newJob.isPaid = checkBoxIsPaid.isChecked();
        return newJob;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_job_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                saveSessionAndFinish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSessionAndFinish() {
        //  init:
        Boolean isAllOk = true;
        Client currentClient = null;
        SessionType currentSessionType = null;
        int currentMonthTotalAmount = 0;
        int currentMonthTotalUnpaidAmount = 0;
        int currentMonthTotalCount = 0;

        //  get the details:
        Session newSession = getSessionDetailsFromForm();

        if (newSession != null) {

            //  search client details from DB:

            if (fbHelper.currentMonthClients != null) {
                for (Client client : fbHelper.currentMonthClients) {
                    if (client.clientName.equals(newSession.clientName)) {
                        currentClient = client;
                        currentClient.totalAmount += newSession.amount;
                        currentClient.totalCount++;
                        break;
                    }
                }
                if (currentClient == null) // if it's the client's first entry
                    currentClient = new Client(newSession.clientName, newSession.amount, 1);
            } else {
                isAllOk = false;
            }


            //  search type details from DB:
            if (fbHelper.currentMonthTypes != null) {
                for (SessionType type : fbHelper.currentMonthTypes) {
                    if (type.type.equals(newSession.type)) {
                        currentSessionType = type;
                        currentSessionType.totalAmount += newSession.amount;
                        currentSessionType.totalCount++;
                        break;
                    }
                }
                if (currentSessionType == null) // if it's the type's first entry
                    currentSessionType = new SessionType(newSession.type, newSession.amount, 1);
            } else {
                isAllOk = false;
            }

            //  get current year's total amount and total counts:
            Map<String, Integer> currentMonthTotalAmountMap = new HashMap<String, Integer>();
            if (fbHelper.currentYearTotalAmounts != null) {
                if (fbHelper.currentYearTotalAmounts.get(FBHelper.currentMonth) != null) {
                    currentMonthTotalAmount = fbHelper.currentYearTotalAmounts.get(FBHelper.currentMonth);
                }
                currentMonthTotalAmount += newSession.amount;
                currentMonthTotalAmountMap.put(FBHelper.currentMonth, currentMonthTotalAmount);
            } else {
                isAllOk = false;
            }

            if (!newSession.isPaid) {
                //  get current year's total unpaid amount and total counts:
                Map<String, Integer> currentMonthTotalUnpaidAmountMap = new HashMap<String, Integer>();
                if (fbHelper.currentYearTotalUnpaidAmounts != null) {
                    if (fbHelper.currentYearTotalUnpaidAmounts.get(FBHelper.currentMonth) != null) {
                        currentMonthTotalUnpaidAmount = fbHelper.currentYearTotalUnpaidAmounts.get(FBHelper.currentMonth);
                    }
                    currentMonthTotalUnpaidAmount += newSession.amount;
                    currentMonthTotalUnpaidAmountMap.put(FBHelper.currentMonth, currentMonthTotalUnpaidAmount);
                } else {
                    isAllOk = false;
                }
            }



            Map<String, Integer> currentMonthTotalCountMap = new HashMap<String, Integer>();
            if (fbHelper.currentYearTotalCounts != null) {
                if (fbHelper.currentYearTotalCounts.get(FBHelper.currentMonth) != null) {
                    currentMonthTotalCount = fbHelper.currentYearTotalCounts.get(FBHelper.currentMonth);
                }
                currentMonthTotalCount++;
                currentMonthTotalCountMap.put(FBHelper.currentMonth, currentMonthTotalCount);
            } else {
                isAllOk = false;
            }

            // get all sessionTypes from DB
            if (fbHelper.allSessionTypes != null) {
                if (!fbHelper.allSessionTypes.contains(currentSessionType.type)) {
                    fbHelper.allSessionTypes.add(currentSessionType.type);
                }
            } else {
                isAllOk = false;
            }

            // get all clients from DB
            if (fbHelper.allClients != null) {
                if (!fbHelper.allClients.contains(currentClient.clientName)) {
                    fbHelper.allClients.add(currentClient.clientName);
                }
            } else {
                isAllOk = false;
            }

            //  insert info to DB

            if (isAllOk) {
                fbHelper.getMonthSessionsRefByDate(selectedDate).push().setValue(newSession);
                fbHelper.getMonthClientsRefByDate(selectedDate).child(currentClient.clientName).setValue(currentClient);
                fbHelper.getMonthTypesRefByDate(selectedDate).child(currentSessionType.type).setValue(currentSessionType);
                fbHelper.getYearTotalAmountsRefByDate(selectedDate).child(FBHelper.currentMonth).setValue(currentMonthTotalAmount);
//                fbHelper.getYearTotalAmountsRefByDate().child(FBHelper.currentMonth).setValue(currentMonthTotalAmountMap);
                fbHelper.getYearTotalCountsRefByDate(selectedDate).child(FBHelper.currentMonth).setValue(currentMonthTotalCount);
//                fbHelper.getYearTotalCountsRefByDate().child(FBHelper.currentMonth).setValue(currentMonthTotalCountMap);
                fbHelper.refTypes.setValue(fbHelper.allSessionTypes);
                fbHelper.refClients.setValue(fbHelper.allClients);
                finish();

            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.oops)
                        .setMessage(R.string.something_happened_cant_save_session)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }
    }


}
