package nestedternary.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.R.attr.src;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GetDirectionsUrlInstrumentedTest {

    MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        activity = mActivityRule.getActivity();
    }

    @Test
    public void test() throws Exception {
        helper("https://maps.googleapis.com/maps/api/directions/json?origin=12.0,34.0&destination=56.0,78.0&sensor=false", new LatLng(12, 34), new LatLng(56, 78));
        helper("https://maps.googleapis.com/maps/api/directions/json?origin=-90.0,34.0&destination=56.0,78.0&sensor=false", new LatLng(-90, 34), new LatLng(56, 78));
        helper("https://maps.googleapis.com/maps/api/directions/json?origin=12.0,66.6&destination=56.0,78.0&sensor=false", new LatLng(12, 66.6), new LatLng(56, 78));
        helper("https://maps.googleapis.com/maps/api/directions/json?origin=12.0,34.0&destination=56.0,123.0&sensor=false", new LatLng(12, 34), new LatLng(56, 123));
    }

    /**
     * Helper function that uses Reflection to access private methods of MainActivity and
     * asserts that the Url matches the expected string
     *
     * @param expected
     * @param src
     * @param dst
     */
    private void helper(final String expected, final LatLng src, final LatLng dst) throws Exception {
        Class clazz = activity.getClass();

        Method get_directions_url;
        get_directions_url = clazz.getDeclaredMethod("get_directions_url", LatLng.class, LatLng.class);
        get_directions_url.setAccessible(true);
        assertEquals(expected, get_directions_url.invoke(activity, src, dst));
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("nestedternary.project", appContext.getPackageName());
    }
}
