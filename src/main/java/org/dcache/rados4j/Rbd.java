package org.dcache.rados4j;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.provider.FFIProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Rbd {

    private final Logger LOG = LoggerFactory.getLogger(Rbd.class);

    private final LibRbd libRbd;
    private final jnr.ffi.Runtime runtime;

    private final Pointer ctx;
    Rbd(Pointer ctx) throws RadosException {

        this.ctx = ctx;
        libRbd = FFIProvider.getSystemProvider()
                .createLibraryLoader(LibRbd.class)
                .load("rbd");

        runtime = jnr.ffi.Runtime.getRuntime(libRbd);
        LOG.info("Using RBD version {}", version());
    }

    public String version() throws RadosException {
        IntByReference maj = new IntByReference();
        IntByReference min = new IntByReference();
        IntByReference extra = new IntByReference();
        int rc = libRbd.rbd_version(maj, min, extra);
        checkError(rc, "Failed to get version number");
        return maj.intValue() + "." + min.intValue() + "." + extra.intValue();
    }

    private void checkError(int rc, String msg) throws RadosException {
        if (rc < 0) {
            int errno = runtime.getLastError();
            LOG.error("{} : {}, errno {}", msg, rc, errno);
            throw new RadosException(msg + ": " + rc + " errno " + errno);
        }
    }

    @SuppressWarnings("PublicInnerClass")
    public interface LibRbd {
        int rbd_version(@Out IntByReference maj, @Out IntByReference min, @Out IntByReference extra);
    }
}