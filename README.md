# PDDL to SAT solver (Java) : 


## Our goal : 

- We tried to compare the performance of an HSP solver and a SAT solver.
To this extent, we measured first the efficiency of  an HSP solver on 4 different pddl problems, in the following benchmarks : blocks, logistics, gripper and depots.

- We used PDDL4j in order to get the performances of HSP solver.

- Thus, we needed to decode our pddl domains and problems and express them into a SAT problem that could be red by SAT4j.


## Pre-requisites in order to launch the program :

- Having Maven installed (ide such as intelliJ or NETBeans are recommended)

- Install  pddl4j (https://github.com/pellierd/pddl4j) 

- Install sat4j (https://www.sat4j.org/products.php)


## How to use the program : 

- Open SATplanning project, open the ASP.java class (at src/main/java/fr.uga.pddl4j.examples.asp/ASP) and execute the file. <br>
- To change the benchmark you want to use, uncomment the line corresponding to the wanted benchmark in the main of the program (line 269, 270, 271 or 272 ) and comment the three othe ones. <br>
- To change planner and use HSP, uncomment line 265 and comment the line 264. <br>
- After the execution the number of steps needed to resolve the problem as well as the time needed are printed in the console of your IDE. <br>

## Results :
    
### PDDL Decoding :
    
- First, we succeded to fetch the goal and initial state fluents and encode them into a SAT sate.
- We did the decoding of every of our possible actions in a suitable way for SAT modeling (preconditions, positive and negative effects), in the shape of a three-dimensionnal array.
- Finally,  we generated a SAT problem, and added our clauses.
- As we didn't succeed to generate the desired plan, we could not get the propper statistics our ASP planner.


### Graphs
- We made two python scripts allowing to generate graphs on which you can compare the two planners performances. <br>
We generated two graphs, with two scripts:
- The first one shows the performance in terms of running time of each planner for a given problem with each of the four benchmarks (blocks, logistics, gripper and depots). 
- The second one shows the number of steps each planner needs to resolve the same problem in each of the four benchmarks. <br>
Since we did not manage to find any plans with our ASP planner, the curve of the number of steps needed with the ASP planner is at 0.


Juliette Descotils & Farid Belhiteche


