package com.apiframework.common.logging;

import org.apache.log4j.Level;

public class FailLevel extends Level {

    public static final int FAIL_INT = ERROR_INT;
    public static final Level FAIL = new FailLevel(FAIL_INT,"FAIL", 10);


    protected FailLevel(int leveliNTvalue, String name, int agr) {
        super(leveliNTvalue, name, agr);
    }
}
