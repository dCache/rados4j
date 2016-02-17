package org.dcache.rados4j;

import com.google.common.base.MoreObjects;
import java.nio.charset.StandardCharsets;
import jnr.ffi.Struct;

public class RbdImageInfo extends Struct {

    public final Unsigned64 obj_size = new Unsigned64();
    public final Unsigned64 num_objs = new Unsigned64();
    public final Unsigned32 order = new Unsigned32();
    public final Signed8[] block_name_prefix = array(new Signed8[24]);
    public final Unsigned64 parent_pool = new Unsigned64();
    public final Signed8[] parent_name = array(new Signed8[96]);

    public RbdImageInfo(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    @Override
    public java.lang.String toString() {
        return MoreObjects.toStringHelper("RbdImageInfo")
                .add("obj_size", obj_size.get())
                .add("num_obj", num_objs.get())
                .add("order", order.get())
                .add("parent_pool", parent_pool.get())
                .add("block_name_prefix", toString(block_name_prefix))
                .add("parent_name", toString(parent_name))
                .toString();
    }

    private java.lang.String toString(Signed8[] data) {
        byte[] b = new byte[data.length];
        int len = 0;

        int offset = data.length - 1;
        for ( ;offset >= 0; offset--) {
            byte c = data[offset].get();

            if (c == (byte)0) {
                break;
            }
            b[offset] = c;
            len++;
        }
        return new java.lang.String(b, offset + 1, len, StandardCharsets.UTF_8);
    }
}
