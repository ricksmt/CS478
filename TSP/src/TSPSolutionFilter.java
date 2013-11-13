import java.io.File;
import java.io.FileFilter;

public class TSPSolutionFilter implements FileFilter {

	String problemName = null;
	
	public TSPSolutionFilter(String problem) {
		problemName = problem;
	}

	@Override
	public boolean accept(File f) {
		boolean answer = f.isFile() && f.getName().toLowerCase().endsWith("opt.tour");
		if(problemName == null || !answer) return answer;
		else return f.getName().startsWith(problemName);
	}
}
