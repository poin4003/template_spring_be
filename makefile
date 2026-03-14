-include .env

ifeq ($(OS),Windows_NT)
	SHELL = cmd.exe
	MVN_CMD = mvnw.cmd
else
	MVN_CMD = ./mvnw
endif

# Start server
run:
	@echo "Starting server..."
	$(MVN_CMD) spring-boot:run

# Migration command
db_generate:
	@echo "Generating schema from current DB..."
	$(MVN_CMD) liquibase:generateChangeLog -Dliquibase.url="$(DB_URL)" -Dliquibase.username="$(DB_USER)" -Dliquibase.password="$(DB_PASSWORD)" -Dliquibase.outputChangeLogFile="src/main/resources/db/changelog/01-init-schema.sql"

# docker compose -f docker-compose-broker-kafka.yaml --project-name kafka  up -d
# docker exec kafka1 kafka-topics.sh --describe --topic import-sim-topic --bootstrap-server localhost:9092
# docker exec kafka1 kafka-topics.sh --alter --topic import-sim-topic --partitions 1 --bootstrap-server localhost:9092