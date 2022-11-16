package com.agnity.win.operations;

/**
 * 
 * This interface contains operation codes of all WIN messages.
 * 
 * @author Rajeev Arya
 * 
 */
public interface WinOpCodes {

	String LR      = "0x0F";       // 15- Location Request 
	String RR      = "0x10";       // 16- Routing Request 
	String FR      = "0x11";       // 17- Feature Request 
	String OR      = "0x2F";       //  47, Origination Request 
	String AIR     = "0x40";       //  64, Analyzed Information Request 
	String CFR     = "0x41";       //  65, Connection Failure Report 
	String CR      = "0x42";       //  66, Connect Resource 
	String DR      = "0x43";       //  67, Disconnect Resource 
	String IR      = "0x45";       //  69, Instruction Request 
	String RT      = "0x47";       //  71, Reset Timer 
	String SR      = "0x49";       //  73, Seize Resource 
	String SRF_DIR = "0x4A";       //  74, SRF Directive 
	String T_BUSY  = "0x4B";       // 75, T Busy 
	String T_NOANS = "0x4C";       //  76, T NoAnswer 
	String REL     = "0x4D";       //  77, Release 
	String CALL_CNTRL_DIR= "0x51"; //81 Call Control Directive
	String O_ANS   = "0x52";       //  82, O Answer 
	String O_DISC  = "0x53";       //  83, O Disconnect 
	String T_ANS   = "0x55";       //  85, T Answer 
	String T_DISC  = "0x56";       //  86, T Disconnect 
	String O_CLD_PTY_BUSY= "0x58"; //  88, O CalledPartyBusy 
	String O_NOANS = "0x59";       //  89, O NoAnswer 
	String SM_ANLYZD = "0x69";       // 105, ShortMessageAnalyzd 
	

	byte LR_BYTE      = 0x0F;       // 15- Location Request 
	byte RR_BYTE      = 0x10;       // 16- Routing Request 
	byte FR_BYTE      = 0x11;       // 17- Feature Request 
	byte OR_BYTE      = 0x2F;       //  47, Origination Request 
	byte AIR_BYTE     = 0x40;       //  64, Analyzed Information Request 
	byte CFR_BYTE     = 0x41;       //  65, Connection Failure Report 
	byte CR_BYTE      = 0x42;       //  66, Connect Resource 
	byte DR_BYTE      = 0x43;       //  67, Disconnect Resource 
	byte IR_BYTE      = 0x45;       //  69, Instruction Request 
	byte RT_BYTE      = 0x47;       //  71, Reset Timer 
	byte SR_BYTE      = 0x49;       //  73, Seize Resource 
	byte SRF_DIR_BYTE = 0x4A;       //  74, SRF Directive 
	byte T_BUSY_BYTE  = 0x4B;       // 75, T Busy 
	byte T_NOANS_BYTE = 0x4C;       //  76, T NoAnswer 
	byte REL_BYTE     = 0x4D;       //  77, Release 
	byte CALL_CNTRL_DIR_BYTE= 0x51; //81 Call Control Directive
	byte O_ANS_BYTE   = 0x52;       //  82, O Answer 
	byte O_DISC_BYTE  = 0x53;       //  83, O Disconnect 
	byte T_ANS_BYTE   = 0x55;       //  85, T Answer 
	byte T_DISC_BYTE  = 0x56;       //  86, T Disconnect 
	byte O_CLD_PTY_BUSY_BYTE= 0x58; //  88, O CalledPartyBusy 
	byte O_NOANS_BYTE = 0x59;       //  89, O NoAnswer 
	byte SM_ANLYZD_BYTE = 0X69 ;    //69,ShortMessageAnalyzd
}
