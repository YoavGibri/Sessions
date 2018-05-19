package com.yoavgibri.myincome.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavgibri.myincome.FireBase.FBHelper;
import com.yoavgibri.myincome.MainActivity;
import com.yoavgibri.myincome.Models.Client;
import com.yoavgibri.myincome.Models.SessionType;
import com.yoavgibri.myincome.R;
import com.yoavgibri.myincome.SessionsActivity;
import com.yoavgibri.myincome.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TIME_IN_MILLIS = "timeInMillis";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView textViewDateTitle;
    private PieChart pieChartClients;
    private PieChart pieChartSessionTypes;
    private BarChart barChartMonths;
    private FBHelper fbHelper;
    private Calendar cal;
    private TextView textViewMonthIncome;
    private TextView textViewTargetIncome;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(Calendar cal) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putLong("calendar", cal.getTimeInMillis());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(getArguments().getLong("calendar"));
        } else cal = Calendar.getInstance();

        this.fbHelper = ((MainActivity) getActivity()).fbHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        textViewDateTitle = v.findViewById(R.id.textViewDateTitle);
        textViewMonthIncome = v.findViewById(R.id.textViewMonthIncome);
//        textViewMonthUnpaid = v.findViewById(R.id.textViewMonthUnpaid);
        textViewTargetIncome = v.findViewById(R.id.textViewTargetIncome);
        pieChartClients = v.findViewById(R.id.pieChartClients);
        pieChartSessionTypes = v.findViewById(R.id.pieChartSessionTypes);
        barChartMonths = v.findViewById(R.id.barChartMonths);
        TextView textViewSessionTypes = v.findViewById(R.id.textViewSessionTypes);
        textViewSessionTypes.setText(Utils.convert(getActivity(), textViewSessionTypes.getText().toString()));


        textViewMonthIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SessionsActivity.class);
                intent.putExtra(TIME_IN_MILLIS, cal.getTimeInMillis());
                startActivity(intent);
            }
        });

        getData();
        String dateTitle = getResources().getStringArray(R.array.months)[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR);
        textViewDateTitle.setText(dateTitle);

        return v;
    }

    private void getData() {
        // TODO: 19/04/17 start progresses here

        if (fbHelper != null) {
            fbHelper.getMonthClientsRefByDate(cal).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Client> clients = new ArrayList<Client>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Client client = snapshot.getValue(Client.class);
                        clients.add(client);
                    }
                    updatePieChartClients(clients);
                    // TODO: 19/04/17 stop progress

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            fbHelper.getMonthTypesRefByDate(cal).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<SessionType> types = new ArrayList<SessionType>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SessionType type = snapshot.getValue(SessionType.class);
                        types.add(type);
                    }
                    if (types.size() > 0) {
                        updatePieChartTypes(types);
                        // TODO: 19/04/17 stop progress
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            fbHelper.getYearTotalAmountsRefByDate(cal).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Integer, Integer> monthTotals = new HashMap<Integer, Integer>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        monthTotals.put(Integer.parseInt(snapshot.getKey()), snapshot.getValue(Integer.class));
                        if (Integer.parseInt(snapshot.getKey()) == (cal.get(Calendar.MONTH) + 1)) {
                            showMonthIncome(snapshot);
                        }
                    }
                    if (monthTotals.size() > 0) {
                        updateBarChartMonths(monthTotals);
                        // TODO: 19/04/17 stop progress
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

                private void showMonthIncome(DataSnapshot snapshot) {
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String s = formatter.format(snapshot.getValue(Integer.class));
                    textViewMonthIncome.setText(s);
                }
            });
        }


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentStatsClick(uri);
        }
    }


    private void updatePieChartClients(ArrayList<Client> clients) {
        HashMap<String, Integer> map = new HashMap<>();
        if (clients.size() > 0) {
            for (Client client : clients) {
                String key = client.clientName;
                map.put(key, client.totalAmount);
            }
        }
        else {
            map.put("", 1);
        }

        // iterate the hashmap and set pie entries:
        List<PieEntry> entries = getPieEntries(map);

        //  sort by totalAmount
        sortByTotalAmount(entries);

        // set pieChart
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
//        dataSet.setSliceSpace(5f);
        
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        PieChart pieChart = setPieChart(pieChartClients, data);
        pieChart.setRotationEnabled(false);

        setLegend(pieChart);

        pieChart.invalidate();
    }

    private void updatePieChartTypes(ArrayList<SessionType> types) {
        HashMap<String, Integer> map = new HashMap<>();
        for (SessionType type : types) {
            String key = type.type;
            map.put(key, type.totalAmount);
        }

        // iterate the hashMap and set pie entries:
        List<PieEntry> entries = getPieEntries(map);

        //  sort by totalAmount
        sortByTotalAmount(entries);

        // set pieChart
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        PieChart pieChart = setPieChart(pieChartSessionTypes, data);
        pieChart.setRotationEnabled(false);

        setLegend(pieChart);

        pieChart.invalidate();
    }

    @NonNull
    private List<PieEntry> getPieEntries(HashMap<String, Integer> map) {
        List<PieEntry> entries = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry clientEntry = (Map.Entry) it.next();
            entries.add(new PieEntry(Float.valueOf(clientEntry.getValue().toString()), clientEntry.getKey().toString()));
        }
        it.remove();
        return entries;
    }

    private void sortByTotalAmount(List<PieEntry> entries) {
        Collections.sort(entries, new Comparator<PieEntry>() {
            @Override
            public int compare(PieEntry o1, PieEntry o2) {
                if (o1.getValue() == o2.getValue()) return 0;
                return o1.getValue() < o2.getValue() ? 1 : -1;
            }
        });
    }

    private PieChart setPieChart(PieChart pieChart, PieData data) {
        pieChart.setData(data);
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setDrawEntryLabels(false);
//        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDescription("");
        return pieChart;
    }

    private void setLegend(PieChart pieChart) {
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
    }


    private void updateBarChartMonths(HashMap<Integer, Integer> monthTotals) {
        int[] colors = new int[12];
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            int monthAmount = 0;
            if (monthTotals.containsKey(i + 1)) {
                monthAmount = monthTotals.get(i + 1);
            }
            entries.add(new BarEntry((float) i + 1, (float) monthAmount, getResources().getStringArray(R.array.months)[i]));
            //noinspection WrongConstant
            if (i == cal.get(Calendar.MONTH)) {
                colors[i] = R.color.md_black_1000;
            } else {
                colors[i] = R.color.md_cyan_700;
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setColors(colors, getContext());
        BarData data = new BarData(dataSet);
        barChartMonths.setData(data);
        barChartMonths.getAxisLeft().setEnabled(false);
        barChartMonths.getAxisRight().setEnabled(false);
        XAxis xAxis = barChartMonths.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(true);
        barChartMonths.setDescription("");
        barChartMonths.invalidate();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentStatsClick(Uri uri);
    }

}
