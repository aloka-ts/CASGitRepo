package com.baypackets.ase.ra.diameter.common.enums;

import com.traffix.openblox.diameter.enums.FlagRule;


public enum FlagRuleEnum
{
	MAY,
	MUST,
	MUSTNOT,
	SHOULDNOT; 

	public static FlagRuleEnum getContainerObj(FlagRule shEnum){
		FlagRuleEnum containerObj;
		switch(shEnum){
		case May:
			containerObj = FlagRuleEnum.MAY;
			break;
		case Must:
			containerObj = FlagRuleEnum.MUST;
			break;
		case MustNot:
			containerObj = FlagRuleEnum.MUSTNOT;
			break;
		case ShouldNot:
			containerObj = FlagRuleEnum.SHOULDNOT;
			break;
		default :
			// TODO change to ShResourceException
			throw new IllegalArgumentException("value not supported");
		}
		return containerObj;
	}

	public static final FlagRule getStackObj(FlagRuleEnum shEnum){
		FlagRule stackObj;
		switch(shEnum){
		case MAY:
			stackObj = FlagRule.May;
			break;
		case MUST:
			stackObj = FlagRule.Must;
			break;
		case MUSTNOT:
			stackObj = FlagRule.MustNot;
			break;
		case SHOULDNOT:
			stackObj = FlagRule.ShouldNot;
			break;
		default :
			// TODO change to ShResourceException
			throw new IllegalArgumentException("value not supported");
		}
		return stackObj;
	}
	
	//	public static boolean isValid(boolean value){
	//		FlagRule.isValid(value);
	//	}

	//	public static AuthSessionStateEnum 	valueOf(java.lang.String name){
	//		return AuthSessionStateEnum.valueOf(name);
	//	}
	//
	//	static AuthSessionStateEnum[] values(){
	//		return FlagRule.values();
	//	}

}
