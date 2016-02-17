package org.dcache.rados4j;

import jnr.ffi.Pointer;

public class IoCtx {

    private final Pointer ctx;
    private final Rados.LibRados rados;

    public IoCtx(Pointer ctx, Rados.LibRados rados) {
        this.ctx = ctx;
        this.rados = rados;
    }

    public void destroy() {
        int rc = rados.rados_ioctx_destroy(ctx);
    }

}
