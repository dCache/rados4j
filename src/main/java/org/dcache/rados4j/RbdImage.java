package org.dcache.rados4j;

import jnr.ffi.Pointer;

import static org.dcache.rados4j.Error.checkError;

public class RbdImage implements AutoCloseable {

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

    @Override
    public void close() throws RadosException {
        int rc = rbd.rbd_close(image);
        checkError(runtime, rc, "Failed to close image " + name);
    }

    public void write(byte[] data, long offset, int length) throws RadosException {
        int rc = rbd.rbd_write(image, offset, length, data);
        checkError(runtime, rc, "Failed to write into image " + name);
    }

    public int read(byte[] data, long offset, int length) throws RadosException {
        int rc = rbd.rbd_read(image, offset, length, data);
        checkError(runtime, rc, "Failed to read from image " + name);
        return rc;
    }

    public RbdImageInfo stat() throws RadosException {
        RbdImageInfo info = new RbdImageInfo(runtime);
        int rc = rbd.rbd_stat(image, info, 0);
        checkError(runtime, rc, "Failed to stat imoge " + name);
        return info;
    }
}
