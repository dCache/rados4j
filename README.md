RADOS4J
=======
A Java binding for librados and librbd (an alternative to https://github.com/ceph/rados-java)


Testing
-------
The unit tests require running CEPH instance. You can use [all-in-one](https://gist.github.com/kofemann/796c1f07f87adc1eb8e3)
instance for that. There are three environment variables to point test to your
installation: **RADOS_TEST_CLUSTER**, **RADOS_TEST_CONF**, **RADOS_TEST_POOL**, with
**admin**, **/etc/ceph/ceph.conf** and **test-pool** as default values.
License:
--------

licensed under [LGPLv3](http://www.gnu.org/licenses/lgpl-3.0.txt "LGPLv3") (or later)


