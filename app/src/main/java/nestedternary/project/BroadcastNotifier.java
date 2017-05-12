package nestedternary.project;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by David on 2017-05-11.
 */

public class BroadcastNotifier {
    private LocalBroadcastManager broadcastManager;

    public BroadcastNotifier(Context context) {
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void broadcastIntentWithState(int status) {

        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_ACTION);
        intent.putExtra (Constants.EXTENDED_DATA_STATUS, status);

        broadcastManager.sendBroadcast(intent);
    }
}
