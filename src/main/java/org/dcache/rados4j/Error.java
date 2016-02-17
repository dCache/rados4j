package org.dcache.rados4j;

public class Error {

    private Error() {}

    public static void checkError(jnr.ffi.Runtime runtime, int rc, String msg) throws RadosException {
        if (rc < 0) {
            int errno = runtime.getLastError();
            throw new RadosException(msg + ": " + rc + " errno " + errno);
        }
    }
}
