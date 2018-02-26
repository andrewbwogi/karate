package com.intuit.karate.validator;

import com.intuit.karate.CoverageStructure;
import org.junit.Test;

public class WDIYCoverageTest {

    @Test
    public void testPrint() {
        CoverageStructure.setCoverageStructure("toMatchType", 12, 1);
        CoverageStructure.printBranches(1);

        CoverageStructure.setCoverageStructure("configure", 24, 2);
        CoverageStructure.printBranches(2);

        CoverageStructure.setCoverageStructure("ScriptContext", 10, 3);
        CoverageStructure.printBranches(3);

    }
}
