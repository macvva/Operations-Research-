/*******************************************************************************
 * Maciej Nowaczyk
 * Date: 14-04-2020
 * This code is my own work, it was developed without using or copying code
 * from other students or other resources.
 ******************************************************************************/


import ilog.concert.*;
import ilog.cplex.IloCplex;

public class Sodaco_model{

    public static void solveSodacoProblem(){
        try{
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);

            int numVariables = 6;
            int brandConstraints = 3;
            int constraintsProp=7;


            // define parameters
            int[] cost = {4,3,2,-3,-2,-1};
            int[] minimumValueComp={0,0,0,0,0,0,0};
            int[] maximumImportBrand = {2000,4000,1000};
            double [][] composition = { {1, 0, 0,-0.8,0,0}, {-1,0,0,0,0,0.2}, {0,1,0,-0.2,0,0}, {0,-1,0,0,0,0.8}, {0,0,1,0,0,0},
                    {0,0,-1,0,0,0.7}, {-1,-1,-1,1,1,1} };

            // declare variable array:   integer variables with lower bound 0 and upper bound 10000
            IloNumVar[] variables = cplex.numVarArray(numVariables,0,4000, IloNumVarType.Int);

            // add objective function
            IloLinearNumExpr expr1 = cplex.linearNumExpr();
            for (int i = 0; i < numVariables; i++) {
                expr1.addTerm(cost[i],variables[i]);
            }
            cplex.addMaximize(expr1);

            IloRange[] listOfCons= new IloRange[brandConstraints];
            for(int i=0; i< brandConstraints; i++){
                listOfCons[i]=cplex.addLe(variables[i+3], maximumImportBrand[i]);
            }


            // add max number of import brands
            cplex.addLe(variables[3], maximumImportBrand[0]);
            cplex.addLe(variables[4], maximumImportBrand[1]);
            cplex.addLe(variables[5], maximumImportBrand[2]);
            //add constraints for proportions regarding the products
            for(int j=0; j < constraintsProp; j++){
                IloLinearNumExpr expr2 = cplex.linearNumExpr();
                for (int i = 0; i < numVariables; i++){
                    expr2.addTerm(variables[i], composition[j][i]);
                }
                cplex.addGe(expr2,minimumValueComp[j]);
            }
            //export
            cplex.exportModel("model.lp");

            // solve ILP
            cplex.solve();

            // output solution
            System.out.println("Optimal value  = " + cplex.getObjValue());
            //slack only for the brand constraints, because amount of products were not bounded
            System.out.println("Slack for the first constraint: "+cplex.getSlack(listOfCons[0]));
            System.out.println("Slack for the second constraint: "+cplex.getSlack(listOfCons[1]));
            System.out.println("Slack for the third constraint: "+cplex.getSlack(listOfCons[2]));

            for (int i = 0; i <numVariables ; i++) {
                System.out.println(cplex.getValue(variables[i]));
            }
            // close cplex object
            cplex.end();

        }
        catch (IloException exc) {
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) {

        solveSodacoProblem();

    }
}