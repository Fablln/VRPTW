/*******************************************************************************
* Copyright (C) 2015  ORO e ISMB
* Questo e' il main del programma di ottimizzazione VRPTW
* Il programma prende in input un file csv con i clienti ed un csv di configurazione (deposito e veicoli) e restituisce in output il file delle route ed un file sintetico di dati dei viaggi.
* L'algoritmo ultizzato e' un Large Neighborhood
******************************************************************************/

package main;

import java.util.Collection;
import java.util.stream.Collectors;

import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
import jsprit.core.algorithm.selector.SelectBest;
import jsprit.core.algorithm.termination.TimeTermination;
import jsprit.core.analysis.SolutionAnalyser;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.instance.reader.SolomonReader;
import jsprit.util.Examples;
import main.OROoptions.CONSTANTS;
import main.OROoptions.PARAMS;

public class Main {
public static int numVehicles = 0;
public static boolean secondPart=false;
public static  VehicleRoutingProblem.Builder vrpBuilder;
public static SolutionAnalyser analyser;
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
for (String string : args) {
	System.out.println(string);
}
		
		if(args.length == 3){
			if(Integer.parseInt(args[2]) >= 0){
				numVehicles = Integer.parseInt(args[2].trim());
			}
			else{
				System.out.println("Number of vehicles must be positive");
			}
		}
		else{
			System.out.println("Wrong number of parameters");
			return; 
		}
		
		// Some preparation - create output folder

		Examples.createOutputFolder();

		// Read input parameters
		OROoptions options = new OROoptions(args);

		for(int r=0; r<(int)options.get(CONSTANTS.REPETITION); r++) {
			secondPart=false;
			// Time tracking
			long startTime = System.currentTimeMillis();
			// Create a vrp problem builder
			  vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			// A solomonReader reads solomon-instance files, and stores the required information in the builder.
			new SolomonReader(vrpBuilder).read("input/" + options.get(PARAMS.INSTANCE));
			vrpBuilder.setFleetSize(FleetSize.INFINITE);

			VehicleRoutingProblem vrp = vrpBuilder.build();
			// Create the instace and solve the problem
			
			VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp,
					(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG));
			setTimeLimit(vra, (long)options.get(CONSTANTS.TIME)/6*4);
			// Solve the problem
			Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
	
			
			//solve second part
			//enable balancing part of cost calculator
			secondPart=true;
			
			vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp,
					(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG));
			setTimeLimit(vra, (long)options.get(CONSTANTS.TIME)/6*2);
			
			VehicleRoutingProblemSolution solution1 = new SelectBest().selectSolution(solutions);
			
			analyser = new SolutionAnalyser(vrp, solution1, new SolutionAnalyser.DistanceCalculator() { @Override
				public double getDistance(Location from, Location to) {
				return vrp.getTransportCosts().getTransportCost(from, to, 0., null, null); }
				});
			
			//I need to set the cost of the bestSolution because in this part the cost is calculated without penalty,
			//to do this I "copied" the cost calcultor in the main.
			solution1.setCost(calcolatore(solution1,analyser));
			
			//I add the solution of the first part in the solution memory of the second one,
			//I decided to add many times the solution, so that it can't be erased by error.
			for(int i=0;i<solutions.size();i++)
				vra.addInitialSolution(solution1);
			
			// Solve the problem
			solutions = vra.searchSolutions();
		
			// Extract the best solution
			VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);
			

			 analyser = new SolutionAnalyser(vrp, solution, new SolutionAnalyser.DistanceCalculator() { @Override
				public double getDistance(Location from, Location to) {
				return vrp.getTransportCosts().getTransportCost(from, to, 0., null, null); }
				});
			
			System.out.println(analyser.getDistance());
			
			// Print solution on a file

			PrintFileUtils.write(vrp, solution, (String)options.get(PARAMS.INSTANCE), System.currentTimeMillis()-startTime, (String)options.get(CONSTANTS.OUTPUT));
			// Print solution on the screen (optional)
			
			SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
			// Draw solution on the screen (optional)
			//new GraphStreamViewer(vrp, solution).labelWith(Label.ID).setRenderDelay(10).display();
			
		}
	}

	private static void setTimeLimit(VehicleRoutingAlgorithm vra, long timeMilliSec) {
		TimeTermination tterm = new TimeTermination(timeMilliSec);
		vra.setPrematureAlgorithmTermination(tterm);
		vra.addListener(tterm);
	}
	
	public static double calcolatore(VehicleRoutingProblemSolution solution, SolutionAnalyser analyzer) {
		double c = 0.0;
		double rangeCosto=0.2;
		double rangeTempo=0.2;

		double tempoavg = solution.getRoutes().stream().collect(Collectors.averagingDouble(route -> route.getEnd().getArrTime()));

       for(VehicleRoute r : solution.getRoutes()){
			c += analyzer.getVariableTransportCosts(r);
			c += analyzer.getFixedCosts(r);
       }
		if(Main.secondPart){
			//bilanciamento del costo
            double avgCosto=c/solution.getRoutes().size();
//              double avgCosto=solution.getRoutes().stream().map(r->stateManager.getRouteState(r, InternalStates.COSTS, Double.class)).min(Comparator.comparing(s-> s)).get();
            c+=solution.getRoutes().stream().collect(Collectors.summingDouble(r->{
            	double costoR=analyzer.getVariableTransportCosts(r);
            	if(costoR>(avgCosto+rangeCosto))
            		return (costoR-avgCosto)*tempoavg/avgCosto*4;
            	else if(costoR<(avgCosto-rangeCosto))
            		return (avgCosto-costoR)*tempoavg/avgCosto*4;
            	else
            		return 0.0;
            }));

          //bilanciamento tempo
			c+=solution.getRoutes().stream()
					.map(s->s.getActivities().stream()
						.mapToDouble(n ->n.getEndTime()).max())
					.collect(Collectors.summingDouble(
							t->{
								if(t.isPresent())
								{
									if(t.getAsDouble()>(tempoavg+rangeTempo)){
										return (t.getAsDouble()-tempoavg)*4;
									}else 
										if(t.getAsDouble()<(tempoavg-rangeTempo)){
											return (tempoavg-t.getAsDouble())*4;
										}else
										return 0.0;
								}	
					
								else return 500.0;
							}
							));
			
		}
		
        c += solution.getUnassignedJobs().size() *1000000 ;
        c += Math.abs(solution.getRoutes().size() -Main.numVehicles)*1000000;
        
		return c;
	}

}
	
