package org.dcache.rados4j;

import jnr.ffi.Address;
import jnr.ffi.annotations.*;
import jnr.ffi.provider.FFIProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rados {

    private final Logger LOG = LoggerFactory.getLogger(Rados.class);

    private final LibRados libRados;
    private final jnr.ffi.Runtime runtime;

    public Rados() {
        libRados = FFIProvider.getSystemProvider()
            .createLibraryLoader(LibRados.class)
            .load("rados");

        runtime = jnr.ffi.Runtime.getRuntime(libRados);
    }

    @SuppressWarnings("PublicInnerClass")
    public interface LibRados {
      int rados_create(@Out Address cluster, @In String id);   
    }
}
