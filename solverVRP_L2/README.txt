In order to run correctly the jar file of the second part 
of the assignment is necessary to write three parameters. 
An example of run configuration is the following one:

java -jar solverVRP2.jar -i C101.txt 10

where the third parameter is the number of vehicle.

The run.bat file runs automatically the jar using all the 
files and a number of vehicle at +10%, +20% and +30% than 
the result found in the first part of the assignment.

In the directory there are four instancesXX.txt files, 
that associates to each file the correct number of vehicles 
and are used from the run.bat script to run properly.

instances01.txt contains for each file the number of vehicles 
found in the first part + 10%
instances02.txt contains for each file the number of vehicles 
found in the first part + 20%
instances03.txt contains for each file the number of vehicles 
found in the first part + 30%

The output file will be in /output/solutions.csv  and contains the following columns:

name of file;
cost of solution;
time of run;
number of vehicles used;
standard deviation considering serviceTime;
standard deviation considering transportCost;
average gap from average serviceTime;
average gap from average transportCost;
maximum serviceTime/avgServiceTime;
minimum serviceTime/avgServiceTime;
maximum transportCost/avgTransportCost;
minimum transportCost/avgTransportCost;

WARNING: the outputs of the three jar will be concatenated in
following order into ouput/solutions.csv:

results_instances01 >> results_instances02 >> results_instances03

Fabio Ferrero
S231779
Leader Workgroup 12
