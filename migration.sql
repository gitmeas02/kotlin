-- Drop and Recreate Database: Fresh start with UUID-based schema
-- Database: SQL Server

PRINT 'Starting database recreation...';

-- Switch to master to drop the database
USE master;
GO

-- Close any existing connections to KotlinDB
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'KotlinDB')
BEGIN
    ALTER DATABASE KotlinDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE KotlinDB;
    PRINT 'Dropped KotlinDB database';
END;
GO

-- Create fresh database
CREATE DATABASE KotlinDB;
PRINT 'Created KotlinDB database';
GO

-- Switch to the new database
USE KotlinDB;
GO

-- Create roles table
CREATE TABLE roles (
    name NVARCHAR(50) NOT NULL PRIMARY KEY,
    description NVARCHAR(200) NULL
);
PRINT 'Created roles table';

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
    ('user', 'Default user role'),
    ('admin', 'Administrator role'),
    ('editor', 'Editor role');
PRINT 'Inserted default roles';

-- Create users table with UUID as primary key
CREATE TABLE users (
    uuid NVARCHAR(36) NOT NULL PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    avatar NVARCHAR(MAX) NULL,
    token_version INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);
PRINT 'Created users table with UUID primary key';

-- Create user_roles join table (many-to-many)
CREATE TABLE user_roles (
    user_uuid NVARCHAR(36) NOT NULL,
    role_name NVARCHAR(50) NOT NULL,
    PRIMARY KEY (user_uuid, role_name),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT FK_user_roles_role FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE
);
PRINT 'Created user_roles join table';

-- Create indexes for better performance
CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_user_roles_user ON user_roles(user_uuid);
CREATE INDEX IX_user_roles_role ON user_roles(role_name);
PRINT 'Created indexes';

PRINT 'Database recreation completed successfully!';
