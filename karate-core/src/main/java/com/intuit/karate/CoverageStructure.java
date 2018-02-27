package com.intuit.karate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CoverageStructure {
    public String             className;
    public String             functionName;
    public int                noOfBranches;
    public ArrayList<Integer> branches; // array containing id's of each branch
    Path path;

    CoverageStructure(String className, String functionName, int noOfBranches) {
        this.className = className;
        this.functionName = functionName;
        this.noOfBranches = noOfBranches;
        try {
            createFile();
            //System.out.println("Saving output to " + path.toString());
            branches = readBranchLog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // creates a file in the system's temporary directory
    // template: path/to/tmp/coverage-className_functionName
    private void createFile() throws IOException {
        File parent = new File(System.getProperty("java.io.tmpdir"));

        File temp = new File(parent, "coverage-" + className + "_" + functionName);
        this.path = temp.toPath();

        if (!temp.exists()) {
            temp.createNewFile();
        }
    }

    public void writeBranchLog() {
        ArrayList<Integer> notReached = IntStream.rangeClosed(0, noOfBranches - 1)
                                                .filter(n -> !branches.contains(n))
                                                .boxed()
                                                .collect(Collectors.toCollection(ArrayList<Integer>::new));

        try (BufferedWriter outputWriter = Files.newBufferedWriter(path)) {
            outputWriter.write(branches.toString());
            outputWriter.newLine();
            outputWriter.write(branches.size() + " of " + noOfBranches);
            outputWriter.newLine();
            outputWriter.write(notReached.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList readBranchLog() throws IOException {
        BufferedReader reader = null;
        reader = Files.newBufferedReader(path);

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("[\\[\\]\\s]", "");

            ArrayList<Integer> vals = Arrays.stream(line.trim().split(","))
                                              .mapToInt(Integer::parseInt)
                                              .boxed()
                                              .sorted()
                                              .collect(Collectors.toCollection(ArrayList<Integer>::new));
            return vals != null ? vals : new ArrayList<Integer>();
        }

        return new ArrayList<Integer>();
    }

    /**
     * Add a branch to the structure. If branch already exists do nothing.
     *
     * @param id - id of the branch
     * @return - true if unique id, false if not
     */
    public boolean addBranch(int id) {
        if (!branches.contains(id)) {
            branches.add(id);
            Collections.sort(branches);
            writeBranchLog();
            return true;
        } else {
            return false;
        }
    }

    public void printBranches() {
        System.out.println("==========COVERAGE===========");
        System.out.printf("Reached %d out of %d branches.\n", branches.size(), noOfBranches);
        System.out.println("===Showing reached branches===");
        for (int i = 0; i < branches.size(); i++)
            System.out.printf("Branch id %d reached.\n", branches.get(i));
    }

}
