#!/usr/bin/env sh

if [ ! -f /opt/sec/private-key.pem ]; then
    echo "Creating new EC P-384 public/private key pair for JWT"
    openssl genpkey -out /opt/sec/private-key.pem -algorithm ec -pkeyopt ec_paramgen_curve:secp384r1
    openssl pkey -in /opt/sec/private-key.pem -pubout -out /opt/sec/public-key.pem
fi

exec /opt/jboss/container/java/run/run-java.sh "$@"
