-include .env

ifeq ($(OS),Windows_NT)
    SHELL = cmd.exe
    MVN_CMD = mvnw.cmd
else
    MVN_CMD = ./mvnw
endif

dev:
	@echo "Starting server in DEV mode..."
	$(MVN_CMD) spring-boot:run -Dspring-boot.run.profiles=dev

prod:
	@echo "Starting server in PROD mode..."
	$(MVN_CMD) spring-boot:run -Dspring-boot.run.profiles=prod

rollback:
	@echo "Rolling back 1 version database"
	$(MVN_CMD) liquibase:rollback \
	  -Dliquibase.url="$(DB_URL)" \
	  -Dliquibase.username="$(DB_USER)" \
	  -Dliquibase.password="$(DB_PASSWORD)" \
	  -Dliquibase.changeLogFile="src/main/resources/db/changelog/db.changelog-master.yaml" \
	  -Dliquibase.rollbackCount=1

# mvn liquibase:generateChangeLog "-Dliquibase.url=jdbc:postgresql://localhost:5432/spring_app_db" "-Dliquibase.username=postgres" "-Dliquibase.password=yourvibes123" "-Dliquibase.outputChangeLogFile=db/changelog/changes/01-init-schema.sql"
# mvn liquibase:changelogSync "-Dliquibase.url=jdbc:postgresql://localhost:5432/spring_app_db" "-Dliquibase.username=postgres" "-Dliquibase.password=yourvibes123" "-Dliquibase.changeLogFile=db/changelog/db.changelog-master.yaml"
# -- liquibase formatted sql # copy into .sql before run
# -- changeset admin:1 # copy into .sql before run
# SELECT 1; # copy into .sql before run
# docker compose -f docker-compose-broker-kafka.yaml --project-name kafka  up -d
# docker exec kafka1 kafka-topics.sh --describe --topic import-sim-topic --bootstrap-server localhost:9092
# docker exec kafka1 kafka-topics.sh --alter --topic import-sim-topic --partitions 1 --bootstrap-server localhost:9092
