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