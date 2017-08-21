package com.app.sinaps.tabtesting.Maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.sinaps.tabtesting.R;
import com.app.sinaps.tabtesting.Scaler.ScalerFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;

    public static final int PERMISSION_REQUEST_CODE_MAP = 454;

    private LocationManager locationManager;
    /** Частота запрос обновления текущей позиции в мс*/
    private int frequencyUpdate = 5000;
    /** Радиус сохранеия координат в м */
    private int radiusUpdate = 10;
    /** Величина увеличения камеры GoogleMap*/
    private int zoomRate = 16;
    private int textSize = 20;
    private int padding = 5;
    private double latitude;
    private double longitude;
    /** Координаты карты поумолчанию */
    private LatLng tomsk = new LatLng(56, 84);
    /** Координаты текущего местоположения */
    private LatLng currentPosition;
    private Marker marker;
    /** Строк для формирования надписа с текущими координатами */
    private String coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE_MAP);
        }else {
            Log.d("map", "У нас есть разрешение");
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, frequencyUpdate, radiusUpdate, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d("map", "Получаем разрешения");

        if (requestCode == ScalerFragment.PERMISSION_REQUEST_CODE && grantResults.length > 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("map", "разрешение полученно!!)))");
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.d("map", "разрешение полученно!!)))");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.moveCamera(CameraUpdateFactory.newLatLng(tomsk));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE_MAP);
        }else {
    }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        currentPosition = new LatLng(latitude, longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(zoomRate)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        coords = String.format(getResources().getString(R.string.map_lat) +
                        " %1$.4f,\n"+ getResources().getString(R.string.map_lon) + " %2$.4f",
                currentPosition.latitude, currentPosition.longitude);

        marker = map.addMarker(new MarkerOptions().position(currentPosition)
                .snippet(coords)
                .title(getResources().getString(R.string.map_marker_title)));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                if (marker.getId().equals(MapsActivity.this.marker.getId())) {
                    TextView tvLat = new TextView(MapsActivity.this);
                    tvLat.setText(coords);
                    tvLat.setTextSize(textSize);
                    tvLat.setPadding(padding,padding,padding,padding);
                    tvLat.setBackgroundColor(Color.WHITE);
                    tvLat.setTextColor(Color.GREEN);
                    return tvLat;
                } else
                    return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                TextView tv = new TextView(MapsActivity.this);
                tv.setText("Test getInfoContents");
                return tv;
            }
        });

        marker.showInfoWindow();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
