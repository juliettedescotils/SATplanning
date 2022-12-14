package fr.uga.pddl4j.examples.asp;

import com.sun.tools.jconsole.JConsoleContext;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.*;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.util.BitVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.core.VecInt;
import org.sat4j.pb.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.*;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;
import static java.lang.System.setOut;


/**
 * The class is an example. It shows how to create a simple A* search planner able to
 * solve an ADL problem by choosing the heuristic to used and its weight.
 *
 * @author D. Pellier
 * @version 4.0 - 30.11.2021
 */
@CommandLine.Command(name = "ASP",
        version = "ASP 1.0",
        description = "Solves a specified planning problem using A* search strategy.",
        sortOptions = false,
        mixinStandardHelpOptions = true,
        headerHeading = "Usage:%n",
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n")


public class ASP extends AbstractPlanner {

    /**
     * The class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ASP.class.getName());
    /**
     * Instantiates the planning problem from a parsed problem.
     *
     * @param problem the problem to instantiate.
     * @return the instantiated planning problem or null if the problem cannot be instantiated.
     */
    @Override
    public Problem instantiate(DefaultParsedProblem problem) {
        final Problem pb = new DefaultProblem(problem);
        pb.instantiate();
        return pb;
    }

    /**
     * Search a solution plan to a specified domain and problem using A*.
     *
     * @param problem the problem to solve.
     * @return the plan found or null if no plan was found.
     */
    @Override

    public Plan solve(final Problem problem) {

        Plan plan = new SequentialPlan();
        //Soit problem notre probl??me pddl.


        //Ici, on travaille sur les fluents du probl??me.
        List<Fluent> problemFluents = problem.getFluents(); //On r??cup??re tous les fluents
        int numberOfFluents = problemFluents.size(); //On r??cup??re leur nombre



        //Ici, on travaille sur l'??tat initial du probl??me
        InitialState initialState = problem.getInitialState(); //On r??cup??re l'??tat initial du probl??me pddl
        BitVector initialStatePositiveFluents = initialState.getPositiveFluents(); //On r??cup??re les fluents positifs

        int[] initialStateFluentsSign = new int[numberOfFluents]; //On initialise le vecteur des fluents, sous forme de tableau d'entiers
        for(int i=0; i<initialStateFluentsSign.length; i++){ //On les initialise tous ?? false (sous forme d'entiers n??gatifs)
            initialStateFluentsSign[i] = -(i+1); //on incr??mente tous les entiers de 1 pour ne pas avoir de fluent de symbole 0.
        }


        int[] initialPosFluents = initialStatePositiveFluents.stream().toArray(); //On r??cup??re les fluents positifs sous la forma d'array depuis le bitvector
        for(int i=0; i<initialPosFluents.length ;i++){
            initialStateFluentsSign[initialPosFluents[i]] = - initialStateFluentsSign[initialPosFluents[i]]; //on prend l'oppos?? des fluents positifs (ie on les met ?? true)
        }


        //Ici, on travaille sur le but du probl??me
        Goal goalState = (Goal) problem.getGoal(); //on r??cup??re le but

        BitVector goalStatePositiveFluents = goalState.getPositiveFluents(); //on r??cup??re de mani??re analogue les fluents positifs du but

        int[] goalStateFluentsSign = new int[numberOfFluents];

        for(int i=0; i<goalStateFluentsSign.length ;i++){ //on initialise tous les fluents ?? false
            goalStateFluentsSign[i] = -(i+1);
        }

        int[] goalPosFluents = goalStatePositiveFluents.stream().toArray();
        for(int i=0; i<goalPosFluents.length; i++){
            goalStateFluentsSign[goalPosFluents[i]] = -goalStateFluentsSign[goalPosFluents[i]]; //on initialise les bons ?? true
        }


        //Ici, on travaille sur les actions
        List<Action> problemActions = problem.getActions();

        //on cr??e un vecteur contenant pour chaque action ses pr??conditions, ses effets positifs et n??gatifs
        int[][][] vectorActions = new int[problemActions.size()][3][]; // pour avoir un tableau de la forme [[[preconditions],[effets+],[effets-]],[[preconditions],[effets+],[effets-]],...]
        int[][] pivot = new int[3][]; //cela correspond ?? [[preconditions],[effets+],[effets-]]

        int numberOfAction = 0; //on initialise un compteur

        for(Action problemAction: problemActions){
            //on r??cup??re les pr??conditions
            pivot[0] = problemAction.getPrecondition().getPositiveFluents().stream().toArray();
            //on r??cup??re les effets positifs
            pivot[1] = problemAction.getConditionalEffects().get(0).getEffect().getPositiveFluents().stream().toArray();
            //on r??cup??re les effets n??gatifs
            pivot[2] = problemAction.getConditionalEffects().get(0).getEffect().getNegativeFluents().stream().toArray();



            for(int j =0; j<pivot[0].length;j++){
                pivot[0][j] = pivot[0][j]+1; //on incr??mente tous les symboles 1 pour ne pas se retrouver avec un fluent de symbole 0
            }
            for(int j =0; j<pivot[1].length;j++){
                pivot[1][j] = pivot[1][j]+1; //idem
            }
            //on rajoute un signe - devant les effets n??gatifs
            for(int j =0; j<pivot[2].length;j++){
                pivot[2][j] = -(pivot[2][j]+1); //on incr??mente ??galement de 1 avant de r??aliser le changement de signe
            }

            vectorActions[numberOfAction][0] = pivot[0]; //on les ins??re tels quels dans notre tableau d'actions (pr??conditions)
            vectorActions[numberOfAction][1] = pivot[1]; //de m??me (effets positifs)
            vectorActions[numberOfAction][2] = pivot[2]; //de m??me (effets n??gatifs)

            numberOfAction++; //on incr??mente notre compteur
        }



        //on calcule n, ie le nombre maximum d'??tapes du plan
        double n = 0;
        int D = problem.getConstantSymbols().size();
        int Ap = 0;
        for(Fluent flu : problemFluents ){
            if(Ap < flu.getArguments().length){
                Ap = flu.getArguments().length;
                //System.out.println(Ap);
            }
        }

        //n = Math.pow(2,Math.pow(D,Ap));



        final int maxVar = 1000000;
        final int nbClauses = numberOfAction;

        ISolver solver = SolverFactory.newDefault();

        //on ajoute la clause associ??e ?? l'??tat initial et ?? l'??tat but
        try {
            solver.addClause(new VecInt(initialStateFluentsSign)); // adapt Array to IVecInt
            solver.addClause(new VecInt(goalStateFluentsSign));
        } catch (ContradictionException e) {
            throw new RuntimeException(e);
        }

        List<Integer> clausePivot = new ArrayList<>(); //nous allons d??sormais cr??er nos clauses, ?? partir de notre vecteur d'actions

        for (int i=0;i<nbClauses;i++) {
            //ici, on cr??e la clause qui va bien
            //pour mod??liser une possibilit?? d'action (ie une transition possible), on va cr??er les clauses

            //Pour une transition d'une ??tape i-1 ?? une ??tape i :

            //on encode l'??tape i-1 :

            //on concat??ne les (pr??conditions)+ ?? true , les (effets n??gatifs)+ ?? true, les (effets positifs)- ?? false
            //explication 1: les pr??conditions sont n??cessaires aux actions -> les entiers associ??s aux fluents sont positifs
            //explication 2: les effets qui sont n??gatifs ?? l'??tape i sont  ?? l'??tape i-1 en positif -> on ne pourrait pas les passer en n??gatif sinon
            //explication 3: vice versa pour les effets positifs ?? l'??tape i -> on ne saurait ajouter ce qui est d??j?? pr??sent


            //Rappel : vectorActions est ?? 3 dimensions
            //Pour chaque ??l??ment : il y a 3 vecteurs d'entiers : [[[preconditions],[effets+],[effets-]]]

            //on parcourt le premier vecteur de pr??conditions :
            for(int prec : vectorActions[i][0]){
                clausePivot.add(prec);
            }

            //on parcourt de m??me le vecteur des effets positifs
            for(int pos : vectorActions[i][1]){
                clausePivot.add(-pos); //les effets positifs ?? l'??tape i sont n??gatifs ?? l'??tape i-1
            }

            //puis le vecteur des effets n??gatifs
            for(int neg : vectorActions[i][2]){
                clausePivot.add(-neg); //les effets n??gatifs ?? l'??tape i sont positifs ?? l'??tape i-1
            }


            int[] deuxiemePivot = new int[clausePivot.size()]; //Ce second pivot nous permet de placer notre clause dans un tableau ?? une dimension
            for(int j=0;j<clausePivot.size();j++){
                deuxiemePivot[j] = clausePivot.get(j);
            }

            try {
                solver.addClause(new VecInt(deuxiemePivot)); // adapt Array to IVecInt
            } catch (ContradictionException e) {
                throw new RuntimeException(e);
            }

            clausePivot.clear(); //on r??initialise notre liste pour l'??tape suivante
        }

        try {
            if (solver.isSatisfiable()) {
                System.out.println("Satisfiable !");
                for(int num : solver.findModel()){
                    System.out.println(num);
                }

            } else {
                System.out.println("Unsatisfiable !");
            }
        } catch (TimeoutException e) {
            System.out.println("Timeout, sorry!");
        }


        return null;
    }

    /**
     * The main method of the <code>ASP</code> planner.
     *
     * @param args the arguments of the command line.
     */
    public static void main(String[] args) {
        try {
            final ASP planner = new ASP(); //on instancie notre planner
            //final HSP planner = new HSP(); // -> on instancie le planner HSP (utilis?? plus tard pour nos graphiques de statistiques sur 4 diff??rents types de probl??mes)

            CommandLine cmd = new CommandLine(planner);

            cmd.execute("ressources_pddl/domain_blocks.pddl","ressources_pddl/blocks_p001.pddl");
            //cmd.execute("ressources_pddl/domain_logistics.pddl","ressources_pddl/logistics_p01.pddl");
            //cmd.execute("ressources_pddl/domain_depots.pddl","ressources_pddl/depots_p01.pddl");
            //cmd.execute("ressources_pddl/domain_gripper.pddl","ressources_pddl/gripper_p01.pddl");
        } catch (IllegalArgumentException e) {
            LOGGER.fatal(e.getMessage());
        }
    }
}

