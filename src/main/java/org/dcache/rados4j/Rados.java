package org.dcache.rados4j;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.*;
import jnr.ffi.byref.PointerByReference;
import jnr.ffi.provider.FFIProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rados {

    private final Logger LOG = LoggerFactory.getLogger(Rados.class);

    private final LibRados libRados;
    private final jnr.ffi.Runtime runtime;
    private final Pointer _cluster;

    /**
     * Create new instance of Rados with given {@code id} and {@code configFile}.
     * @param id cluster id.
     * @param configFile path to config file.
     * @throws RadosException
     */
    public Rados(String id, String configFile) throws RadosException {
        libRados = FFIProvider.getSystemProvider()
            .createLibraryLoader(LibRados.class)
            .load("rados");

        runtime = jnr.ffi.Runtime.getRuntime(libRados);

        PointerByReference clusterPtr = new PointerByReference();

        int rc;
        rc = libRados.rados_create(clusterPtr, id);
        checkError(rc, "Failed to create cluster");

        _cluster = clusterPtr.getValue();
        rc = libRados.rados_conf_read_file(_cluster, configFile);
        checkError(rc, "Failed to read config file");
    }

    public void connect() throws RadosException {
        int rc = libRados.rados_connect(_cluster);
        checkError(rc, "Failed to connect to cluster");
    }

    private void checkError(int rc, String msg) throws RadosException {
        if (rc < 0) {
            int errno = runtime.getLastError();
            LOG.error("{} : {}, errno {}", msg, rc, errno);
            throw new RadosException( msg + ": " + rc + " errno " + errno);
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public interface LibRados {
      int rados_create(@Out PointerByReference cluster, @In String id);
      int rados_conf_read_file(@In Pointer cluster, @In String config);
      int rados_connect(@In Pointer cluster);
    }
}
