package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1,locationListener);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MainActivity.this,location.toString(),Toast.LENGTH_SHORT).show();
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null)
                updateLocationInfo(location);
        }
    }

    public void updateLocationInfo(Location location)
    {
        TextView latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        TextView accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
        TextView altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);

        latitudeTextView.setText("Latitude: " + String.format("%.2f",location.getLatitude()));
        longitudeTextView.setText("Longitude: " + String.format("%.2f",location.getLongitude()));
        accuracyTextView.setText("Accuracy: " + String.format("%.2f",location.getAccuracy()));
        altitudeTextView.setText("Altitude: " + String.format("%.2f",location.getAltitude()));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String address = "Could not find Address :(";
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(addressList!=null && addressList.size()>0)
            {
                address = "Address:\n";
                if(addressList.get(0).getThoroughfare()!=null)
                    address+=addressList.get(0).getThoroughfare() + "\n";
                if(addressList.get(0).getLocality()!=null)
                    address+=addressList.get(0).getLocality() + " ";
                if(addressList.get(0).getPostalCode()!=null)
                    address+=addressList.get(0).getPostalCode() + " ";
                if(addressList.get(0).getAdminArea()!=null)
                    address+=addressList.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(address.length()<15)
            addressTextView.setText("Could not find Address :(");
        else addressTextView.setText(address);
    }
}