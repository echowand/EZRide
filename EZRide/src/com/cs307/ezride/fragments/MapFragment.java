package com.cs307.ezride.fragments;

import com.cs307.ezride.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
	private GoogleMap mMap = null;
	private static View view = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup)view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
			mMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.fragment_map_map)).getMap();
			mMap.setMyLocationEnabled(true);
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			
			LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
			Criteria c = new Criteria();
			String bestProvider = locationManager.getBestProvider(c, true);
			Location location = locationManager.getLastKnownLocation(bestProvider);
			LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
			CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom((float)13.75).bearing(0).tilt(0).build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		} catch (InflateException e) {
			Log.d(MapFragment.class.getName() + ".onCreateView", "Map already exists");
		}
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	public boolean notifyFragmentOfGroupChange(int groupIndex) {
		return true;
	}

}
