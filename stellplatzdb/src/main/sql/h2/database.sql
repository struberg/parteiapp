CREATE TABLE BUILDING (ID INTEGER NOT NULL, CREATED_AT TIMESTAMP WITH TIME ZONE, CREATED_BY VARCHAR(70), MODIFIED_AT TIMESTAMP WITH TIME ZONE, MODIFIED_BY VARCHAR(70), BIC INTEGER NOT NULL, CITY VARCHAR(100) NOT NULL, HOUSENR VARCHAR(30) NOT NULL, STREET VARCHAR(255) NOT NULL, OPTLOCK INTEGER, PRIMARY KEY (ID));
CREATE TABLE SEQUENCES (ID VARCHAR(255) NOT NULL, SEQUENCE_VALUE BIGINT, PRIMARY KEY (ID));
CREATE TABLE SURVEY (ID VARCHAR(30) NOT NULL, CREATED_AT TIMESTAMP WITH TIME ZONE, CREATED_BY VARCHAR(70), MODIFIED_AT TIMESTAMP WITH TIME ZONE, MODIFIED_BY VARCHAR(70), ACTIVE BOOLEAN NOT NULL, DEFAULT_BIC INTEGER, DEFAULT_CITY VARCHAR(100), DESCRIPTION VARCHAR(4000) NOT NULL, OPEN_FROM DATE NOT NULL, OPEN_UNTIL DATE NOT NULL, SURVEY_NAME VARCHAR(255) NOT NULL, OPTLOCK INTEGER, PRIMARY KEY (ID));
CREATE TABLE SURVEY_ENTRY (ID INTEGER NOT NULL, CREATED_AT TIMESTAMP WITH TIME ZONE, CREATED_BY VARCHAR(70), MODIFIED_AT TIMESTAMP WITH TIME ZONE, MODIFIED_BY VARCHAR(70), BIC INTEGER NOT NULL, CITY VARCHAR(100) NOT NULL, COUNTED_AT_DATE DATE NOT NULL, COUNTED_AT_TIME INTEGER NOT NULL, EMAIL VARCHAR(255), HOUSENR VARCHAR(30) NOT NULL, REMOTE_IP VARCHAR(100), STATUS SMALLINT NOT NULL, STATUS_TEXT VARCHAR(4000), STREET VARCHAR(255) NOT NULL, SUM_HOUSING_UNITS INTEGER, SUM_COURTYARD INTEGER, SUM_INSIDE INTEGER, USED_ABROAD_COURTYARD INTEGER, USED_ABROAD_INSIDE INTEGER, USED_LOCAL_COURTYARD INTEGER, USED_LOCAL_INSIDE INTEGER, USER_COMMENT VARCHAR(4000), VERIFIED_AT DATE, VERIFIED_BY DATE, OPTLOCK INTEGER, SURVEY_ID VARCHAR(30) NOT NULL, VERIFIEDBUILDING_ID INTEGER, PRIMARY KEY (ID));
ALTER TABLE SURVEY_ENTRY ADD FOREIGN KEY (SURVEY_ID) REFERENCES SURVEY (ID);
ALTER TABLE SURVEY_ENTRY ADD FOREIGN KEY (VERIFIEDBUILDING_ID) REFERENCES BUILDING (ID);
