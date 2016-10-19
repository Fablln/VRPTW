package main;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * 
 */
public class OROoptions {
	public enum PARAMS{	INSTANCE; }
	public enum CONSTANTS{ CONFIG, THREADS, HTML, OUTPUT, TIME, REPETITION; }

	private final String INSTANCE_ARG 	= "i";
	private final String INSTANCE_TXT 	= "Instance file (Solomon Format)";
	private final String INSTANCE_FILE	= "C101.txt";
	
	// algorithmConfig is a shrimpfgreedy_0602_0101_a01_it, a configuration with 4 strategies:
	// strategy 1 - probability 0.6: shrimpf (alpha = 0.1 and fixed initialThreshold = 150) with selectRandomly and bestInsertion
	// strategy 2 - probability 0.2: shrimpf with selectRandomly and bestInsertion (but radialRuin)
	// strategy 3 - probability 0.1: greedy with selectRandomly and regretInsertion
	// strategy 4 - probability 0.1: greedy with selectRandomly and regretInsertion (but radialRuin)
	// all with memory = 50
	private final String CONFIG_ALG_C	= "input/algorithmConfig.xml";
	private final String SOL_PATH_C		= "output/solutions.csv";
	private final String HTML_PATH_C	= "output/solution";
	private final long TIME_C			= 120*1000;
	private final int THREADS_C			= 2;
	private final int REPETITION_C		= 10;
	
	// Command line help
	private String CMD_MSG = "Available commands are:\n" +
		"-" + INSTANCE_ARG 	+": "+ INSTANCE_TXT + "\n";
	
	HashMap<String, String> options;
	EnumMap<PARAMS, String> references;
	HashMap<CONSTANTS, Object> constants;
			
	public OROoptions(String[] args) {
		options = new HashMap<String, String>();
		options.put(INSTANCE_ARG, INSTANCE_FILE);
				
		references = new EnumMap<PARAMS, String>(PARAMS.class);
		references.put(PARAMS.INSTANCE, INSTANCE_ARG);
		
		constants = new HashMap<CONSTANTS, Object>();
		constants.put(CONSTANTS.CONFIG, CONFIG_ALG_C);
		constants.put(CONSTANTS.HTML, HTML_PATH_C);
		constants.put(CONSTANTS.THREADS, THREADS_C);
		constants.put(CONSTANTS.TIME, TIME_C);
		constants.put(CONSTANTS.OUTPUT, SOL_PATH_C);
		constants.put(CONSTANTS.REPETITION, REPETITION_C);
			
		for(int i=0; i<args.length; i++){
			String arg = "";
			// Manage error in command line
			if(args[i].charAt(0) != '-'){
				System.out.println("No command exists for " + args[i]);
				System.out.println(CMD_MSG);
				System.exit(1);
			}
			// Manage double and single -
			if(args[i].substring(0,1).equals("--"))
				arg = args[i].substring(2).toLowerCase();
			else if(args[i].charAt(0) == '-')
				arg = args[i].substring(1).toLowerCase();
			// Check command
			if(!options.containsKey(arg)) {
				System.out.println("No command exists for " + args[i]);
				System.out.println(CMD_MSG);
				System.exit(1);
			}
			// Change default configuration
			options.put(arg, args[i+1]);
			i++;
		}
	}
	/**
	 * Return the value of parameter
	 * @param param the identifier of the parameter
	 * @return value of parameter
	 */
	public String get(PARAMS param) {
		return options.get(references.get(param));
	}
	/**
	 * Return the value of selected constant. 
	 * The value must be casted.
	 * @param c the idetifier of the constant
	 * @return value of constant
	 */
	public Object get(CONSTANTS c){
		return constants.get(c);
	}
}
