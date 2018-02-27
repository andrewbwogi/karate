package com.intuit.karate;

import java.util.ArrayList;

/**
 * Usage:
 * 1. Call CoverageStructure.setCoverageStructure() in the beginning of the test function.
 * 2. Set out CoverageStructure.addBranch() in every branch of the tested function.
 * 3. Call CoverageStructure.printBranches() in the end of the test function.
 */
public class CoverageStructure {

    public static String functionName;
    public static int noOfBranches;
    public static ArrayList<Integer> branches; // array containing id's of each branch

    public static void setCoverageStructure(String functionNameIn, int noOfBranchesIn) {
        functionName = functionNameIn;
        noOfBranches = noOfBranchesIn;
        branches = new ArrayList<>();
        System.out.println("*********Now covering: " + functionName + "*********");
    }

    public static int addBranch(int id) {
        if(!branches.contains(id)) {
            branches.add(id);
            return 1;
        } else {
            return -1;
        }
    }

    public static void printBranches() {
        System.out.printf("Reached %d out of %d branches.\n", branches.size(), noOfBranches);
        if(noOfBranches != 0)
            System.out.println("Coverage: " + (branches.size()/noOfBranches) + "%\n");
        else
            System.out.println("No Branches");
        System.out.println("===Showing reached branches===");
        for(Integer id : branches)
            System.out.printf("Branch id %d reached.\n", id);
        System.out.println("\n===Showing unreached branches===");
        for(int i = 0; i < noOfBranches; i++){
            if(!branches.contains(i))
                System.out.printf("Branch id %d not reached.\n", i);
        }
    }
}
