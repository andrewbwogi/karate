package com.intuit.karate;

import java.util.ArrayList;

/**
 * Usage:
 * 1. Call CoverageStructure.setCoverageStructure() in the beginning of the test function.
 * 2. Set out CoverageStructure.addBranch(id) in every branch of the tested function.
 * The id specifies the id of the covered branch.
 * 3. Call CoverageStructure.printBranches() in the end of the test function.
 *
 * Alternative usage:
 * 1. Set out CoverageStructure.addBranch(id, funcId) in every branch of the tested function.
 * The id specifies the id of the covered branch. The funcId specifies the id of the method covered.
 * 2. Call first CoverageStructure.setCoverageStructure() and then CoverageStructure.printBranches()
 * with the desired function id that represents the covered method. This alternative usage is for covering
 * methods that are called from several different test suites. These last calls should be called after every test
 * suit relevant to the method has been called.
 *
 * The configuration and printing of the branch coverage is made in the following test classes:
 *
 * JsonUtils::setValueByPath in JsonUtilsTest::testSetByPath
 * ScriptValue::getAsString is untested in the original project
 * StepDefs::toMatchType in WDIYCoverageTest::testPrint
 * ScriptContext::configure in WDIYCoverageTest::testPrint
 * ScriptContext::ScriptContext in WDIYCoverageTest::testPrint
 */
public class CoverageStructure {

    public static String functionName;
    public static int noOfBranches;
    public static ArrayList<Integer> branches = new ArrayList<>(); // array containing id's of each branch
    public static int[][] branchesArray = new int[10][100];
    public static int[] noOfBranchesArray = new int[10];

    public static void setCoverageStructure(String functionNameIn, int noOfBranchesIn) {
        functionName = functionNameIn;
        noOfBranches = noOfBranchesIn;
        branches = new ArrayList<>();
        System.out.println("\n\n\n*********Now covering: " + functionName + "*********");
    }

    public static void setCoverageStructure(String functionNameIn, int noOfBranchesIn, int funcId) {
        functionName = functionNameIn;
        noOfBranchesArray[funcId] = noOfBranchesIn;
        System.out.println("\n\n\n*********Now covering: " + functionName + "*********");
    }

    public static int addBranch(int id) {
        if(!branches.contains(id)) {
            branches.add(id);
            return 1;
        } else {
            return -1;
        }
    }

    public static void addBranch(int id, int funcId) {
        branchesArray[funcId][id] = 1;
    }

    public static void printBranches() {
        System.out.printf("Reached %d out of %d branches.\n", branches.size(), noOfBranches);
        System.out.println("===Showing reached branches===");
        for(Integer id : branches)
            System.out.printf("Branch id %d reached.\n", id);
        System.out.println("\n===Showing unreached branches===");
        for(int i = 1; i < noOfBranches+1; i++){
            if(!branches.contains(i))
                System.out.printf("Branch id %d not reached.\n", i);
        }
    }

    public static void printBranches(int funcId) {
        int count = 0;
        for(int i = 0; i<noOfBranchesArray[funcId]; i++){
            if(branchesArray[funcId][i] == 1)
                count++;
        }
        System.out.printf("Reached %d out of %d branches.\n", count, noOfBranchesArray[funcId]);
        System.out.println("===Showing reached branches===");
        for(int i = 1; i<noOfBranchesArray[funcId]+1; i++){
            if(branchesArray[funcId][i] == 1)
                System.out.printf("Branch id %d reached.\n", i);
        }
        System.out.println("\n===Showing unreached branches===");
        for(int i = 1; i<noOfBranchesArray[funcId]+1; i++){
            if(branchesArray[funcId][i] == 0)
                System.out.printf("Branch id %d not reached.\n", i);
        }
    }
}
