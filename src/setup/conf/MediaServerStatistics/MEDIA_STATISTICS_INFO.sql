  CREATE TABLE "MEDIA_STATISTICS_INFO" 
   (	"ANNOUNCEMENT_ID" VARCHAR2(1000 BYTE) NOT NULL ENABLE, 
	"ANNOUNCEMENT_TYPE" VARCHAR2(30 BYTE) NOT NULL ENABLE, 
	"ATTEMPTS" NUMBER(30,0), 
	"RETRIES" NUMBER(30,0), 
	"ANNOUNCEMENT_DURATION" FLOAT(126), 
	"LAST_UPDATED_TIME" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	 CONSTRAINT "MEDIA_STATISTICS_PK" PRIMARY KEY ("ANNOUNCEMENT_ID", "ANNOUNCEMENT_TYPE")
    );
 


