import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class KMLParser {
	public static void main (String args[]) {	
		try {
			
			ArrayList<Double> lat = new ArrayList<>(), longtitude = new ArrayList<>(), z_coords = new ArrayList<>();
		
			String line;
			
			BufferedReader br = new BufferedReader (new FileReader ("PosAbilities.kml"));
			
			while ((line = br.readLine()) != null)
				if (line.contains ("<coordinates>")) {
					line = br.readLine ();
					
					String[] l_coords = line.split (",");
					
					for (int i = 0; i < l_coords.length; ++i)
						l_coords[i] = l_coords[i].replaceAll("\\s+","");
					
					lat.add (Double.parseDouble (l_coords[0]));
					longtitude.add (Double.parseDouble (l_coords[1]));
					z_coords.add (Double.parseDouble (l_coords[2]));
				}
				
			for (double lat_ : lat)
				System.out.println (lat_);
			
			System.out.println ("***************");
			
			for (double long_ : longtitude)
				System.out.println (long_);
			
			System.out.println ("***************");
			
			for (double z_ : z_coords)
				System.out.println (z_);
			
		} catch (Exception ex) {
			ex.printStackTrace ();
		}
		
	}
}