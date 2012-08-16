DROP TABLE [dbo].[RegistrationInformation]
GO
DROP TABLE [dbo].[RegistrationApplicationName]
GO
CREATE TABLE [dbo].[RegistrationInformation]  ( 
    [id]           	int IDENTITY(1,1) NOT NULL,
    [applicationNameId]	int NOT NULL,
    [firstName]    	varchar(50) NULL,
    [lastName]     	varchar(50) NULL,
    [institution]  	varchar(50) NULL,
    [email]        	varchar(50) NULL,
    [comment]      	text NULL,
    CONSTRAINT [RegistrationInformationId_PK] PRIMARY KEY([id])
)
GO
GRANT SELECT, INSERT, UPDATE, DELETE ON [dbo].[RegistrationInformation] TO [userMimiWeb]
GO
CREATE TABLE [dbo].[RegistrationApplicationName] (
    [id]           	int IDENTITY(1,1) NOT NULL,
    [applicationName] varchar(50) NOT NULL,
    CONSTRAINT [RegistrationApplicationNameId_PK] PRIMARY KEY([id])
)
GO
GRANT SELECT ON [dbo].[RegistrationApplicationName] TO [userMimiWeb]
GO
