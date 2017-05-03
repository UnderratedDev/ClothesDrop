package nestedternary.project;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static org.junit.Assert.assertNotEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GetClosestBinInstrumentedTest {

    Location loc;
    Location loc2;
    Location null_loc;
    ArrayList<MarkerOptions> asc_markers;
    ArrayList<MarkerOptions> desc_markers;
    ArrayList<MarkerOptions> empty_markers;
    MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Pre-seed data before each test for consistency.
     * This method is called before every method that is annotated with @Test.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        activity = mActivityRule.getActivity();
        loc = new Location("");
        loc.setLatitude(0);
        loc.setLongitude(-1);
        loc2 = new Location("");
        loc2.setLatitude(0);
        loc2.setLongitude(3);
        null_loc = null;
        asc_markers = new ArrayList<>();
        desc_markers = new ArrayList<>();
        empty_markers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LatLng latLng = new LatLng(0, i);
            MarkerOptions marker = new MarkerOptions();
            marker.position(latLng);
            asc_markers.add(marker);
        }
        for (int i = 5; i > 0; i--) {
            LatLng latLng = new LatLng(0, i - 1);
            MarkerOptions marker = new MarkerOptions();
            marker.position(latLng);
            desc_markers.add(marker);
        }

        assertNotEquals(loc, null);
        assertEquals(null_loc, null);
        assertNotEquals(asc_markers, null);
        assertNotEquals(desc_markers, null);
        assertEquals(asc_markers.size(), 5);
        assertEquals(desc_markers.size(), 5);
        assertNotEquals(empty_markers, null);
        assertEquals(empty_markers.size(), 0);
    }

    /**
     * Null location should give us -1
     *
     * @throws Exception
     */
    @Test
    public void null_loc() throws Exception {
        closest_bin(-1, null_loc, empty_markers);
        closest_bin(-1, null_loc, asc_markers);
        closest_bin(-1, null_loc, desc_markers);
    }

    /**
     * Tests with an actual location
     *
     * @throws Exception
     */
    @Test
    public void loc() throws Exception {
        closest_bin(-1, loc, empty_markers);
        closest_bin(0, loc, asc_markers);
        closest_bin(4, loc, desc_markers);
    }

    /**
     * Test with another location.
     *
     * @throws Exception
     */
    @Test
    public void loc2() throws Exception {
        closest_bin(-1, loc2, empty_markers);
        closest_bin(3, loc2, asc_markers);
        closest_bin(1, loc2, desc_markers);
    }

    /**
     * Helper function that uses Reflection to access private members of the MainActivity
     * and asserts that the correct marker is selected.
     *
     * @param expected expected result
     * @param curr current location to use
     * @param markers locations of bins
     * @throws Exception
     */
    private void closest_bin(final int expected, final Location curr, final ArrayList<MarkerOptions> markers) throws Exception {
        Class clazz = activity.getClass();
        Field cur_location;
        Field main_markers;

        Method get_closest_bin;
        cur_location = clazz.getDeclaredField("cur_location");
        cur_location.setAccessible(true);
        main_markers = clazz.getDeclaredField("markers");
        main_markers.setAccessible(true);
        cur_location.set(activity, curr);
        main_markers.set(activity, markers);

        get_closest_bin = clazz.getDeclaredMethod("get_closest_bin");
        get_closest_bin.setAccessible(true);
        assertEquals(expected, get_closest_bin.invoke(activity));
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("nestedternary.project", appContext.getPackageName());
    }
}
