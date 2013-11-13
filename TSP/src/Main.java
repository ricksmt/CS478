import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.Tour;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Main {
	
	static String[] algorithms = { };

	public static void main(String[] args) throws IOException {
		
		List<Instance> instances = new ArrayList<Instance>();
		FastVector atts = new FastVector();
		atts.addElement(new Attribute("Problem Name", (FastVector)null));// String
		atts.addElement(new Attribute("Node Count"));// Int (Numeric)
		atts.addElement(new Attribute("Edge Count"));// Int (Numeric)
		atts.addElement(new Attribute("StdDev Neighbors"));// Numeric
		atts.addElement(new Attribute("StdDev Edges"));// Numeric
		atts.addElement(new Attribute("Connectedness"));// Numeric
		for(String algorithm: algorithms) {
			atts.addElement(new Attribute(algorithm + " Time"));// Numeric
			atts.addElement(new Attribute(algorithm + " Solution"));// Numeric
			atts.addElement(new Attribute(algorithm + " Ratio"));// Numeric
		}
		atts.addElement(new Attribute("Optimal Solution Count"));// Int (Numeric)
		atts.addElement(new Attribute("Optimal Solution"));// Numeric


		System.out.println("Processing");
		String problem = "tsp";
		// Create new dataset
		Instances dataset = new Instances(problem.toUpperCase() + "-Dataset-" + new Date(), atts, instances.size());
		File tsp = new File("./data/" + problem + "/");
		FileFilter filter = new TSPInstanceFilter();
		for(File file: tsp.listFiles(filter)) {
			TSPInstance instance = new TSPInstance(file);
			for(File solution: tsp.listFiles(new TSPSolutionFilter(file.getName().substring(0, file.getName().indexOf('.'))))) {
				instance.addTour(solution);
			}
			DistanceTable table = instance.getDistanceTable();
			
			// Create entry
			Instance row = new Instance(atts.size());
			row.setDataset(dataset);
			String name = instance.getName();
			System.out.print(name);
			row.setValue(0, name);
			int nodeTotal = table.listNodes().length;
			row.setValue(1, nodeTotal);
			int edgeTotal = getEdgeCount(table);
			row.setValue(2, edgeTotal);
			System.out.print('.');
			double neighborAverage = getNeighborAverage(table);
			double edgeAverage = getEdgeAverage(table);
			double stdDevNeighbor = 0, stdDevEdge = 0;
			int[] nodes = table.listNodes();
			for (int node: nodes)
			{
				int[] neighbors = table.getNeighborsOf(node);
				stdDevNeighbor += Math.pow(neighbors.length - neighborAverage, 2);
				
				double sum = 0;
				for(int neighbor: nodes) sum += table.getDistanceBetween(node, neighbor);
				stdDevEdge += Math.pow(sum - edgeAverage, 2);
			}
			row.setValue(3, stdDevNeighbor);
			row.setValue(4, stdDevEdge);
			row.setValue(5, 2 * edgeTotal / nodeTotal);

			System.out.print('.');
			List<Tour> solutions = instance.getTours();
			boolean haveSolution = solutions.size() > 0;
			if(haveSolution) {
				row.setValue(atts.size() - 2, solutions.size());
				row.setValue(atts.size() - 1, solutions.get(0).distance(instance));
			}
			else {
				row.setValue(atts.size() - 2, Instance.missingValue());
				row.setValue(atts.size() - 1, Instance.missingValue());
			}

			System.out.print('.');
			for(int i = 6; i + 3 < atts.size(); i += 3) {
				@SuppressWarnings("unused")
				int index = (i - 6) / 3;// Index into algorithm
				if(haveSolution);
				else row.setValue(i + 2, Instance.missingValue());
			}
			dataset.add(row);
			System.out.println("done");
		}
		System.out.println("Complete");
	}
	
	static public int getEdgeCount(DistanceTable table) {
		int count = 0;
		int[] nodes = table.listNodes();
		for(int i = 0; i < nodes.length; i++) {
			int[] neighbors = table.getNeighborsOf(nodes[i]);
			for(int j = 0; j < neighbors.length; j++) {
				if(neighbors[j] > nodes[i]) count++;
			}
		}
		return count;
	}
	
	static public double getNeighborAverage(DistanceTable table) {
		double count = 0;
		int[] nodes = table.listNodes();
		for(int i = 0; i < nodes.length; i++) {
			count += table.getNeighborsOf(nodes[i]).length;
		}
		return count / nodes.length;
	}
	
	static public double getEdgeAverage(DistanceTable table) {
		double count = 0;
		int[] nodes = table.listNodes();
		for(int i = 0; i < nodes.length; i++) {
			int[] neighbors = table.getNeighborsOf(nodes[i]);
			for(int j = 0; j < neighbors.length; j++) {
				if(neighbors[j] > nodes[i])// This removes double counting
					count += table.getDistanceBetween(nodes[i], neighbors[j]);
			}
		}
		return count / getEdgeCount(table);
	}
}
