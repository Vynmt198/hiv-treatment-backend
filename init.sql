IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'hiv_medical')
BEGIN
    CREATE DATABASE hiv_medical;
END;
GO