package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import jsprit.core.analysis.SolutionAnalyser;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.cost.TransportDistance;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;

public class PrintFileUtils {

	public static void write(VehicleRoutingProblem vrp,VehicleRoutingProblemSolution sol, String name, long eTime, String path){
		if(sol == null) {
			System.out.println("Solution not defined!");
			return;
		}

	ArrayList<Double> times =	(ArrayList<Double>) sol.getRoutes().stream().map(route -> route.getEnd().getArrTime()).collect(Collectors.toList());
System.out.println(times.stream().map(t -> String.valueOf(t)).collect(Collectors.joining("\t")));
		SolutionAnalyser analyser = PrintFileUtils.getObjectiveFunction(vrp, sol);
//analyser.getVariableTransportCosts(route)
	//analyser.getFixedCosts(route)
		ArrayList<Double> costs =	(ArrayList<Double>) sol.getRoutes().stream().map(route -> analyser.getVariableTransportCosts(route)+analyser.getFixedCosts(route)).collect(Collectors.toList());
		// Write solution on files
		BufferedWriter writer = getAppender(path);
		try {
			double seconds = eTime/1000.0;
			DecimalFormat df = new DecimalFormat("#.00");
			String output = name + ";" + df.format(analyser.getDistance()) + ";"
					+ df.format(seconds) + ";" + sol.getRoutes().size() + ";"
					+df.format(getStandardDeviation(times)) + ";" +df.format(getStandardDeviation(costs)) +";"
					+df.format(getGapFromAvg(times)) + ";" +df.format(getGapFromAvg(costs)) +";"
					+df.format(getMax(times)) + ";" +df.format(getMin(times)) +";"
					+df.format(getMax(costs)) + ";" +df.format(getMin(costs))
					+"\n";
		//	System.out.println(output);
			writer.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		close(writer);
	}
	private static BufferedWriter getAppender(String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return writer;
	}
	private static void close(BufferedWriter writer)  {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private static  double getStandardDeviation(Collection<Double> values){
		double avg;
		double sum = 0;
		double squareSum = 0 ;
		double variance = 0 ;

		for (double val : values){
			sum += val;
			squareSum += val*val;
		}
		avg = sum/values.size();
	variance = squareSum/values.size() - avg*avg;

		return Math.sqrt(variance);
	}

	private static  double getGapFromAvg(Collection<Double> values){
		double avg=0;
		double sum = 0;
		double sommascarto=0;
		for (double val : values){
			sum += val;
		}
		avg = sum/values.size();
		
		for (double val : values){
			sommascarto += Math.abs(val-avg)/avg;
		}
		

		return sommascarto/values.size();
	}
	
	private static double getMin(Collection<Double> values){
		double min=Double.MAX_VALUE;
		double sum = 0;
		double avg=0;
		for (Double double1 : values) {
			sum += double1;
			if(double1<min)
				min=double1;
		}
		avg=sum/values.size();
		return min/avg;
	}	
	
	private static double getMax(Collection<Double> values){
		double max=0;
		double avg=0;
		double sum = 0;
		for (Double double1 : values) {
			sum += double1;
			if(double1>max)
				max=double1;
		}
		avg=sum/values.size();
		return max/avg;
	}	
	
	
	
	
	
	 private static SolutionAnalyser getObjectiveFunction(final VehicleRoutingProblem vrp,VehicleRoutingProblemSolution solution) {

	     SolutionAnalyser analyser = new SolutionAnalyser(vrp, solution, new TransportDistance() {

			@Override
			public double getDistance(Location from, Location to) {
				// TODO Auto-generated method stub
				return vrp.getTransportCosts().getTransportCost(from, to,0.,null,null);
			}
		});
	     return analyser;
}}
