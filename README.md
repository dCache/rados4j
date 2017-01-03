RADOS4J
=======
A Java binding for librados and librbd (an alternative to https://github.com/ceph/rados-java)

Testing
-------
The unit tests require a running CEPH instance. A [ceph/demo](https://github.com/ceph/ceph-docker/tree/master/ceph-releases/jewel/ubuntu/14.04/demo)
docker container is a great way to deploy a test instance. There are three environment variables to point test to your
installation: **RADOS_TEST_CLUSTER**, **RADOS_TEST_CONF**, **RADOS_TEST_POOL**, with
**admin**, **/etc/ceph/ceph.conf** and **test-pool** as default values.

License:
--------

licensed under [LGPLv3](http://www.gnu.org/licenses/lgpl-3.0.txt "LGPLv3") (or later)


