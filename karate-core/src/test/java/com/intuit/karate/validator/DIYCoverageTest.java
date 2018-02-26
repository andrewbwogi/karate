package com.intuit.karate.validator;

import com.intuit.karate.CoverageStructure;
import org.junit.Test;

public class DIYCoverageTest {

    @Test
    public void testPrint() {
        CoverageStructure.setCoverageStructure("toMatchType", 12, 1);
        CoverageStructure.printBranches(1);

    }
}
