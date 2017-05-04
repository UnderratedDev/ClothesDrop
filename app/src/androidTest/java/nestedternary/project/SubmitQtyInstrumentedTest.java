package nestedternary.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SubmitQtyInstrumentedTest {

    Donate activity;
    EditText qtyEdit;

    @Rule
    public ActivityTestRule<Donate> mActivityRule = new ActivityTestRule<>(Donate.class);

    @Before
    public void setUp() {
        activity = mActivityRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qtyEdit = (EditText) activity.findViewById(R.id.donate_qty);
            }
        });
    }

    /**
     * Tests positive numbers
     *
     * @throws Exception
     */
    @Test
    public void testnormal() throws Exception {
        testQty(5);
        testQty(20);
        testQty(100);
        testQty(666);
    }

    /**
     * Tests negative numbers
     *
     * @throws Exception
     */
    @Test
    public void testneg() throws Exception {
        testQty(-5);
        testQty(-20);
        testQty(-234);
        testQty(-666);
    }

    /**
     * Helper method that sets the text of the quantity box to the expected amount,
     * runs the submitQty method, and asserts that the shared preferences value is equal to the
     * expected value.
     *
     * @param expected value to test
     */
    private void testQty(final int expected) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qtyEdit.setText(expected + "");
                activity.submtiQty(null);
                assertEquals(expected, activity.prefs.getInt("qty", -1));
            }
        });
    }
}
