create or replace
TRIGGER INSERT_SENDFILENAME
  AFTER INSERT ON CDR_DATA_TBL
DECLARE
    ORIGFILENAME VARCHAR2(50);
    SEND_FILENAME VARCHAR2(50);
    FILENAME_WITHOUT_EXT VARCHAR2(50);
    FILENAME_EXTENSION VARCHAR2(50);
    
    CURSOR GETFILENAME IS SELECT DISTINCT FILENAME FROM CDR_DATA_TBL WHERE SENT_FILENAME IS NULL;
BEGIN
  
    FOR TMPFILENAME IN GETFILENAME
  LOOP
      ORIGFILENAME := TMPFILENAME.FILENAME;
      IF ORIGFILENAME LIKE '%.%' THEN
        FILENAME_WITHOUT_EXT := SUBSTR(ORIGFILENAME,0,INSTR(ORIGFILENAME, '.', -1, 1));
        FILENAME_EXTENSION := SUBSTR(ORIGFILENAME,INSTR(ORIGFILENAME, '.', -1, 1));
      ELSE
        FILENAME_WITHOUT_EXT := ORIGFILENAME;
        FILENAME_EXTENSION := NULL;
      END IF;
   
      IF INSTR(FILENAME_WITHOUT_EXT, 'P', -1, 1) > INSTR(FILENAME_WITHOUT_EXT, 'S', -1, 1) THEN
          SEND_FILENAME := SUBSTR(FILENAME_WITHOUT_EXT,0,INSTR(FILENAME_WITHOUT_EXT, 'P', -1, 1)) || 
                        DSI_FILE_SEQ.NEXTVAL || FILENAME_EXTENSION;
      ELSE
          SEND_FILENAME := SUBSTR(FILENAME_WITHOUT_EXT,0,INSTR(FILENAME_WITHOUT_EXT, 'S', -1, 1)) || 
                        DSI_FILE_SEQ.NEXTVAL || FILENAME_EXTENSION;
      END IF;
      UPDATE CDR_DATA_TBL SET SENT_FILENAME=SEND_FILENAME WHERE FILENAME=ORIGFILENAME;
  END LOOP;
END;
/
