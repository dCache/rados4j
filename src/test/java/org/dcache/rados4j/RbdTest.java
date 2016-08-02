package org.dcache.rados4j;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Set;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 *
 * @author tigran
 */
public class RbdTest {

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
    private Rbd rbd;

    @Before
    public void setUp() throws RadosException {
        id = getEnvOrDefault("RADOS_TEST_CLUSTER", "admin");
        configFile = getEnvOrDefault("RADOS_TEST_CONF", "/etc/ceph/ceph.conf");
        testPool = getEnvOrDefault("RADOS_TEST_POOL", "test-pool");

        rados = new Rados(id, configFile);
        rados.connect();
        try {
            rados.deletePool(testPool);
        } catch (RadosException e) {
            // NOP
        }
        rados.createPool(testPool);
        rbd = rados.createIoContext(testPool).createRbd();
    }

    @After
    public void tearDown() throws RadosException {
        rados.deletePool(testPool);
        rados.shutdown();
    }

    @Test
    public void testCreateImage() throws RadosException {
        rbd.create("test-image", 0);
        rbd.remove("test-image");
    }

    @Test
    public void testOpenImage() throws RadosException {
        rbd.create("test-image", 0);
        RbdImage image = rbd.open("test-image");
        image.close();
        rbd.remove("test-image");
    }

    @Test
    public void testOpenReadOnlyImage() throws RadosException {
        rbd.create("test-image", 0);
        RbdImage image = rbd.openReadOnly("test-image");
        image.close();
        rbd.remove("test-image");
    }

    @Test
    public void testWriteImage() throws RadosException {
        rbd.create("test-image", 0);
        try (RbdImage image = rbd.open("test-image") ) {
            int n = 1024;
            int bufSize = 1024;
            byte[] data = new byte[bufSize];
            new Random().nextBytes(data);

            image.resize(bufSize*n);
            for(int i = 0; i < n; i++) {
                image.write(data, i*bufSize, data.length);
            }
        }

        rbd.remove("test-image");
    }

    @Test
    public void testReadImage() throws RadosException {
        rbd.create("test-image", 0);
        try (RbdImage image = rbd.open("test-image")) {
            byte[] data = new byte[1024];
            image.resize(1024);
            int n = image.read(data, 0L, data.length);
        }

        rbd.remove("test-image");
    }

    @Test
    public void testWriteReadImage() throws RadosException, NoSuchAlgorithmException {
        rbd.create("test-image", 0);
        try (RbdImage image = rbd.open("test-image")) {

            int n = 1024;
            int bufSize = 1024;

            MessageDigest mdIn = MessageDigest.getInstance("SHA1");
            MessageDigest mdOut = MessageDigest.getInstance("SHA1");

            byte[] dataIn = new byte[bufSize];
            new Random().nextBytes(dataIn);

            image.resize(bufSize * n);
            for (int i = 0; i < n; i++) {
                ByteBuffer b = ByteBuffer.wrap(dataIn);
                mdIn.update(dataIn);
                image.write(b, i * bufSize);
            }
            for (int i = 0; i < n * 2; i++) {
                ByteBuffer b = ByteBuffer.allocateDirect(bufSize / 2);
                b.limit(b.capacity());
                image.read(b, i * bufSize / 2);
                mdOut.update(b);
            }

            assertArrayEquals(mdIn.digest(), mdOut.digest());
        }

        rbd.remove("test-image");
    }

    @Test
    public void testListImages() throws RadosException {
        rbd.create("test-image1", 0);
        rbd.create("test-image2", 0);

        Set<String> list = rbd.list();

        assertEquals(2, list.size());
        assertTrue(list.contains("test-image1"));
        assertTrue(list.contains("test-image2"));

        rbd.remove("test-image1");
        rbd.remove("test-image2");
    }
}
