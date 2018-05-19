package com.yoavgibri.myincome;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.yoavgibri.myincome.Adapters.SessionsArrayAdapter;
import com.yoavgibri.myincome.FireBase.FBHelper;
import com.yoavgibri.myincome.Fragments.StatsFragment;
import com.yoavgibri.myincome.Models.Session;
import com.yoavgibri.myincome.SQLite.DBHelper;
import com.yoavgibri.myincome.SQLite.JobsDBHelper;

import java.util.ArrayList;

import pro.alexzaitsev.freepager.library.view.infinite.Constants;
import pro.alexzaitsev.freepager.library.view.infinite.InfiniteHorizontalPager;
import pro.alexzaitsev.freepager.library.view.infinite.ViewFactory;

public class MainActivity extends AppCompatActivity implements FBHelper.OnCurrentMonthSessionsValueEventListener, ViewPager.OnPageChangeListener, StatsFragment.OnFragmentInteractionListener {

    //region Members

    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 420;
    private static final int REQUEST_CODE_CHOOSE_CALENDAR = 1;
    public static final int REQUEST_CODE_INSERT_SESSION = 2;
    public static final String SESSION_ACTIVITY_STATE = "jobActivityState";
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE_READ_CALENDAR = 11;
    private static final int PERMISSION_REQUEST_CODE_SEND_SMS = 12;
    private static final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 13;
    public static final String IS_CRASHLYTICS_ON = "isCrashLyticsOn";

    private static String[] EVENT_PROJECTION;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ListView ListPreviousMonths;
    private JobsDBHelper jobsDBHelper;
    public FBHelper fbHelper;
    private SessionsArrayAdapter SessionsArrayAdapter;

    private ImageView imageViewLoading;
    private int mCurrentHorizontalIndex = 0;
    private ViewGroup mView;
    private ViewPager viewPagerStats;
    private SharedPreferences sp;


    //endregion

    //region Inits


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (BuildConfig.DEBUG) {
            setTitle(getTitle() + "(Debug)");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        initMembers();
        initData();
        initEvents();
        checkPermissions();

//        queryJobs();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE_READ_CALENDAR);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE_SEND_SMS);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }


    private void initMembers() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ListPreviousMonths = (ListView) findViewById(R.id.previousMonths);
        imageViewLoading = (ImageView) findViewById(R.id.imageViewLoading);
        viewPagerStats = (ViewPager) findViewById(R.id.viewPagerStats);
    }


    private void initData() {
        setSupportActionBar(toolbar);

        ((AnimationDrawable) imageViewLoading.getDrawable()).start();

//        EVENT_PROJECTION = new String[]{
//                CalendarContract.Events.CALENDAR_ID,                      // 0
//                CalendarContract.Events.TITLE,                            // 1
//                CalendarContract.Events.DESCRIPTION,                      // 2
//                CalendarContract.Events.DTSTART                           // 3
//        };
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        jobsDBHelper = new JobsDBHelper(dbHelper);
        fbHelper = new FBHelper(this);

        SessionsArrayAdapter = new SessionsArrayAdapter(this, R.layout.sessions_list_row, new ArrayList());
        ListPreviousMonths.setAdapter(SessionsArrayAdapter);

        PagerAdapterStats pagerAdapter = new PagerAdapterStats(getSupportFragmentManager(), Utils.isHebrewLocale(this)); //
        viewPagerStats.setAdapter(pagerAdapter);
        viewPagerStats.setCurrentItem(Utils.isHebrewLocale(this) ? 0 : 12);
//        verticalPager.setFactory(mVerticalViewFactory);
//        verticalPager.instantiate();
//        mView = verticalPager;
    }


    private void initEvents() {
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Intent sessionIntent = new Intent(MainActivity.this, addUpdateSessionActivity.class);
                    sessionIntent.putExtra(SESSION_ACTIVITY_STATE, "New Session");
                    startActivityForResult(sessionIntent, REQUEST_CODE_INSERT_SESSION);
                }
            });
    }


