package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import java.util.TreeMap;
import java.lang.Class;
import java.lang.reflect.Method;
import java.io.IOException;
import java.math.BigInteger;

import com.genband.ase.alc.alcml.jaxb.xjc.Conditiontype;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceActionCondition implements ServiceAction, ServiceBlockListener {
	static Logger logger = Logger.getLogger(ServiceActionCondition.class
			.getName());

	public boolean isAnAtomicAction() {
		return true;
	}

	public String EndOfBlockDisplay() {
		return "</condition>  <!--" + Display() + "-->";
	}

	public ServiceAction notifyEndOfBlock(ServiceContext context,
			ServiceActionBlock sab) {
		if (sab == myThenClause && myElseCondition != null) {
			sab.removeServiceActionDefaultContextProvider(context);
			return myElseCondition.getNextActionAfterBlock();
		}
		return null;
	}

	public String Display() {
		String display = new String("<condition ");
		display += "if=\"" + myAction.getIf() + "\"";
		if (myAction.getEqualTo() != null)
			display += " equal-to=\"" + myAction.getEqualTo() + "\"";
		if (myAction.getNotEqualTo() != null)
			display += " not-equal-to=\"" + myAction.getNotEqualTo() + "\"";
		if (myAction.getGreaterThan() != null)
			display += " greater-than=\"" + myAction.getGreaterThan() + "\"";
		if (myAction.getGreaterThanEqualTo() != null)
			display += " greater-than-equal-to=\"" + myAction.getGreaterThanEqualTo() + "\"";
		if (myAction.getLessThan() != null)
			display += " less-than=\"" + myAction.getLessThan() + "\"";
		if (myAction.getLessThanEqualTo() != null)
			display += " less-than-equal-to=\"" + myAction.getLessThanEqualTo() + "\"";
		if (myAction.getStringContains() != null)
			display += " string-contains=\"" + myAction.getStringContains() + "\"";
		if (myAction.getStringBeginsWith()!= null)
			display += " string-begins-with=\"" + myAction.getStringBeginsWith() + "\"";
		if (myAction.getStringEndsWith() != null)
			display += " string-ends-with=\"" + myAction.getStringEndsWith() + "\"";		
		display += " ... ";

		return display;
	}

	public String getServiceName() {
		return "ServiceActionCondition";
	}

	public String getServiceMethod() {
		return "ServiceActionCondition";
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType,
			List<ServiceAction> subordinateActionList)
			throws ServiceActionCreationException {
		subordinateActionList.add(this);
		this.myDef = defXRef;
		this.myAction = (Conditiontype) XMLActionType;
		if (myAction.getElse() == null) {
			if (myAction.getThen() == null) {
				myThenClause = ServiceActionBlock.CreateBlock("condition if",
						defXRef, myAction.getConditionOrLoopOrRegex(),
						subordinateActionList, null);
				myThenClause.setServiceBlockListener(this);
			} else {
				myThenClause = ServiceActionBlock.CreateBlock("then", defXRef,
						myAction.getThen().getConditionOrLoopOrRegex(),
						subordinateActionList, null);
				myThenClause.setServiceBlockListener(this);
			}
		} else {
			if (myAction.getThen() == null) {
				throw new ServiceActionCreationException(
						"must use <then>..</then> block if <else> Block is present.");
			} else {
				myThenClause = ServiceActionBlock.CreateBlock("then", defXRef,
						myAction.getThen().getConditionOrLoopOrRegex(),
						subordinateActionList, null);
				myThenClause.setServiceBlockListener(this);
				if (myAction.getElse().getConditionOrLoopOrRegex().size() > 0) {
					myElseCondition = ServiceActionBlock.CreateBlock("else",
							defXRef, myAction.getElse()
									.getConditionOrLoopOrRegex(),
							subordinateActionList, null);
					myElseCondition.setServiceBlockListener(this);
				}
			}
		}
	}

	public void ex(ServiceContext context)
			throws ServiceActionExecutionException {
		context.setCurrentAction(this);
		context.ActionCompleted(OKAY);
	}

	public String getLabel() {
		return myAction.getLabel();
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults) {
		String ifClause = ALCMLExpression.toString(sThisContext, myAction
				.getIf());
		boolean isInt = false;
		boolean isFloat = false;
		Number ifClauseNum = null;
		try {
		if(ifClause.indexOf('.') != -1){
			ifClauseNum = Float.parseFloat(ifClause);
			isFloat = true;
		} else {
			ifClauseNum = Integer.parseInt(ifClause);
			isInt = true;
		}}
		catch(Exception ex){
			isInt = false;
			isFloat = false;
		}
		
		
		String compareStr = "";
		if(myAction.getEqualTo() != null){
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getEqualTo());
		} else if(myAction.getNotEqualTo() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getNotEqualTo());
		}else if(myAction.getGreaterThan() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getGreaterThan());
		}else if(myAction.getGreaterThanEqualTo() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getGreaterThanEqualTo());
		}else if(myAction.getLessThan() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getLessThan());
		}else if(myAction.getLessThanEqualTo() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getLessThanEqualTo());
			//hpahuja  |SCE|add string comparisons to decision |start
		}else if(myAction.getStringContains() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getStringContains());
		}else if(myAction.getStringBeginsWith()!= null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getStringBeginsWith());
		}else if(myAction.getStringEndsWith() != null) {
			compareStr = ALCMLExpression.toString(sThisContext, myAction
					.getStringEndsWith());
		}
		//hpahuja  |SCE|add string comparisons to decision |ends
		
		Number compareStrNum = null;
		try {
			if (isInt) {
				try {
					compareStrNum = Integer.parseInt(compareStr);
				} catch (Exception ex) {
					ifClauseNum = Float.parseFloat(ifClause);
					compareStrNum = Float.parseFloat(compareStr);
					isInt = false;
					isFloat = true;
				}
			}
			else if (isFloat) {
				compareStrNum = Float.parseFloat(compareStr);
			}
		} catch(Exception ex){
			isInt = false;
			isFloat = false;
		}
		 
		
		if (myAction.getEqualTo() != null) {
						
			if (!isInt && !isFloat) {
				if (ifClause.equals(compareStr)) {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is equal to " + compareStr + ", executing block.");
					return myThenClause;
				} else {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is not equal to " + compareStr
							+ ", skipping block.");
					if (myElseCondition != null)
						return myElseCondition;
					else
						return myThenClause.getEndOfBlock();
				}
			} else {

				if (ifClauseNum.equals(compareStrNum)) {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is equal to " + compareStrNum + ", executing block.");
					return myThenClause;
				} else {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is not equal to " + compareStrNum
							+ ", skipping block.");
					if (myElseCondition != null)
						return myElseCondition;
					else
						return myThenClause.getEndOfBlock();
				}

			} 
		}
		if (myAction.getNotEqualTo() != null) {
			if (!isInt && !isFloat) {
				if (ifClause.equals(compareStr)) {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is not equal to " + compareStr
							+ ", skipping block.");
					if (myElseCondition != null)
						return myElseCondition;
					else
						return myThenClause.getEndOfBlock();
				} else {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is not equal to " + compareStr
							+ ", executing block.");
					return myThenClause;
				}
			} else {
				if (ifClauseNum.equals(compareStrNum)) {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is not equal to " + compareStrNum
							+ ", skipping block.");
					if (myElseCondition != null)
						return myElseCondition;
					else
						return myThenClause.getEndOfBlock();
				} else {
					sThisContext.log(logger, Level.DEBUG, ifClause
							+ " is not equal to " + compareStrNum
							+ ", executing block.");
					return myThenClause;
				}
			
			}
		} if(myAction.getGreaterThan()!=null && (isInt || isFloat)){
			
			
			
			if ((isInt && ( ifClauseNum.intValue() >  compareStrNum.intValue()))
					|| (isFloat && ( ifClauseNum.floatValue() >  compareStrNum.floatValue()))) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", skipping block.");
				return myThenClause;
				
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", executing block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
				
			}
		}if(myAction.getGreaterThanEqualTo()!=null && (isInt || isFloat)){
			if ((isInt && ( ifClauseNum.intValue() >=  compareStrNum.intValue()))
					|| (isFloat && ( ifClauseNum.floatValue() >=  compareStrNum.floatValue()))) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", skipping block.");
				return myThenClause;
				
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", executing block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
			}
		}if(myAction.getLessThan()!=null && (isInt || isFloat)){
			if ((isInt && ( ifClauseNum.intValue() <  compareStrNum.intValue()))
					|| (isFloat && ( ifClauseNum.floatValue() <  compareStrNum.floatValue()))) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", skipping block.");
				return myThenClause;
				
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", executing block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
				
			}
		}if(myAction.getLessThanEqualTo()!=null && (isInt || isFloat)){
			if ((isInt && ( ifClauseNum.intValue() <=  compareStrNum.intValue()))
					|| (isFloat && ( ifClauseNum.floatValue() <=  compareStrNum.floatValue()))) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", skipping block.");
				return myThenClause;
				
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " is not equal to " + compareStrNum
						+ ", executing block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
			}//hpahuja  |SCE|add string comparisons to decision |start
		}if(myAction.getStringContains()!=null && !isInt && !isFloat ){
			if (ifClause.contains(compareStr)) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " contains" + compareStr + ", executing block.");
				return myThenClause;
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " does not contains to " + compareStr
						+ ", skipping block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
			}			
		}if(myAction.getStringEndsWith()!=null && !isInt && !isFloat ){
			if (ifClause.endsWith(compareStr)) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " ends with " + compareStr + ", executing block.");
				return myThenClause;
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " does not ends with " + compareStr
						+ ", skipping block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
			}			
		}if(myAction.getStringBeginsWith()!=null && !isInt && !isFloat ){
			if (ifClause.startsWith(compareStr)) {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " starts with " + compareStr + ", executing block.");
				return myThenClause;
			} else {
				sThisContext.log(logger, Level.DEBUG, ifClause
						+ " does not starts with " + compareStr
						+ ", skipping block.");
				if (myElseCondition != null)
					return myElseCondition;
				else
					return myThenClause.getEndOfBlock();
			}			
		}
		//hpahuja  |SCE|add string comparisons to decision |ends
		return getNextAction();
	}

	public void setNextAction(ServiceAction sa) {
		nextAction = sa;
	}

	public ServiceAction getNextAction() {
		return nextAction;
	}

	private ServiceAction nextAction = null;
	private ServiceDefinition myDef = null;
	private Conditiontype myAction = null;
	private ServiceActionBlock myElseCondition = null;
	private ServiceActionBlock myThenClause = null;

	static private String OKAY = "OKAY";

}

