----------------------------------------------------
-- 1. EXTENSION & CLEANUP
----------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS mq_consumer_details CASCADE;
DROP TABLE IF EXISTS service_endpoint_configs CASCADE;
DROP TABLE IF EXISTS sims CASCADE;
DROP TABLE IF EXISTS consumed_refresh_tokens CASCADE;
DROP TABLE IF EXISTS key_store CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS user_info CASCADE;
DROP TABLE IF EXISTS user_base CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;

----------------------------------------------------
-- 2. RBAC TABLES (Inherit BaseEntity)
----------------------------------------------------

-- Permissions
CREATE TABLE permissions (
    permission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    permission_name VARCHAR(100) NOT NULL,
    permission_key VARCHAR(100) UNIQUE NOT NULL,
    
    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Roles
CREATE TABLE roles (
    role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_name VARCHAR(30) NOT NULL,
    role_key VARCHAR(100) UNIQUE NOT NULL,
    
    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

----------------------------------------------------
-- 3. USER TABLES (Inherit BaseEntity)
----------------------------------------------------

-- User Base
CREATE TABLE user_base (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_email VARCHAR(30) UNIQUE NOT NULL,
    user_password VARCHAR(255) DEFAULT NULL, 
    
    user_status INT NOT NULL DEFAULT 1, 
    del_flag CHAR(1) DEFAULT '0', 
    
    user_login_time TIMESTAMP WITHOUT TIME ZONE,
    user_logout_time TIMESTAMP WITHOUT TIME ZONE,
    user_login_ip VARCHAR(45),
    
    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- User Info
CREATE TABLE user_info (
    user_id UUID PRIMARY KEY, 
    username VARCHAR(100) NOT NULL,
    user_phone_number VARCHAR(20),
    user_birthday TIMESTAMP WITHOUT TIME ZONE,
    
    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user_base (user_id) ON DELETE CASCADE
);

----------------------------------------------------
-- 4. MAPPING TABLES (No BaseEntity - Pure Link)
----------------------------------------------------

-- User <-> Role
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_base (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);

-- Role <-> Permission
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (permission_id) ON DELETE CASCADE
);

----------------------------------------------------
-- 5. AUTH SECURITY TABLES (Inherit BaseEntity)
----------------------------------------------------

-- KeyStore
CREATE TABLE key_store (
    key_store_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL,
    public_key TEXT NOT NULL,
    private_key TEXT NOT NULL,
    refresh_token TEXT UNIQUE,

    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user_base (user_id) ON DELETE CASCADE
);

-- Consumed Refresh Tokens
CREATE TABLE consumed_refresh_tokens (
    token_history_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key_store_id UUID,
    user_id UUID NOT NULL,
    token_value TEXT UNIQUE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- BaseEntity columns (Inherited from class definition)
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (key_store_id) REFERENCES key_store (key_store_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES user_base (user_id) ON DELETE CASCADE
);

----------------------------------------------------
-- 6. BUSINESS TABLES (Inherit BaseEntity)
----------------------------------------------------

-- Sims
CREATE TABLE sims (
    sim_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
    sim_phone_number VARCHAR(20) NOT NULL UNIQUE,
    sim_status INT NOT NULL,
    sim_selling_price INT,
    sim_dealer_price INT,
    sim_import_price INT,
    
    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

----------------------------------------------------
-- 2. CREATE TABLE: service_endpoint_configs
----------------------------------------------------
CREATE TABLE service_endpoint_configs (
    service_endpoint_config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    endpoint_type INT NOT NULL,
    endpoint_status INT NOT NULL,

    -- BaseEntity columns
    note TEXT,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

----------------------------------------------------
-- 3. CREATE TABLE: mq_consumer_details
----------------------------------------------------
CREATE TABLE mq_consumer_details (
    service_endpoint_config_id UUID PRIMARY KEY,

    source_name VARCHAR(255) NOT NULL,
    consumer_group VARCHAR(255) NOT NULL,

    parallelism INT DEFAULT 1,

    handler_key VARCHAR(255) NOT NULL,

    ack_strategy INT DEFAULT 0,
    retry_enabled BOOLEAN DEFAULT TRUE,

    transport_config JSONB,

    note VARCHAR(255),
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mq_consumer_endpoint
        FOREIGN KEY (service_endpoint_config_id)
        REFERENCES service_endpoint_configs(service_endpoint_config_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

----------------------------------------------------
-- 7. SEED DATA
----------------------------------------------------
DO $$
DECLARE
    ADMIN_SYS_UID UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01';
    SELLER_UID UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02';
    
    ADMIN_SYS_RID UUID := 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b01';
    SELLER_RID UUID := 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b02';
    BUYER_RID UUID := 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b03';
BEGIN
    -- 1. ROLES
    INSERT INTO roles (role_id, role_name, role_key, description) VALUES 
        (ADMIN_SYS_RID, 'Quản trị viên Hệ thống', 'ADMIN_SYSTEM', 'Quyền cao nhất trong hệ thống'),
        (SELLER_RID, 'Người bán', 'SELLER', 'Đối tác bán hàng'),
        (BUYER_RID, 'Người mua', 'BUYER', 'Khách hàng mua sim');

    -- 2. PERMISSIONS
    INSERT INTO permissions (permission_name, permission_key, description) VALUES 
        ('Quản lý cấu hình dịch vụ', 'system:config:manage', 'Cho phép thay đổi cấu hình Kafka, Endpoint'),
        ('Quản lý tài khoản Admin', 'system:admin:manage', 'Tạo và khóa tài khoản admin'),
        ('Xem báo cáo hệ thống', 'system:report:view', 'Xem dashboard thống kê'),
        
        ('Quản lý sản phẩm', 'seller:product:manage', 'Thêm sửa xóa sim'),
        ('Xem đơn hàng của Shop', 'seller:order:view', 'Xem danh sách đơn hàng'),
        
        ('Xem sản phẩm', 'buyer:product:view', 'Xem danh sách sim'),
        ('Thực hiện thanh toán', 'buyer:order:checkout', 'Đặt hàng');

    -- 3. USERS
    -- user_status = 1 (ACTIVE)
    INSERT INTO user_base (user_id, user_email, user_password, user_status, del_flag, description) VALUES 
        (ADMIN_SYS_UID, 'system@admin.com', '$2b$10$aFCsDS3JNrk3lR/mopRPROvACMVX4rj8v0QTpalcFbuUc9YuYqwFu', 1, '0', 'Tài khoản Super Admin'),
        (SELLER_UID, 'shop@partner.com', '$2b$10$aFCsDS3JNrk3lR/mopRPROvACMVX4rj8v0QTpalcFbuUc9YuYqwFu', 1, '0', 'Tài khoản Shop mẫu');

    -- 4. USER INFO
    INSERT INTO user_info (user_id, username, description) VALUES
        (ADMIN_SYS_UID, 'System Admin', 'Profile Admin'),
        (SELLER_UID, 'Partner Shop', 'Profile Shop');

    -- 5. USER ROLES
    INSERT INTO user_roles (user_id, role_id) VALUES 
        (ADMIN_SYS_UID, ADMIN_SYS_RID),
        (SELLER_UID, SELLER_RID);
    
    -- 6. ROLE PERMISSIONS
    -- ADMIN_SYSTEM
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT ADMIN_SYS_RID, permission_id FROM permissions WHERE permission_key LIKE 'system:%';
    
    -- SELLER
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT SELLER_RID, permission_id FROM permissions WHERE permission_key LIKE 'seller:%';
    
    -- BUYER
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT BUYER_RID, permission_id FROM permissions WHERE permission_key LIKE 'buyer:%';

    -- 7. OPS
    INSERT INTO service_endpoint_configs
    (
        service_endpoint_config_id,
        endpoint_type,
        endpoint_status,
        note,
        description
    )
    VALUES
    (
        'c9c6e9af-feb1-4464-a4e6-015c1fbd8d70',
        1,
        1,
        'SIM import listener',
        'Kafka consumer import SIM'
    );

    INSERT INTO mq_consumer_details
    (
        service_endpoint_config_id,
        source_name,
        consumer_group,
        parallelism,
        handler_key,
        ack_strategy,
        retry_enabled,
        transport_config,
        note,
        description
    )
    VALUES
    (
        'c9c6e9af-feb1-4464-a4e6-015c1fbd8d70',
        'import-sim-topic',
        'sim-import-group-partner-a',
        3,
        'simImportConsumer',
        0,
        true,
        '{"autoOffsetReset":"earliest"}',
        'Main SIM import',
        'Consumer handle import single sim'
    );

END;
$$ LANGUAGE plpgsql;