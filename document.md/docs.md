 docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Smeypassword123!" -C -Q "CREATE DATABASE KotlinDB;"

 docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Smeypassword123!" -C -Q "CREATE LOGIN smeyusername WITH PASSWORD = 'Smeypassword123!';"

 docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Smeypassword123!" -C -d KotlinDB -Q "CREATE USER smeyusername FOR LOGIN smeyusername; ALTER ROLE db_owner ADD MEMBER smeyusername;"

 .\gradlew :app:bootRun