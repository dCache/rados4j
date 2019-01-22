package org.dcache.rados4j;

import java.nio.ByteBuffer;
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

    public int write(byte[] data, long offset, int length) throws RadosException {
        return this.write(ByteBuffer.wrap(data), offset);
    }

    public int read(byte[] data, long offset, int length) throws RadosException {
        return this.read(ByteBuffer.wrap(data), offset);
    }

    public int write(ByteBuffer buf, long offset) throws RadosException {
        int rc = rbd.rbd_write(image, offset, buf.remaining(), buf);
        checkError(runtime, rc, "Failed to write into image " + name);
        // JNI interface does not updates the position
        buf.position( buf.position() + rc);
        return rc;
    }

    public int read(ByteBuffer buf, long offset) throws RadosException {
        int rc = rbd.rbd_read(image, offset, buf.remaining(), buf);
        checkError(runtime, rc, "Failed to read from image " + name);
        // JNI interface does not updates the position
        buf.position( buf.position() + rc);
        return rc;
    }

    public void resize(long newSize) throws RadosException {
        int rc = rbd.rbd_resize(image, newSize);
        checkError(runtime, rc, "Failed to resize image " + name);
    }

    public RbdImageInfo stat() throws RadosException {
        RbdImageInfo info = new RbdImageInfo(runtime);
        int rc = rbd.rbd_stat(image, info, 0);
        checkError(runtime, rc, "Failed to stat image " + name);
        return info;
    }

    public void flush() throws RadosException {
        int rc = rbd.rbd_flush(image);
        checkError(runtime, rc, "Failed to flush image " + name);
    }
}
