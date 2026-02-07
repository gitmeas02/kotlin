CREATE DATABASE KotlinDB;
GO
CREATE LOGIN smeyusername WITH PASSWORD = 'Smeypassword123!';
GO
USE KotlinDB;
GO
CREATE USER smeyusername FOR LOGIN smeyusername;
GO
ALTER ROLE db_owner ADD MEMBER smeyusername;
GO

-- Create users table with all required columns
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);
GO