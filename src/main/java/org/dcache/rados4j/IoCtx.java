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

}
