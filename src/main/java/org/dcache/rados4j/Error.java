package org.dcache.rados4j;

import jnr.constants.platform.Errno;

public class Error {

    private Error() {}

    public static void checkError(jnr.ffi.Runtime runtime, int rc, String msg) throws RadosException {
        if (rc < 0) {
            int errno = runtime.getLastError();
            Errno e = Errno.valueOf(-rc);
            Errno se = Errno.valueOf(errno);

            throw new RadosException(
                    String.format("%s : %s(%d), sys: %s(%d)",
                            msg, e.name(), e.intValue(),
                            se.name(), se.intValue()), rc);
        }
    }
}
