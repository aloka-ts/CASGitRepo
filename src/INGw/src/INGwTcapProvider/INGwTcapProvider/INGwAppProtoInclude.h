//********************************************************************
//
//     File:   INGwAppProtoInclude.h 
//
//     Desc:  Description of INAP parameter tags   
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 04/03/13       Initial Creation
//********************************************************************

#ifndef _INGW_APP_PROTO_INCLUDE_
#define _INGW_APP_PROTO_INCLUDE_

/*parameter tags of one byte*/

#define FIRST_OCTET_1B_MIN   0x81   /* 129 = 1 0 0 0 0 0 0 1 <included>*/
#define FIRST_OCTET_1B_MAX   0x9E   /* 158 = 1 0 0 1 1 1 1 0 <included>*/


/*parameter tags of two bytes*/
#define FIRST_OCTET_2B       0x9F   /* 158 = 1 0 0 1 1 1 1 1 <included>*/
#define SEC_OCTET_2B_MIN     0x1F   /* 31  = 0 0 0 1 1 1 1 1 <included>*/
#define SEC_OCTET_2B_MAX     0x7F   /* 127 = 0 1 1 1 1 1 1 1 <included>*/

/*parameter tags of three bytes*/
#define SEC_OCTET_3B_MIN     0x81   /* 129 = 1 0 0 0 0 0 0 1 <included>*/  
#define SEC_OCTET_3B_MAX     0xFF   /* 129 = 1 1 1 1 1 1 1 1 <included>*/  
#define THIRD_OCTET_3B_MIN   0x00   /* 000 = 0 0 0 0 0 0 0 0 <included>*/  
#define THIRD_OCTET_3B_MAX   0x3F   /* 063 = 0 0 1 1 1 1 1 1 <included>*/ 

#endif/*_INGW_APP_PROTO_INCLUDE_*/
