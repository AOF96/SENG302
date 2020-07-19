fuser -k 9499/tcp || true

SENG302_DB_URL=jdbc:mariadb://db2.csse.canterbury.ac.nz/seng302-2020-team300-test \
	SENG302_DB_USERNAME=seng302-team300 \
	SENG302_DB_PASSWORD=CreatingRibbon7031 \
	java -jar test-server/libs/server-0.0.1-SNAPSHOT.jar --server.port=9499
