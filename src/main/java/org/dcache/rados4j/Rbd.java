package org.dcache.rados4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import jnr.constants.platform.Errno;
import jnr.ffi.Pointer;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.LongLongByReference;
import jnr.ffi.byref.PointerByReference;
import jnr.ffi.provider.FFIProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.dcache.rados4j.Error.checkError;

public class Rbd {

    private static final Logger LOG = LoggerFactory.getLogger(Rbd.class);

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
        checkError(runtime, rc, "Failed to get version number");
        return maj.intValue() + "." + min.intValue() + "." + extra.intValue();
    }

    public void create(String name, long size) throws RadosException {
        IntByReference order = new IntByReference(0);
        int rc = libRbd.rbd_create(ctx, name, size, order);
        checkError(runtime, rc, "Failed to create image " + name);
    }

    public RbdImage open(String name) throws RadosException {
        PointerByReference imagePtr = new PointerByReference();
        int rc = libRbd.rbd_open(ctx, name, imagePtr, null);
        checkError(runtime, rc, "Failed to open image " + name);
        return new RbdImage(imagePtr.getValue(), name, libRbd, runtime);
    }

    public RbdImage openReadOnly(String name) throws RadosException {
        PointerByReference imagePtr = new PointerByReference();
        int rc = libRbd.rbd_open_read_only(ctx, name, imagePtr, null);
        checkError(runtime, rc, "Failed to (ro)open image " + name);
        return new RbdImage(imagePtr.getValue(), name, libRbd, runtime);
    }

    public void remove(String name) throws RadosException {
        int rc = libRbd.rbd_remove(ctx, name);
        checkError(runtime, rc, "Failed to remove image " + name);
    }

    public Set<String> list() throws RadosException {

        LongLongByReference size = new LongLongByReference(1024); // initial buffer size
        byte[] names;

        int rc;
        while(true) {
            names = new byte[size.intValue()];
            rc = libRbd.rbd_list(ctx, names, size);
            if (rc == -Errno.ERANGE.intValue()) {
                // provided byte array is smaller than listing size
                LOG.debug("Bigger array required to get the listing: {}", size.intValue());
                continue;
            }

            if (rc >=0) {
                break;
            }

            checkError(runtime, rc, "Failed to get list of RBD images");
            throw new RuntimeException("must never get here");
        }

        /*
         * returned byte array contains image names separated by '\0'.
         * the value of rc points to actual size used in the array.
         */
        Set<String> dirList = new HashSet<>();
        int o = 0;
        int i  = 0;
        for( ; i < rc; i++) {
            if (names[i] == '\0') {
                dirList.add(new String(names, o, i - o,  StandardCharsets.UTF_8));
                o = i + 1;
            }
        }
        return dirList;
    }

    @SuppressWarnings("PublicInnerClass")
    public interface LibRbd {
        int rbd_version(@Out IntByReference maj, @Out IntByReference min, @Out IntByReference extra);
        int rbd_create(@In Pointer ctx, @In String name, @In long size, @In @Out IntByReference order);
        int rbd_open(@In Pointer ctx, @In String name, @Out PointerByReference image, @In String snap_name);
        int rbd_open_read_only(@In Pointer ctx, @In String name, @Out PointerByReference image, @In String snap_name);
        int rbd_close(@In Pointer image);
        int rbd_remove(@In Pointer ctx, @In String name);
        int rbd_write(@In Pointer image, long offset, int len, @In ByteBuffer buf);
        int rbd_read(@In Pointer image, long offset, int len, @Out ByteBuffer buf);
        int rbd_stat(@In Pointer image, @Out RbdImageInfo info, long size);
        int rbd_resize(@In Pointer image, long size);
        int rbd_list(@In Pointer ctx, @In @Out byte[] names, @In @Out LongLongByReference size);
        int rbd_flush(@In Pointer image);
    }
}