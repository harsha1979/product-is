CREATE UNIQUE INDEX SCOPE_INDEX ON IDN_OAUTH2_SCOPE (NAME, TENANT_ID);

CREATE TABLE IF NOT EXISTS IDN_OAUTH2_SCOPE_BINDING (
            SCOPE_ID INTEGER NOT NULL,
            SCOPE_BINDING VARCHAR(255),
            FOREIGN KEY (SCOPE_ID) REFERENCES IDN_OAUTH2_SCOPE(SCOPE_ID) ON DELETE CASCADE
)ENGINE INNODB;


SELECT CONCAT("ALTER TABLE IDN_OAUTH2_RESOURCE_SCOPE DROP FOREIGN KEY ",constraint_name)
INTO @sqlst
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = @databasename AND TABLE_NAME = "SCOPE_ID"
AND REFERENCED_TABLE_NAME="SCOPE_ID" LIMIT 1;

PREPARE stmt FROM @sqlst;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
SET @sqlstr = NULL;

ALTER TABLE IDN_OAUTH2_RESOURCE_SCOPE ADD FOREIGN KEY (SCOPE_ID) REFERENCES IDN_OAUTH2_SCOPE(SCOPE_ID) ON DELETE CASCADE;

ALTER TABLE IDN_OAUTH2_SCOPE MODIFY TENANT_ID INTEGER NOT NULL DEFAULT -1;