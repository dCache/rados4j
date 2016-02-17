package org.dcache.rados4j;


import java.io.IOException;

public class RadosException extends IOException {

    RadosException(String string) {
        super(string);
    }
}
