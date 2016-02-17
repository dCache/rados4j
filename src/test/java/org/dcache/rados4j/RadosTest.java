package org.dcache.rados4j;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RadosTest {

    private String getEnvOrDefault(String env, String value) {
        String v = System.getenv(env);
        if (v == null) {
            v = value;
        }
        return v;
    }

    private String id;
    private String configFile;
    private String testPool;
    private Rados rados;


    @Before
    public void setUp() throws RadosException {
        id = getEnvOrDefault("RADOS_TEST_CLUSTER", "admin");
        configFile = getEnvOrDefault("RADOS_TEST_CONF", "/etc/ceph/ceph.conf");
        testPool = getEnvOrDefault("RADOS_TEST_POOL", "test-pool");

        rados = new Rados(id, configFile);
        rados.connect();
    }

    @After
    public void tearDown() throws RadosException {
        rados.shutdown();
    }

    @Test
    public void testCreatePool() throws RadosException {
        rados.createPool("test-pool");
        rados.deletePool("test-pool");
    }

    @Test
    public void testCreateContext() throws RadosException {
        rados.createPool("test-pool");
        IoCtx ctx = rados.createIoContext("test-pool");
        rados.deletePool("test-pool");
    }
}

