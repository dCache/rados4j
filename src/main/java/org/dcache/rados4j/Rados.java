package org.dcache.rados4j;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.*;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.PointerByReference;
import jnr.ffi.provider.FFIProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.dcache.rados4j.Error.checkError;

public class Rados {

    private final Logger LOG = LoggerFactory.getLogger(Rados.class);

    private final LibRados libRados;
    private final jnr.ffi.Runtime runtime;
    private final Pointer cluster;

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
        checkError(runtime, rc, "Failed to create cluster");

        cluster = clusterPtr.getValue();
        rc = libRados.rados_conf_read_file(cluster, configFile);
        checkError(runtime, rc, "Failed to read config file");
        LOG.info("Using RADOS version {}", version());
    }

    public String version() throws RadosException {
        IntByReference maj = new IntByReference();
        IntByReference min = new IntByReference();
        IntByReference extra = new IntByReference();
        int rc = libRados.rados_version(maj, min, extra);
        checkError(runtime, rc, "Failed to get version number");
        return maj.intValue() + "." + min.intValue() + "." + extra.intValue();
    }

    public void connect() throws RadosException {
        int rc = libRados.rados_connect(cluster);
        checkError(runtime, rc, "Failed to connect to cluster");
    }

    public void shutdown() throws RadosException {
        int rc = libRados.rados_shutdown(cluster);
        checkError(runtime, rc, "Failed to shutdown rados");
    }

    public void createPool(String poolName) throws RadosException {
        int rc = libRados.rados_pool_create(cluster, poolName);
        checkError(runtime, rc, "Failed to create pool " + poolName);
    }

    public void deletePool(String poolName) throws RadosException {
        int rc = libRados.rados_pool_delete(cluster, poolName);
        checkError(runtime, rc, "Failed to delete pool " + poolName);
    }

    public IoCtx createIoContext(String poolName) throws RadosException {
        PointerByReference ctxPtr = new PointerByReference();
        int rc = libRados.rados_ioctx_create(cluster, poolName, ctxPtr);
        checkError(runtime, rc, "Failed to create IO context for pool " + poolName);
        return new IoCtx(ctxPtr.getValue(), libRados, runtime);
    }

    public RadosPoolInfo statPool(IoCtx ctx) throws RadosException {
        RadosPoolInfo poolInfo = new RadosPoolInfo(runtime);
        int rc = libRados.rados_ioctx_pool_stat(ctx.pointer(), poolInfo);
        checkError(runtime, rc, "Failed to get pool status");
        return poolInfo;
    }

    @SuppressWarnings("PublicInnerClass")
    public interface LibRados {

      int rados_version(@Out IntByReference maj, @Out IntByReference min, @Out IntByReference extra);
      int rados_create(@Out PointerByReference cluster, @In String id);
      int rados_conf_read_file(@In Pointer cluster, @In String config);
      int rados_connect(@In Pointer cluster);
      int rados_shutdown(@In Pointer cluster);

      int rados_pool_create(@In Pointer cluster, @In String poolName);
      int rados_pool_delete(@In Pointer cluster, @In String poolName);

      int rados_ioctx_pool_stat(@In Pointer ctx, @Out RadosPoolInfo poolInfo);
      int rados_ioctx_create(@In Pointer cluster, @In String poolName, @Out PointerByReference ctx);
      int rados_ioctx_destroy(@In Pointer ctx);

    }
}
