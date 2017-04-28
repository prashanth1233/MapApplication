package com.example.prasanth.mapsfusedapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewFragment extends Fragment {

  /*  private Context context;*/
    private MapView mapView;
    private GoogleMap googleMap;
    private WebView webview;


//    public MapViewFragment(Context context)
//    {
//        this.context=context;
//    }
public MapViewFragment()
{

}

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.activity_map_view_fragment, viewGroup, false);


        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(bundle);
        webview=(WebView)view.findViewById(R.id.webView);
        webview.setBackgroundColor(0);

        if(Build.VERSION.SDK_INT>11)
        {
            webview.setLayerType(WebView.LAYER_TYPE_SOFTWARE,null);
        }
        webview.getSettings().setBuiltInZoomControls(true);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(bundle);
        if(mapView!=null)
        {
            googleMap=mapView.getMap();
            googleMap.addMarker(new MarkerOptions()
            .icon(BitmapDescriptorFactory.defaultMarker())
            .anchor(0.0f,1.0f)
            .position(new LatLng(17.4382103, 78.3938656)));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return view;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            /*MapsInitializer.initialize(getActivity());

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(17.4382103, 78.3938656));
            LatLngBounds bounds = builder.build();
            int padding = 0;
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cameraUpdate);
*/
        }
        return view;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}
