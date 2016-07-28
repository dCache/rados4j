package org.dcache.rados4j;

import com.google.common.base.MoreObjects;
import jnr.ffi.Struct;

public class RadosPoolInfo extends Struct {

    public final Unsigned64 num_bytes = new Unsigned64();
    public final Unsigned64 num_kb = new Unsigned64();
    public final Unsigned64 num_objects = new Unsigned64();
    public final Unsigned64 num_object_clones = new Unsigned64();
    public final Unsigned64 num_object_copies = new Unsigned64();
    public final Unsigned64 num_objects_missing_on_primary = new Unsigned64();
    public final Unsigned64 num_objects_unfound = new Unsigned64();
    public final Unsigned64 num_objects_degraded = new Unsigned64();
    public final Unsigned64 num_rd = new Unsigned64();
    public final Unsigned64 num_rd_kb = new Unsigned64();
    public final Unsigned64 num_wr = new Unsigned64();
    public final Unsigned64 num_wr_kb = new Unsigned64();

    public RadosPoolInfo(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    @Override
    public java.lang.String toString() {
        return MoreObjects.toStringHelper("RadosPoolInfo")
                .add("num_bytes", num_bytes.get())
                .add("num_kb", num_kb.get())
                .add("num_objects", num_objects.get())
                .add("num_object_clones", num_object_clones.get())
                .add("num_object_copies", num_object_copies.get())
                .add("num_objects_missing_on_primary", num_objects_missing_on_primary.get())
                .add("num_objects_unfound", num_objects_unfound.get())
                .add("num_objects_degraded", num_objects_degraded.get())
                .add("num_rd", num_rd.get())
                .add("num_rd_kb", num_rd_kb.get())
                .add("num_wr", num_wr.get())
                .add("num_wr_kb", num_wr_kb.get())
                .toString();
    }

}
