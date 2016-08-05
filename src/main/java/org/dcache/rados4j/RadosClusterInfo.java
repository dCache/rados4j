package org.dcache.rados4j;

import com.google.common.base.MoreObjects;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class RadosClusterInfo extends Struct {

    public final Unsigned64 kb = new Unsigned64();
    public final Unsigned64 kb_used = new Unsigned64();
    public final Unsigned64 kb_avail = new Unsigned64();
    public final Unsigned64 num_objects = new Unsigned64();

    public RadosClusterInfo(Runtime runtime) {
        super(runtime);
    }

    @Override
    public java.lang.String toString() {
        return MoreObjects.toStringHelper("RadosClusterInfo")
                .add("kb", kb.get())
                .add("kb_used", kb_used.get())
                .add("kb_avail", kb_avail.get())
                .add("num_objects", num_objects.get())
                .toString();
    }
}
