package org.dcache.rados4j;

import jnr.ffi.Pointer;

import static org.dcache.rados4j.Error.checkError;

public class IoCtx {

    private final Pointer ctx;
    private final Rados.LibRados rados;
    private final jnr.ffi.Runtime runtime;

    public IoCtx(Pointer ctx, Rados.LibRados rados, jnr.ffi.Runtime runtime) {
        this.ctx = ctx;
        this.rados = rados;
        this.runtime = runtime;
    }

    public void destroy() throws RadosException {
        int rc = rados.rados_ioctx_destroy(ctx);
        checkError(runtime, rc, "Failed to destroy io context");
    }

    public Rbd createRbd() throws RadosException {
        return new Rbd(ctx);
    }

    Pointer pointer() {
        return ctx;
    }

    public void setXattr(String obj, String name, byte[] data) throws RadosException {
        int rc = rados.rados_setxattr(ctx, obj, name, data, data.length);
        checkError(runtime, rc, "Failed to set xattr " + name);
    }

    public int getXattr(String obj, String name, byte[] data) throws RadosException {
        int rc = rados.rados_getxattr(ctx, obj, name, data, data.length);
        checkError(runtime, rc, "Failed to get xattr " + name);
        return rc;
    }

    public void rmXattr(String obj, String name) throws RadosException {
        int rc = rados.rados_rmxattr(ctx, obj, name);
        checkError(runtime, rc, "Failed to remove xattr " + name);
    }

}
