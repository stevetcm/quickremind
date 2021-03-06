package com.orangemuffin.quickremind.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.adapters.ReminderAdapter;
import com.orangemuffin.quickremind.database.ReminderDatabase;
import com.orangemuffin.quickremind.models.Reminder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* Created by OrangeMuffin on 6/20/2017 */
public class FragmentEnded extends Fragment {

    private RecyclerView recyclerView;

    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;
    private TextView noInactiveText;

    private ReminderDatabase rb;

    // Required empty public constructor
    public FragmentEnded() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ended, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        noInactiveText = (TextView) rootView.findViewById(R.id.no_reminder_text);

        rb = new ReminderDatabase(getContext());

        reminderList = getReminderList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        reminderAdapter = new ReminderAdapter(reminderList, getContext(), this);
        recyclerView.setAdapter(reminderAdapter);
        return rootView;
    }

    public List<Reminder> getReminderList() {
        List<Reminder> mList = rb.getAllReminders();
        List<Reminder> temp_inactive = new ArrayList<>();
        for (Reminder reminder : mList) {
            if (reminder.getActive().equals("false")) {
                temp_inactive.add(reminder);
            }
        }

        if (temp_inactive.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            noInactiveText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noInactiveText.setVisibility(View.GONE);
        }

        //sort list in ascending order of date and time
        Collections.sort(temp_inactive, new Comparator<Reminder>() {
            @Override
            public int compare(Reminder r1, Reminder r2) {
                DateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
                String c1 = r1.getDate() + " " + r1.getTime();
                String c2 = r2.getDate() + " " + r2.getTime();

                try {
                    return f.parse(c2).compareTo(f.parse(c1));
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        return temp_inactive;
    }

    public void updateList() {
        reminderList.clear();
        reminderList.addAll(getReminderList());
        reminderAdapter.notifyDataSetChanged();
    }

    public void checkEmpty() {
        if (reminderList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            noInactiveText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noInactiveText.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        updateList();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
        super.onPause();
    }
}
