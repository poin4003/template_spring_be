-- liquibase formatted sql

-- changeset pchuy:20260420-1
-- 1. User Management Tables
CREATE TABLE "user_base" (
    "id" UUID NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "status" INTEGER,
    "login_time" TIMESTAMP WITHOUT TIME ZONE,
    "logout_time" TIMESTAMP WITHOUT TIME ZONE,
    "login_ip" VARCHAR(45),
    "del_flag" VARCHAR(1) DEFAULT '0',
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "user_base_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uk_user_email" UNIQUE ("email")
);

CREATE TABLE "user_info" (
    "id" UUID NOT NULL, -- MapsId từ user_base
    "username" VARCHAR(255),
    "phone_number" VARCHAR(20),
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "user_info_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "fk_user_info_base" FOREIGN KEY ("id") REFERENCES "user_base" ("id") ON DELETE CASCADE
);

-- 2. RBAC Tables
CREATE TABLE "role" (
    "id" UUID NOT NULL,
    "name" VARCHAR(255),
    "key" VARCHAR(100) NOT NULL,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "role_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uk_role_key" UNIQUE ("key")
);

CREATE TABLE "permission" (
    "id" UUID NOT NULL,
    "name" VARCHAR(255),
    "key" VARCHAR(100) NOT NULL,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "permission_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uk_permission_key" UNIQUE ("key")
);

CREATE TABLE "user_roles" (
    "user_id" UUID NOT NULL,
    "role_id" UUID NOT NULL,
    CONSTRAINT "user_roles_pkey" PRIMARY KEY ("user_id", "role_id"),
    CONSTRAINT "fk_ur_user" FOREIGN KEY ("user_id") REFERENCES "user_base" ("id"),
    CONSTRAINT "fk_ur_role" FOREIGN KEY ("role_id") REFERENCES "role" ("id")
);

CREATE TABLE "role_permissions" (
    "role_id" UUID NOT NULL,
    "permission_id" UUID NOT NULL,
    CONSTRAINT "role_permissions_pkey" PRIMARY KEY ("role_id", "permission_id"),
    CONSTRAINT "fk_rp_role" FOREIGN KEY ("role_id") REFERENCES "role" ("id"),
    CONSTRAINT "fk_rp_permission" FOREIGN KEY ("permission_id") REFERENCES "permission" ("id")
);

-- 3. SIM Management
CREATE TABLE "sim" (
    "id" UUID NOT NULL,
    "phone_number" VARCHAR(20) NOT NULL,
    "status" INTEGER NOT NULL,
    "selling_price" INTEGER,
    "dealer_price" INTEGER,
    "import_price" INTEGER,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "sim_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uk_sim_phone" UNIQUE ("phone_number")
);

-- 4. Action & Error Engine (JSONB & target_key)
CREATE TABLE "system_error_definition" (
    "id" UUID NOT NULL,
    "code" INTEGER NOT NULL,
    "alias_key" VARCHAR(255) NOT NULL,
    "http_status" INTEGER NOT NULL,
    "category" INTEGER NOT NULL,
    "exception_class_name" JSONB,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "system_error_definition_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uk_error_code" UNIQUE ("code")
);

CREATE TABLE "system_error_message" (
    "id" UUID NOT NULL,
    "error_defination_id" UUID NOT NULL,
    "language_code" VARCHAR(10) NOT NULL,
    "content" TEXT NOT NULL,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "system_error_message_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "fk_sem_definition" FOREIGN KEY ("error_defination_id") REFERENCES "system_error_definition" ("id")
);

CREATE TABLE "action" (
    "id" UUID NOT NULL,
    "name" VARCHAR(255),
    "target_type" INTEGER,
    "target_key" VARCHAR(255),
    "action_type" INTEGER,
    "config_data" JSONB,
    "priority" INTEGER,
    "status" INTEGER,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "action_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "action_mapping" (
    "action_id" UUID NOT NULL,
    "error_id" UUID NOT NULL,
    CONSTRAINT "action_mapping_pkey" PRIMARY KEY ("action_id", "error_id"),
    CONSTRAINT "fk_am_action" FOREIGN KEY ("action_id") REFERENCES "action" ("id"),
    CONSTRAINT "fk_am_error" FOREIGN KEY ("error_id") REFERENCES "system_error_definition" ("id")
);

-- 5. Infrastructure & Auth
CREATE TABLE "cronjob_config" (
    "id" UUID NOT NULL,
    "name" VARCHAR(255),
    "expression" VARCHAR(100),
    "job_type" VARCHAR(100),
    "lock_at_most_for" VARCHAR(50),
    "lock_at_least_for" VARCHAR(50),
    "status" INTEGER NOT NULL,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "cronjob_config_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "key_store" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "public_key" TEXT,
    "private_key" TEXT,
    "refresh_token" TEXT,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "key_store_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "consumed_refresh_token" (
    "id" UUID NOT NULL,
    "key_store_id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "token_value" TEXT,
    "expiry_date" TIMESTAMP WITH TIME ZONE,
    "used_at" TIMESTAMP WITH TIME ZONE,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "consumed_refresh_token_pkey" PRIMARY KEY ("id")
);

-- changeset pchuy:20260420-2 splitStatements:false
-- Master Data Seed
DO $BODY$
DECLARE
    ADMIN_RID UUID := 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b01';
    ADMIN_UID UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01';
BEGIN
    -- 1. Insert Role
    INSERT INTO role (id, name, key, created_at, updated_at) 
    VALUES (ADMIN_RID, 'Admin system', 'ADMIN_SYSTEM', now(), now())
    ON CONFLICT DO NOTHING;

    -- 2. Insert Permissions
    INSERT INTO permission (id, name, key, created_at, updated_at) VALUES 
        (gen_random_uuid(), 'Manage Action Engine', 'system:action:manage', now(), now()),
        (gen_random_uuid(), 'Manage Sim Inventory', 'system:sim:manage', now(), now())
    ON CONFLICT DO NOTHING;

    -- 3. Insert Admin User (Password: 123456)
    INSERT INTO user_base (id, email, password, status, del_flag, created_at, updated_at) 
    VALUES (ADMIN_UID, 'admin@app.com', '$2b$10$aFCsDS3JNrk3lR/mopRPROvACMVX4rj8v0QTpalcFbuUc9YuYqwFu', 1, '0', now(), now())
    ON CONFLICT DO NOTHING;

    INSERT INTO user_info (id, username, phone_number, created_at, updated_at)
    VALUES (ADMIN_UID, 'System Admin', '0999999999', now(), now())
    ON CONFLICT DO NOTHING;

    -- 4. Map Role to User
    INSERT INTO user_roles (user_id, role_id) VALUES (ADMIN_UID, ADMIN_RID)
    ON CONFLICT DO NOTHING;
END $BODY$;