package org.dcache.rados4j;


import org.junit.Test;

public class RadosTest {

    @Test
    public void testInit() throws RadosException {
        Rados rados = new Rados("admin", "/etc/ceph/ceph.conf");
    }

    @Test
    public void testConnect() throws RadosException {
        Rados rados = new Rados("admin", "/etc/ceph/ceph.conf");
        rados.connect();
    }

}

