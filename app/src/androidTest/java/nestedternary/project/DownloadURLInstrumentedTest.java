package nestedternary.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DownloadURLInstrumentedTest {

    MainActivity activity;
    String url, Json;
    Method downloadUrl;
    Class clazz;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setup() throws Exception {

        url = "https://maps.googleapis.com/maps/api/directions/json?origin=49.2484593,-123.0034012&destination=49.2481494,-123.0045945&sensor=false";
        activity = mActivityRule.getActivity();

        clazz = activity.getClass();
        downloadUrl = clazz.getDeclaredMethod("downloadUrl", String.class);
        downloadUrl.setAccessible(true);
        Json = "{\n" +
                "   \"geocoded_waypoints\" : [\n" +
                "      {\n" +
                "         \"geocoder_status\" : \"OK\",\n" +
                "         \"place_id\" : \"ChIJE55uveF2hlQRZU7pOI2C-7c\",\n" +
                "         \"types\" : [ \"route\" ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"geocoder_status\" : \"OK\",\n" +
                "         \"place_id\" : \"EjAzNzU1IFdpbGxpbmdkb24gQXZlLCBCdXJuYWJ5LCBCQyBWNUcgM0gzLCBDYW5hZGE\",\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"routes\" : [\n" +
                "      {\n" +
                "         \"bounds\" : {\n" +
                "            \"northeast\" : {\n" +
                "               \"lat\" : 49.25182419999999,\n" +
                "               \"lng\" : -123.0037394\n" +
                "            },\n" +
                "            \"southwest\" : {\n" +
                "               \"lat\" : 49.2481683,\n" +
                "               \"lng\" : -123.0044465\n" +
                "            }\n" +
                "         },\n" +
                "         \"copyrights\" : \"Map data ©2017 Google\",\n" +
                "         \"legs\" : [\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"0.8 km\",\n" +
                "                  \"value\" : 825\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"2 mins\",\n" +
                "                  \"value\" : 139\n" +
                "               },\n" +
                "               \"end_address\" : \"3755 Willingdon Ave, Burnaby, BC V5G 3H3, Canada\",\n" +
                "               \"end_location\" : {\n" +
                "                  \"lat\" : 49.2481683,\n" +
                "                  \"lng\" : -123.0044289\n" +
                "               },\n" +
                "               \"start_address\" : \"White Ave, Burnaby, BC V5G, Canada\",\n" +
                "               \"start_location\" : {\n" +
                "                  \"lat\" : 49.2484577,\n" +
                "                  \"lng\" : -123.0037394\n" +
                "               },\n" +
                "               \"steps\" : [\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"74 m\",\n" +
                "                        \"value\" : 74\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 13\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : 49.2491272,\n" +
                "                        \"lng\" : -123.0037468\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Head \\u003cb\\u003enorth\\u003c/b\\u003e on \\u003cb\\u003eWhite Ave\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"{yqkHjdwmVeC@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : 49.2484577,\n" +
                "                        \"lng\" : -123.0037394\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"33 m\",\n" +
                "                        \"value\" : 33\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 13\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : 49.24912440000001,\n" +
                "                        \"lng\" : -123.0042056\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e toward \\u003cb\\u003eWillingdon Ave\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"a~qkHldwmV@r@?f@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : 49.2491272,\n" +
                "                        \"lng\" : -123.0037468\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 300\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 38\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : 49.25182419999999,\n" +
                "                        \"lng\" : -123.0042561\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eWillingdon Ave\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"_~qkHhgwmVoBDiI@aB@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : 49.24912440000001,\n" +
                "                        \"lng\" : -123.0042056\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.4 km\",\n" +
                "                        \"value\" : 418\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 75\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : 49.2481683,\n" +
                "                        \"lng\" : -123.0044289\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Make a \\u003cb\\u003eU-turn\\u003c/b\\u003e at \\u003cb\\u003eGoard Way\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"uturn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"{nrkHrgwmV?\\\\lH?nCBr@@nD@`@ATA\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : 49.25182419999999,\n" +
                "                        \"lng\" : -123.0042561\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  }\n" +
                "               ],\n" +
                "               \"traffic_speed_entry\" : [],\n" +
                "               \"via_waypoint\" : []\n" +
                "            }\n" +
                "         ],\n" +
                "         \"overview_polyline\" : {\n" +
                "            \"points\" : \"{yqkHjdwmVeC@@r@?f@oBDiI@aB@?\\\\|LBbFBv@C\"\n" +
                "         },\n" +
                "         \"summary\" : \"Willingdon Ave\",\n" +
                "         \"warnings\" : [],\n" +
                "         \"waypoint_order\" : []\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}" ;
    }

    @Test
    public void checkJSon() throws Exception {
        String temp = Json.replaceAll("\\s+","");
        //String temp = Json;
        String temp2 = downloadUrl.invoke(activity, url).toString();
        temp2 = temp2.replaceAll("\\s+","");
        // Log.e("String Check", temp.compareToIgnoreCase(temp2) + "");
        Log.e("MEOW", temp);
        Log.e("String ", temp2);
        assertEquals(temp, temp2);

    }

}
