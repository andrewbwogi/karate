package com.intuit.karate;

import java.util.ArrayList;

public class CoverageStructure {

    public String functionName;
    public int noOfBranches;
    public ArrayList<Integer> branches; // array containing id's of each branch

    CoverageStructure(String functionName, int noOfBranches) {
        this.functionName = functionName;
        this.noOfBranches = noOfBranches;
    }

    public int addBranch(int id) {
        if(!branches.contains(id)) {
            branches.add(id);
            return 1;
        } else {
            return -1;
        }
    }

    public void printBranches() {
        System.out.printf("Reached %d out of %d branches.\n", branches.size(), noOfBranches);
        System.out.println("Coverage: " + (branches.size()/noOfBranches) + "%");
        System.out.println("===Showing reached branches===");
        for(Integer id : branches)
            System.out.printf("Branch id %d reached.\n", id);
        System.out.println("\n\n\n===Showing unreached branches===");
        for(int i = 0; i < noOfBranches; i++){
            if(!branches.contains(i))
                System.out.printf("Branch id %d not reached.\n", i);
        }
    }
}
