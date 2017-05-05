package nestedternary.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GetBinInstrumentedTest {

    MainActivity activity;
    Class clazz;
    Field main_markers;
    ArrayList<MarkerOptions> markers;
    Method getBin;
    MarkerOptions testPoint;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {

        activity = mActivityRule.getActivity();

        clazz = activity.getClass();
        main_markers = clazz.getDeclaredField("markers");
        main_markers.setAccessible(true);

        markers = new  ArrayList<MarkerOptions>();
        markers.add(new MarkerOptions().position(new LatLng(0.0, 1.0)));
        markers.add(new MarkerOptions().position(new LatLng(42.0, 1.0)));
        markers.add(new MarkerOptions().position(new LatLng(42.0, 42.0)));
        markers.add(new MarkerOptions().position(new LatLng(42.0, -42.0)));
        markers.add(new MarkerOptions().position(new LatLng(-2.0, -42.0)));
        main_markers.set(activity, markers);

        getBin = clazz.getDeclaredMethod("getBin", MarkerOptions.class);
        getBin.setAccessible(true);
    }

    @Test
    public void BinMethodTet() throws Exception{
        testPoint = new MarkerOptions().position(new LatLng(0.0, 1.0));

        assertEquals(0 , getBin.invoke(activity, testPoint));

        testPoint = new MarkerOptions().position(new LatLng(0.0, -1.0));
        assertEquals(-1 , getBin.invoke(activity, testPoint));

        testPoint = new MarkerOptions().position(new LatLng(0.0, 200.0));
        assertEquals(-1 , getBin.invoke(activity, testPoint));

        testPoint = new MarkerOptions().position(new LatLng(-2.0, -42.0));
        assertEquals(4 , getBin.invoke(activity, testPoint));
    }
}