//endregion

    //region Methods


    private void queryWorkCalendar() {
        //check for permissions:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR);
        } else {
            // Submit the query and get a Cursor object back.
            Cursor cur = null;
            ContentResolver cr = getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
            String[] selectionArgs = new String[]{"2"}; //my calendar id

            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            DatabaseUtils.dumpCursor(cur);
        }
    }


//endregion

    //region CallBacks

    //region OptionMenu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemDebug = menu.findItem(R.id.action_start_debug);
        if (BuildConfig.DEBUG) {
            if (sp.getBoolean(IS_CRASHLYTICS_ON, false)) {
                itemDebug.setIcon(R.drawable.ic_action_bell);
            }
        } else {
            itemDebug.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
                startActivity(intent);
                return true;
//            case R.id.action_all_jobs:
//                Intent jobsIntent = new Intent(this, SessionsActivity.class);
//                startActivity(jobsIntent);
//                return true;
            case R.id.action_delete_database:
                new AlertDialog.Builder(this).setTitle(R.string.delete_all_sessions)
                        .setMessage(R.string.delete_all_sessions_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fbHelper.refUser.setValue("");
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            case R.id.action_start_debug:
                if (sp.getBoolean(IS_CRASHLYTICS_ON, false)) {
                    item.setIcon(R.drawable.ic_action_debug);
                    sp.edit().putBoolean(IS_CRASHLYTICS_ON, false).apply();
                } else {
                    ((App) getApplication()).startCrashLytics();
                    item.setIcon(R.drawable.ic_action_bell);
                    sp.edit().putBoolean(IS_CRASHLYTICS_ON, true).apply();
                }
                return true;
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intentLogIn = new Intent(this, LoginActivity.class);
//                intentLogIn.putExtra("SignOut", true);
                startActivity(intentLogIn);
                finish();
                return true;

//            case R.id.action_log:
//                Utils.logToFile("hello");
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_CALENDAR) {
            if (resultCode == RESULT_OK) {

            }
        }
    }

    //endregion

    @Override
    public void OnCurrentMonthSessionsValueEvent(ArrayList<Session> sessions) {
        SessionsArrayAdapter.updateList(sessions);
        imageViewLoading.setVisibility(View.GONE);
        ((AnimationDrawable) imageViewLoading.getDrawable()).stop();
//                updatePieChart(sessions);
    }


    @Override
    public void onFragmentStatsClick(Uri uri) {

    }

//endregion

    //region Pager

    private ViewFactory mVerticalViewFactory = new ViewFactory() {

        static final int VIEWS_TO_INITIALIZE_COUNT = 3;

        private int mViewsToInitializeLeft = VIEWS_TO_INITIALIZE_COUNT;

        @Override
        public View makeView(int vertical, int horizontal) {
            int normalizedHorizontal = 0;
            if (mViewsToInitializeLeft > 0) {
                mViewsToInitializeLeft--;
            } else {
                normalizedHorizontal = mCurrentHorizontalIndex;
            }
            InfiniteHorizontalPager pager = new InfiniteHorizontalPager(MainActivity.this, vertical, normalizedHorizontal, mHorizontalViewFactory);
            pager.getPager().addOnPageChangeListener(MainActivity.this);
            return pager.getPager();
        }
    };

    private ViewFactory mHorizontalViewFactory = new ViewFactory() {

        @Override
        public View makeView(int vertical, int horizontal) {
            Button btn = new Button(MainActivity.this);
            btn.setText("Horizontal " + horizontal + "\nVertical " + vertical);
//            btn.setBackgroundColor(mBgColor);
            return btn;
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        mCurrentHorizontalIndex = i - Constants.START_INDEX;

        ViewPager pagerTop = (ViewPager) mView.getChildAt(Constants.TOP_PAGE_INDEX);
        ViewPager pagerBottom = (ViewPager) mView.getChildAt(Constants.BOTTOM_PAGE_INDEX);
        synchronizePager(pagerTop, i);
        synchronizePager(pagerBottom, i);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void synchronizePager(ViewPager pager, int i) {
        pager.setOnPageChangeListener(null);
        pager.setCurrentItem(i);
        pager.setOnPageChangeListener(this);
    }


    //endregion
}
