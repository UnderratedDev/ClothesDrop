package nestedternary.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Yudhvir on 16/05/2017.
 */

public class Region implements Serializable {
    private String name;
    private int id;
    private ArrayList<Integer> dates;

    public Region (String name, int id, ArrayList<Integer> dates) {
        this.name  = name;
        this.id    = id;
        this.dates = dates;
    }

    public String getName () {
        return name;
    }

    public int getId () {
        return id;
    }

    public ArrayList<Integer> getDates () {
        return dates;
    }
}
