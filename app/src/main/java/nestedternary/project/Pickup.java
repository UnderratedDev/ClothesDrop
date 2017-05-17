package nestedternary.project;

/**
 * Created by Yudhvir on 16/05/2017.
 */

public class Pickup {

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
}
