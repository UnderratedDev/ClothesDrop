package nestedternary.project;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Yudhvir on 10/05/2017.
 */

public class BackendPullService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public BackendPullService() {
        super("BackendPullService");
    }

    @Override
    protected void onHandleIntent (Intent workIntent) {
        String dataString = workIntent.getDataString ();
        Log.d ("Background Serrvice", dataString);
    }
}
