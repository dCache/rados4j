package org.dcache.rados4j;

import jnr.ffi.Pointer;

import static org.dcache.rados4j.Error.checkError;

public class RbdImage {

    private final Pointer image;
    private final Rbd.LibRbd rbd;
    private final jnr.ffi.Runtime runtime;
    private final String name;

    public RbdImage(Pointer image, String name, Rbd.LibRbd rbd, jnr.ffi.Runtime runtime) {
        this.image = image;
        this.rbd = rbd;
        this.runtime = runtime;
        this.name = name;
    }

    public void close() throws RadosException {
        int rc = rbd.rbd_close(image);
        checkError(runtime, rc, "Failed to close image " + name);
    }
}
