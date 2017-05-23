package nestedternary.project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class checkNetworkConnectionInstrumentedTest {
    ConnectivityManager conManger;
    WifiManager wifiManager ;
    MainActivity activity;
    Method checkNetworkConnection, dataMtd, mobileDataEnabledMethod;
    Class conClass, connectivityManagerClass;
    Field connectivityManagerField;
    Object connectivityManager;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {

        activity = mActivityRule.getActivity();
        Class clazz = activity.getClass();
        checkNetworkConnection = clazz.getDeclaredMethod("checkNetworkConnection");
        checkNetworkConnection.setAccessible(true);

        wifiManager  = (WifiManager)activity.getApplicationContext().getSystemService(activity.getApplicationContext().WIFI_SERVICE);
        conManger  = (ConnectivityManager)activity.getApplicationContext().getSystemService(activity.getApplicationContext().CONNECTIVITY_SERVICE);

        conClass = Class.forName(conManger.getClass().getName());
        mobileDataEnabledMethod = conClass.getDeclaredMethod("getMobileDataEnabled");
        mobileDataEnabledMethod.setAccessible(true);
    }

    @Test
    public void withoutWifi() throws Exception {

        wifiManager.setWifiEnabled(false);
        boolean dataStatus = (boolean)mobileDataEnabledMethod.invoke(conManger);

        assertEquals(dataStatus, checkNetworkConnection.invoke(activity));

        wifiManager.setWifiEnabled(true);
    }

    @Test
    public void withWifi() throws Exception {

        wifiManager.setWifiEnabled(true);
        assertEquals(true, checkNetworkConnection.invoke(activity));

    }




}
