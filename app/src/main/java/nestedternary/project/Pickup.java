package nestedternary.project;

import android.util.Log;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yudhvir on 16/05/2017.
 */

public class Pickup implements Serializable {

    private Region region;
    private int    pickupid, bagQty;
    private float  lat, lng;
    String         address, date, notes;

    public Pickup (int pickupid, int bagQty, String date, String address, String notes, float lat, float lng, String name, int regionid) {
        this.pickupid = pickupid;
        this.bagQty = bagQty;
        this.date   = date;
        this.address = address;
        this.notes = notes;
        this.lat = lat;
        this.lng = lng;
        this.region = new Region (name, regionid);
    }

    public Region getRegion () {
        return region;
    }

    public int getbagQty () {
        return bagQty;
    }

    public int getUnixTimestampDate () {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateObj = df.parse (date);
            Log.e (":)", dateObj.toString());
            return (int)(dateObj.getTime () / 1000);
        } catch (ParseException ex) {
            ex.printStackTrace ();
        }
         return 0;
    }

    public int getId () {
        return pickupid;
    }
}
