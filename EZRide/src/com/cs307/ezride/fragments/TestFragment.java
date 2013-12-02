package com.cs307.ezride.fragments;

import com.cs307.ezride.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		return view;
	}
	
	public boolean notifyFragmentOfGroupChange(int groupIndex) {
		Log.d(TestFragment.class.getName(), "Fragment notified");
		return true;
	}

}
