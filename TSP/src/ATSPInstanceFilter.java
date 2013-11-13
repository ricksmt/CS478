import java.io.File;
import java.io.FileFilter;

public class ATSPInstanceFilter implements FileFilter {

	@Override
	public boolean accept(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith("atsp");
	}
}
