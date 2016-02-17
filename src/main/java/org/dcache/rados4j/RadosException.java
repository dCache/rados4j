package org.dcache.rados4j;


import java.io.IOException;

public class RadosException extends IOException {

    private final int rc;
    RadosException(String string, int rc) {
        super(string);
        this.rc = rc;
    }

    public int getErrorCode() {
        return rc;
    }
}
