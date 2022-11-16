package jain;

public class CriticalityTypeException extends Exception {

        /**
         * 
         */
        private static final long       serialVersionUID        = -5660714504363006358L;
        public static enum CRITICALITY {
                IGNORE,ABORT
        }
        CRITICALITY criticality;
        /**
         * @param criticality
         */
        public CriticalityTypeException(CRITICALITY criticality) {
                super();
                this.criticality = criticality;
        }
        /**
         * @return the criticality
         */
        public CRITICALITY getCriticality() {
                return criticality;
        }       
        /**
         * 
         */
        public CriticalityTypeException() {
                super();
                this.criticality = CRITICALITY.ABORT;
        }
        

}