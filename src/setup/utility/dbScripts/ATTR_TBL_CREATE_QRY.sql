CREATE TABLE CDR_ATTR_TBL (
ATTR_NAME VARCHAR2(200),
ATTR_VALUE VARCHAR2(200) );	
ALTER TABLE CDR_ATTR_TBL DROP CONSTRAINT CDR_ATTR_TBL_UK ;

ALTER TABLE CDR_ATTR_TBL ADD CONSTRAINT CDR_ATTR_TBL_UK UNIQUE (ATTR_NAME);
