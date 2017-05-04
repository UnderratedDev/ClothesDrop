package nestedternary.project;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DirectionsJSONParserInstrumentedTest {
    DirectionsJSONParser paser = new DirectionsJSONParser();
    Location myLocation;
    Location destination;
    String parameters, url;
    List<List<HashMap<String,String>>> result, testData;
    List testList;
    HashMap<String,String> path;
    MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);


    //Setting up the locations and url for the json object needed to test the parser
    @Before
    public void setup() throws Exception {

        activity = mActivityRule.getActivity();

        myLocation = new Location("");
        myLocation.setLatitude(0);
        myLocation.setLongitude(-1);

        destination = new Location("");
        destination.setLatitude(0);
        destination.setLongitude(0);


        url = "https://maps.googleapis.com/maps/api/directions/json?origin=49.2484593,-123.0034012&destination=49.2481494,-123.0045945&sensor=false";

        testList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> path = new HashMap<String, String>();
        testData = new ArrayList<List<HashMap<String,String>>>();

        path.put("lat", Double.toString(myLocation.getLatitude()));
        path.put("lng", Double.toString(myLocation.getLongitude()));

        testList.add(path);
        testData.add(testList);
    }

    @Test
    public void test1() throws Exception{
        Method downloadUrl;
        Class clazz = activity.getClass();
        downloadUrl = clazz.getDeclaredMethod("downloadUrl", String.class);
        downloadUrl.setAccessible(true);
        Log.e("BETTER MEOW", downloadUrl.invoke(activity, url).toString());


        JSONObject obj = new JSONObject(downloadUrl.invoke(activity, url).toString());
        result = paser.parse(obj);

        Log.e("Start of MEOW", result.get(0).size() + "");
        for (int i = 0; i < result.size(); i++){
            Log.e("Start of second MEOW", "a");
            for (int j = 0; j < result.get(i).size(); j++){
                 Log.e("A normal name", result.get(i).get(j).get("lat") + "," + result.get(i).get(j).get("lng") );
            }
            Log.e("End of second MEOW", "a");
        }
        Log.e("End of ALL MEOW", "a");

        //assertEquals(testData, result);

    }
}
