package nestedternary.project;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    private ArrayList<MarkerOptions> markers;
    // private ArrayList<PolylineOptions> route;
    private PolylineOptions route;
    private Location cur_location;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markers = new ArrayList<>();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_view);

        if (check_location_permission())
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        else {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;

                    // check_location_permission method does not work here???
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    map.setMyLocationEnabled(true);

                    map.getUiSettings().setMapToolbarEnabled(true);
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                    // Create a criteria object to retrieve provider
                    Criteria criteria = new Criteria();

                    // Get the name of the best provider

                    String provider = locationManager.getBestProvider(criteria, true);
                    // Get Current Location
                    cur_location = locationManager.getLastKnownLocation(provider);

                    // Can use above code when needed or use this.
                    // Below is more battery taxing but is more immediate
                    map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            cur_location = location;
                        }
                    });
                    new AsyncTaskRunnerFetch().execute();
                }
            });
        }
    }
    private boolean check_location_permission () {
        return (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_view);

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            // check_location_permission method does not work here???
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            map.setMyLocationEnabled(true);

                            map.getUiSettings().setMapToolbarEnabled(false);
                            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                            // Create a criteria object to retrieve provider
                            Criteria criteria = new Criteria();

                            // Get the name of the best provider

                            String provider = locationManager.getBestProvider(criteria, true);
                            // Get Current Location
                            cur_location = locationManager.getLastKnownLocation(provider);

                            // Can use above code when needed or use this.
                            // Below is more battery taxing but is more immediate
                            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                @Override
                                public void onMyLocationChange(Location location) {
                                    cur_location = location;
                                }
                            });
                            new AsyncTaskRunnerFetch().execute();
                        }
                    });

                } else {
                    Toast.makeText (getApplicationContext(), "App does not have required permissions to function", Toast.LENGTH_LONG).show ();
                    // System.exit(1);
                }
            }
        }
    }

    private int get_closest_bin () {
        int index = -1;
        float minDistance = Float.MAX_VALUE;
        final Location location = cur_location;
        if (location == null)
            return index;
        if (0 < markers.size ()) {
            Location target = new Location ("target");
            for (int i = 1; i < markers.size(); ++i) {
                LatLng temp = markers.get (i).getPosition ();
                target.setLatitude (temp.latitude);
                target.setLongitude (temp.longitude);
                if (location.distanceTo (target) < minDistance) {
                    minDistance = location.distanceTo(target);
                    index = i;
                }
            }
        }
        return index;
    }

    public void get_directions (final View view) {
        int index = get_closest_bin();
        if (index != -1) {
            LatLng latLng = markers.get(index).getPosition();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + cur_location.getLatitude() + "," + cur_location.getLongitude() + "&daddr=" + latLng.latitude + "," + latLng.longitude));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }
    }

    public void near_bin (final View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        int index = get_closest_bin ();
        if (index != -1)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(index).getPosition(), 7));
    }

    private String get_directions_url (LatLng origin, LatLng dest){

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude, str_dest = "destination=" + dest.latitude + "," + dest.longitude, sensor = "sensor=false", parameters = str_origin + "&" + str_dest + "&" + sensor;

        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while((line = br.readLine ())  != null)
                sb.append(line);

            data = sb.toString();

            br.close();

        } catch(Exception e){
            Log.d("Exception while url", e.toString ());
        } finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString ());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for(int i=0;i<result.size(); ++i) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0; j < path.size();++j) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat")), lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            route = lineOptions;

            final Polyline opts = map.addPolyline(lineOptions);

            map.setOnCameraChangeListener (new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange (CameraPosition cameraPosition) {

                    final float zoom_lvl = cameraPosition.zoom;

                    runOnUiThread (new Runnable (){
                        public void run () {
                            // Toast.makeText (getApplicationContext (), "Zoom LVL is : " + zoom_lvl, Toast.LENGTH_SHORT).show ();
                        }
                    });

                    opts.setWidth (cameraPosition.zoom < 13 ? 10 : 4);

                }
            });
        }
    }

    private class AsyncTaskRunnerFetch extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground (final Void... params) {
            if (map != null) {
                markers = new ArrayList<>();
                final MarkerOptions bin = new MarkerOptions()
                        .title("bin")
                        .snippet("closest bin")
                        .position(new LatLng(100, 255)),
                sydney = new MarkerOptions().title("Sydney").snippet("The most populous city in Australia.").position (new LatLng(-33.867, 151.206)),
                home   = new MarkerOptions().title ("Near Home").snippet("Near Yudhvir's House").position (new LatLng(49.2205977, -122.934911));
                markers.add(bin);
                markers.add(sydney);
                markers.add(home);

                if (map != null) {
                    final int index = get_closest_bin();
                    if (index != -1)
                        markers.get(index).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    runOnUiThread (new Runnable () {
                        public void run () {
                            for (MarkerOptions mo : markers)
                                map.addMarker(mo);

                            if (index != -1) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(index).getPosition(), 13));
                                final Location location = cur_location;

                                LatLng cur = new LatLng (location.getLatitude(), location.getLongitude()), latLng = markers.get (index).getPosition();
                                String url = get_directions_url(cur, latLng);

                                new DownloadTask().execute (url);
                            }
                        }
                    });
                }
            }
            return null;
        }

        protected void onPostExecute (Void results) {
        }
    }

}