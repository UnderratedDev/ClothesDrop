package nestedternary.project;

import android.Manifest;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private ArrayList<MarkerOptions> markers;
    // private ArrayList<PolylineOptions> route;
    private PolylineOptions route;
    private Location cur_location;
    private ImageButton add_donate_qty_btn;
    private ImageButton directions_btn;
    private TextView net_status_textview;
    private Marker donateMarker;
    private boolean donateButtonVisibility;
    private boolean directionsButtonVisibility;
    private boolean netStatusTextviewVisibility;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markers                = new ArrayList<>();
        add_donate_qty_btn     = (ImageButton) findViewById (R.id.add_donate_qty_btn);
        directions_btn         = (ImageButton) findViewById (R.id.directions_btn);
        net_status_textview    = (TextView)    findViewById (R.id.net_status_textview);
        donateButtonVisibility = false;
        directionsButtonVisibility = false;
        netStatusTextviewVisibility= false;
        add_donate_qty_btn.setVisibility (View.GONE);
        directions_btn.setVisibility(View.GONE);
        net_status_textview.setVisibility(View.INVISIBLE);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_view);

        if (check_location_permission())
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        else
            createMap ();
    }

    // Check for location permission (FINE AND COARSE) and returns true if permission has not been granted
    private boolean check_location_permission () {
        return (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    // Uses an anonymous inner class to implement the map
    // Creates the map,
    // Has a listener which constantly updates the cur location private variable at the top
    // Executes the background task that fetches the data and then calculates the closest marker
    private void createMap () {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                // check_location_permission method does not work here???
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    return;

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

    // Depending on which permission has been granted, different actions occur, i.e for location, the map is initialised
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    createMap ();
                else {

                    SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_view);

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            map.getUiSettings().setMapToolbarEnabled(true);

                            new AsyncTaskRunnerFetch().execute();
                        }
                    });

                    // Toast.makeText (getApplicationContext(), "App does not have required permissions to function", Toast.LENGTH_LONG).show ();
                    // System.exit(1);
                }
            }
        }
    }

    // Starts the donation page, passes the snippet so that the donation page may use it to complete the sentence
    public void donateQty (final View view) {
        if (donateMarker != null) {
            Intent intent = new Intent (MainActivity.this, Donate.class);
            intent.putExtra ("binName", donateMarker.getSnippet());
            startActivity (intent);
        }
    }

    // Calculates the closest bin relative to cur user position/location
    private int get_closest_bin () {
        int index = -1;
        float minDistance = Float.MAX_VALUE;
        final Location location = cur_location;
        if (location == null || markers.isEmpty())
            return index;

        if (0 < markers.size ()) {
            Location target = new Location ("target");
            for (int i = 0; i < markers.size(); ++i) {
                LatLng temp = markers.get (i).getPosition ();
                target.setLatitude  (temp.latitude);
                target.setLongitude (temp.longitude);
                if (location.distanceTo (target) < minDistance) {
                    minDistance = location.distanceTo(target);
                    index = i;
                }
            }
        }
        return index;
    }

    // Parses the local kml in the assets folder for testing
    private void parseKML () {
        try {

            // PlaceMark

            String line;

            BufferedReader br = new BufferedReader (new InputStreamReader(getAssets().open ("PosAbilities.kml")));

            while ((line = br.readLine()) != null)
                if (line.contains ("<coordinates>")) {
                    line = br.readLine ();

                    String[] l_coords = line.split (",");

                    for (int i = 0; i < l_coords.length; ++i)
                        l_coords[i] = l_coords[i].replaceAll("\\s+","");

                    if (l_coords[0].isEmpty () || l_coords[1].isEmpty())
                        continue;

                    double latitude = Double.parseDouble (l_coords[1]), longitutde = Double.parseDouble (l_coords[0]);

                    markers.add (new MarkerOptions ().title ("BIN parsed").snippet ("BIN parsed").position (new LatLng (latitude, longitutde)));
                }

        } catch (Exception ex) {
            ex.printStackTrace ();
        }

    }

    // When map is clicked, do not show donate bin qty button
    private void mapClickListener () {
        map.setOnMapClickListener (new GoogleMap.OnMapClickListener() {
            public void onMapClick (LatLng latlng) {
                add_donate_qty_btn.setVisibility (View.GONE);
                directions_btn.setVisibility (View.GONE);
            }
        });
    }

    // When marker has been clicked, show donate qty button and simulate default action
    private void markerClickListener () {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                donateMarker = arg0;
                arg0.showInfoWindow ();
                map.animateCamera (CameraUpdateFactory.newLatLng(arg0.getPosition ()), 400, null);
                add_donate_qty_btn.setVisibility (View.VISIBLE);
                directions_btn.setVisibility (View.VISIBLE);
                return true;
            }
        });
    }

    // Creates and returns the directions url
    private String get_directions_url (LatLng origin, LatLng dest){

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude, str_dest = "destination=" + dest.latitude + "," + dest.longitude, sensor = "sensor=false", parameters = str_origin + "&" + str_dest + "&" + sensor;

        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;

    }

    // mapped to directions button, intent is made for google maps application and then opens the application using the location data we posses
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

    // Opens up the url to download the poly line information
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


    // Uses the downloadUrl method to get the data and returns it
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

    // Uses the DirectionsJSONParser to populate the routes with the necessary information to populate the polyline for the user
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

            if (lineOptions != null) {
                final Polyline opts = map.addPolyline(lineOptions);

                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        opts.setWidth(cameraPosition.zoom < 13 ? 10 : 4);
                    }

                });
            }

            map.setOnCameraMoveStartedListener (new GoogleMap.OnCameraMoveStartedListener () {

                @Override
                public void onCameraMoveStarted (int reason) {
                    add_donate_qty_btn.setVisibility (View.GONE);
                    directions_btn.setVisibility (View.GONE);
                }
            });
        }
    }

    // Fetches the data and calls teh get_closest_bin method to calculate the nearest bin.
    // Overrides the on marker click listener to show and hide the donate qty button when required.
    private class AsyncTaskRunnerFetch extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground (final Void... params) {
            if (map != null) {
                markers = new ArrayList<>();
                /*
                final MarkerOptions bin = new MarkerOptions()
                        .title("bin")
                        .snippet("closest bin")
                        .position(new LatLng(100, 255)),
                sydney = new MarkerOptions().title("Sydney").snippet("The most populous city in Australia.").position (new LatLng(-33.867, 151.206)),
                home   = new MarkerOptions().title ("Near Home").snippet("Near Yudhvir's House").position (new LatLng(49.2205977, -122.934911));
                markers.add(bin);
                markers.add(sydney);
                markers.add(home); */
                parseKML ();
                Log.d ("HERE", "ABC");

                if (map != null) {
                    final int index = get_closest_bin();
                    if (index != -1)
                        markers.get(index).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

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
                            } else
                                map.moveCamera (CameraUpdateFactory.newLatLngZoom (new LatLng(49.2290040, -123.0412511), 10));
                        }
                    });

                /* need to add this to select maker in future maybe??
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        //here turn off the overlays for nav turns etc.
                        marker.hideInfoWindow();
                    } */

                    runOnUiThread (new Runnable () {
                        public void run () {

                            mapClickListener ();
                            // FASTER AND map toolbar

                            markerClickListener ();

                        }
                    });
                }
            }
            return null;
        }

        protected void onPostExecute (Void results) { }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            net_status_textview.setVisibility(isConnected ? View.INVISIBLE : View.VISIBLE);
        }
    };

}