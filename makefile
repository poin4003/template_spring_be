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
flyway_migrate:
	@echo "Running Flyway migration..."
	$(MVN_CMD) flyway:migrate "-Dflyway.url=$(DB_URL)" "-Dflyway.user=$(DB_USER)" "-Dflyway.password=$(DB_PASSWORD)"

flyway_info:
	@echo "Checking migration status..."
	$(MVN_CMD) flyway:info "-Dflyway.url=$(DB_URL)" "-Dflyway.user=$(DB_USER)" "-Dflyway.password=$(DB_PASSWORD)"

flyway_validate:
	@echo "Validating migrations..."
	$(MVN_CMD) flyway:validate "-Dflyway.url=$(DB_URL)" "-Dflyway.user=$(DB_USER)" "-Dflyway.password=$(DB_PASSWORD)"

flyway_repair:
	@echo "Repairing schema history..."
	$(MVN_CMD) flyway:repair "-Dflyway.url=$(DB_URL)" "-Dflyway.user=$(DB_USER)" "-Dflyway.password=$(DB_PASSWORD)"

# docker compose -f docker-compose-broker-kafka.yaml --project-name kafka  up -d
# docker exec kafka1 kafka-topics.sh --describe --topic import-sim-topic --bootstrap-server localhost:9092
# docker exec kafka1 kafka-topics.sh --alter --topic import-sim-topic --partitions 1 --bootstrap-server localhost:9092