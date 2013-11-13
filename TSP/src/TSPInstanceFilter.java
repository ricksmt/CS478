import java.io.File;
import java.io.FileFilter;


public class TSPInstanceFilter implements FileFilter {

	@Override
	public boolean accept(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith("tsp");
	}
}
