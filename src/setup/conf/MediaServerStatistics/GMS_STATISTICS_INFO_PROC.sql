create or replace
PROCEDURE GMS_STATISTICS_INFO_PROC 
(
    ANNOUNCEMENT_ID IN VARCHAR2,
    TYPE_ANN IN VARCHAR2,
    ATTEMPTS_ANN IN NUMBER,
    RETRIES_ANN IN NUMBER,
    DURATION_ANN IN FLOAT,
    LAST_UPDATED_ANN IN VARCHAR2
) AS 
  attempts_into  NUMBER (30);
  announcementIDTemp VARCHAR2(1000);
  announcementTypeTemp VARCHAR2(15);
  duration_into  FLOAT (30);
  retries_into NUMBER (30);
  type_into VARCHAR2(15);
  checkTemp NUMBER(10):=0;

BEGIN
  announcementIDTemp:=ANNOUNCEMENT_ID;
  announcementTypeTemp:=TYPE_ANN;
  BEGIN
  SELECT ATTEMPTS,ANNOUNCEMENT_DURATION,RETRIES INTO attempts_into,duration_into,retries_into FROM MEDIA_STATISTICS_INFO WHERE announcement_id=announcementIDTemp and announcement_type=announcementTypeTemp;
  EXCEPTION 
  WHEN NO_DATA_FOUND THEN
  INSERT INTO MEDIA_STATISTICS_INFO VALUES(ANNOUNCEMENT_ID,TYPE_ANN,ATTEMPTS_ANN,RETRIES_ANN,DURATION_ANN,LAST_UPDATED_ANN);
  checkTemp := 1;
  END;
  
  IF checkTemp = 0 then 
  UPDATE MEDIA_STATISTICS_INFO SET ATTEMPTS = attempts_into + ATTEMPTS_ANN,LAST_UPDATED_TIME=LAST_UPDATED_ANN,RETRIES=retries_into+RETRIES_ANN,ANNOUNCEMENT_DURATION=duration_into+DURATION_ANN WHERE announcement_id=announcementIDTemp  and announcement_type=announcementTypeTemp;
  END IF;
COMMIT;
END ;
/
