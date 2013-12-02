package com.cs307.ezride.fragments;

import com.cs307.ezride.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;

public class CalendarFragment extends Fragment {
	private CalendarView mCalendarView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar, container, false);
		
		mCalendarView = (CalendarView)view.findViewById(R.id.fragment_calendar_calendar);
		mCalendarView.setOnDateChangeListener(new OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				Toast.makeText(getActivity(), "Selected date is " + (month + 1) + " : " + dayOfMonth + " : " + year, Toast.LENGTH_SHORT).show();
			}
		});
		
		return view;
	}
	
	public boolean notifyFragmentOfGroupChange(int groupIndex) {
		return true;
	}

}
