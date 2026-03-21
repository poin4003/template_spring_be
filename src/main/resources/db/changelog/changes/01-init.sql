-- liquibase formatted sql

-- changeset pchuy:1774022793391-1
CREATE TABLE "consumed_refresh_token" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "expiry_date" TIMESTAMP WITH TIME ZONE, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "used_at" TIMESTAMP WITH TIME ZONE, "id" UUID NOT NULL, "key_store_id" UUID NOT NULL, "user_id" UUID NOT NULL, "token_value" VARCHAR(255), CONSTRAINT "consumed_refresh_token_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-2
CREATE TABLE "key_store" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "user_id" UUID NOT NULL, "private_key" VARCHAR(255), "public_key" VARCHAR(255), "refresh_token" VARCHAR(255), CONSTRAINT "key_store_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-3
CREATE TABLE "action" ("action_type" SMALLINT, "priority" INTEGER, "status" SMALLINT, "target_type" SMALLINT, "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "target_id" UUID, "name" VARCHAR(255), "config_data" JSONB, CONSTRAINT "action_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-4
CREATE TABLE "ops_config" ("status" SMALLINT, "type" SMALLINT, "config_version" BIGINT, "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "name" VARCHAR(255), CONSTRAINT "ops_config_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-5
CREATE TABLE "sim" ("dealer_price" INTEGER, "import_price" INTEGER, "selling_price" INTEGER, "status" SMALLINT NOT NULL, "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "phone_number" VARCHAR(255) NOT NULL, CONSTRAINT "sim_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-6
CREATE TABLE "system_error_definition" ("category" SMALLINT NOT NULL, "code" INTEGER NOT NULL, "http_status" INTEGER NOT NULL, "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "alias_key" VARCHAR(255) NOT NULL, "exception_class_name" JSONB, CONSTRAINT "system_error_definition_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-7
CREATE TABLE "user_base" ("status" SMALLINT, "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "login_time" TIMESTAMP WITHOUT TIME ZONE, "logout_time" TIMESTAMP WITHOUT TIME ZONE, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "del_flag" VARCHAR(255), "email" VARCHAR(255) NOT NULL, "login_ip" VARCHAR(255), "password" VARCHAR(255) NOT NULL, CONSTRAINT "user_base_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-8
CREATE INDEX "idx_consumed_token_value" ON "consumed_refresh_token"("token_value");

-- changeset pchuy:1774022793391-9
CREATE INDEX "idx_comsumed_user" ON "consumed_refresh_token"("user_id");

-- changeset pchuy:1774022793391-10
CREATE INDEX "idx_keystore_token" ON "key_store"("refresh_token");

-- changeset pchuy:1774022793391-11
ALTER TABLE "key_store" ADD CONSTRAINT "idx_keystore_user" UNIQUE ("user_id");

-- changeset pchuy:1774022793391-12
ALTER TABLE "sim" ADD CONSTRAINT "sim_phone_number_key" UNIQUE ("phone_number");

-- changeset pchuy:1774022793391-13
ALTER TABLE "system_error_definition" ADD CONSTRAINT "system_error_definition_code_key" UNIQUE ("code");

-- changeset pchuy:1774022793391-14
ALTER TABLE "user_base" ADD CONSTRAINT "user_base_email_key" UNIQUE ("email");

-- changeset pchuy:1774022793391-15
CREATE TABLE "action_mapping" ("action_id" UUID NOT NULL, "error_id" UUID NOT NULL);

-- changeset pchuy:1774022793391-16
CREATE TABLE "cronjob_config" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "expression" VARCHAR(255), "job_type" VARCHAR(255), "lock_at_least_for" VARCHAR(255), "lock_at_most_for" VARCHAR(255), "name" VARCHAR(255), CONSTRAINT "cronjob_config_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-17
CREATE TABLE "mq_consumer_config" ("enable_dlq" BOOLEAN, "parallelism" INTEGER, "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "consumer_group" VARCHAR(255), "dlq_name" VARCHAR(255), "handler_key" VARCHAR(255), "source_name" VARCHAR(255), "config_data" JSONB, CONSTRAINT "mq_consumer_config_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-18
CREATE TABLE "permission" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "key" VARCHAR(255), "name" VARCHAR(255), CONSTRAINT "permission_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-19
CREATE TABLE "role" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "key" VARCHAR(255), "name" VARCHAR(255), CONSTRAINT "role_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-20
CREATE TABLE "role_permissions" ("permission_id" UUID NOT NULL, "role_id" UUID NOT NULL);

-- changeset pchuy:1774022793391-21
CREATE TABLE "system_error_message" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "error_defination_id" UUID NOT NULL, "id" UUID NOT NULL, "content" VARCHAR(255) NOT NULL, "language_code" VARCHAR(255) NOT NULL, CONSTRAINT "system_error_message_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-22
CREATE TABLE "user_info" ("created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL, "id" UUID NOT NULL, "phone_number" VARCHAR(255), "username" VARCHAR(255), CONSTRAINT "user_info_pkey" PRIMARY KEY ("id"));

-- changeset pchuy:1774022793391-23
CREATE TABLE "user_roles" ("role_id" UUID NOT NULL, "user_id" UUID NOT NULL);

-- changeset pchuy:1774022793391-24
ALTER TABLE "action_mapping" ADD CONSTRAINT "fk8lc06ff7c4mprgdevem9kik0v" FOREIGN KEY ("action_id") REFERENCES "action" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-25
ALTER TABLE "system_error_message" ADD CONSTRAINT "fkbmp71t4w7gbr2bntsifmt33y1" FOREIGN KEY ("error_defination_id") REFERENCES "system_error_definition" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-26
ALTER TABLE "user_roles" ADD CONSTRAINT "fkdxg65a1i8oqs3sbvmeqwqjny0" FOREIGN KEY ("user_id") REFERENCES "user_base" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-27
ALTER TABLE "role_permissions" ADD CONSTRAINT "fkh0v7u4w7mttcu81o8wegayr8e" FOREIGN KEY ("permission_id") REFERENCES "permission" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-28
ALTER TABLE "mq_consumer_config" ADD CONSTRAINT "fkifkjok5ibestctew1r0t7lpna" FOREIGN KEY ("id") REFERENCES "ops_config" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-29
ALTER TABLE "user_info" ADD CONSTRAINT "fkl5j35rlx7nypv8u605pud0mxa" FOREIGN KEY ("id") REFERENCES "user_base" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-30
ALTER TABLE "role_permissions" ADD CONSTRAINT "fklodb7xh4a2xjv39gc3lsop95n" FOREIGN KEY ("role_id") REFERENCES "role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-31
ALTER TABLE "action_mapping" ADD CONSTRAINT "fkpmoqcmr5fn3fhi70ygts8v7mr" FOREIGN KEY ("error_id") REFERENCES "system_error_definition" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-32
ALTER TABLE "user_roles" ADD CONSTRAINT "fkrhfovtciq1l558cw6udg0h0d3" FOREIGN KEY ("role_id") REFERENCES "role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:1774022793391-33
ALTER TABLE "cronjob_config" ADD CONSTRAINT "fks99ymx7joxmlu0filt4yb8ud" FOREIGN KEY ("id") REFERENCES "ops_config" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset pchuy:2 splitStatements:false
DO $BODY$
DECLARE
    ADMIN_SYS_RID UUID := 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b01';
    ADMIN_SYS_UID UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01';
BEGIN
    DELETE FROM user_roles;
    DELETE FROM role_permissions;
    DELETE FROM user_info;
    DELETE FROM user_base;
    DELETE FROM role;
    DELETE FROM permission;

    -- 2. Insert Role
    INSERT INTO role (id, name, key, created_at, updated_at) VALUES 
        (ADMIN_SYS_RID, 'Admin system', 'ADMIN_SYSTEM', now(), now());

    -- 3. Insert Permissions
    INSERT INTO permission (id, name, key, created_at, updated_at) VALUES 
        (gen_random_uuid(), 'Manage config', 'system:config:manage', now(), now()),
        (gen_random_uuid(), 'Manage user', 'system:admin:manage', now(), now());

    -- 4. Insert User (Password: 123456)
    INSERT INTO user_base (id, email, password, status, del_flag, created_at, updated_at) VALUES 
        (ADMIN_SYS_UID, 'system@admin.com', '$2b$10$aFCsDS3JNrk3lR/mopRPROvACMVX4rj8v0QTpalcFbuUc9YuYqwFu', 1, '0', now(), now());

    -- 5. Insert User Info
    INSERT INTO user_info (id, username, created_at, updated_at) VALUES
        (ADMIN_SYS_UID, 'System Admin', now(), now());

    -- 6. Mapping User <-> Role
    INSERT INTO user_roles (user_id, role_id) VALUES 
        (ADMIN_SYS_UID, ADMIN_SYS_RID);
    
    -- 7. Mapping Role <-> Permission
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT ADMIN_SYS_RID, id FROM permission WHERE key LIKE 'system:%';
    
END $BODY$;
