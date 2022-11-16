package com.genband.ase.alcx.Expressions;

import java.lang.ClassCastException;

import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.math.BigInteger;

import javax.servlet.sip.SipServlet;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.ServletContext;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;

import com.genband.ase.alc.sip.SipServiceContextProvider;

import com.genband.ase.alc.alcml.ALCServiceInterface.*;

import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alcx.Expressions.*;
import com.genband.ase.alc.common.ALCBaseContext;
import com.genband.ase.alc.config.ALCConfigurator;

class ExpressionTimer {
    static Logger logger = Logger.getLogger(ExpressionTimer.class.getName());
    private Timer timer = new Timer();
    private long milliseconds;
	private long recurrance_seconds = 0;
	private String handlerName = null;
    private ServiceContext sContext;

    public ExpressionTimer(long milliseconds, long recurrance_seconds, String handlerName, ServiceContext sContext) {
        this.milliseconds = milliseconds;
        this.recurrance_seconds = recurrance_seconds;
        this.sContext = sContext;
		this.handlerName = handlerName;
    }

	public void cancel()
	{
		timer.cancel();
	}

    public void start() {
    	String origCallID = (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
    	if (recurrance_seconds == 0)
		{
			logger.log(Level.DEBUG, "[CALL-ID]"+origCallID+"[CALL-ID] "+"Schedule timer for " + milliseconds + "ms, no recurrance.");
			timer.schedule(new TimerTask() {
				public void run() {
					try
					{
						if (handlerName == null)
						{
							done();
						}
						else
						{
							ServiceDefinition handlerService = ServiceDefinition.getServiceDefinition(sContext.getNameSpace(), handlerName);
							if (handlerService != null)
							{
								try
								{
									handlerService.execute(sContext);
								}
								catch (ServiceActionExecutionException se)
								{

								}
							}
						}
					}
					catch (Exception e)
					{
					}
				}
				private void done() throws ServiceActionExecutionException {
					sContext.ActionCompleted("OKAY");
				}
			}, milliseconds);
		}
		else
		{
			logger.log(Level.DEBUG,"[CALL-ID]"+origCallID+"[CALL-ID] "+ "Schedule timer for " + milliseconds + "ms, recurrance is "+recurrance_seconds+"ms.");
			timer.schedule(new TimerTask() {
				public void run() {
					try
					{
						if (handlerName == null)
						{
							done();
						}
						else
						{
							ServiceDefinition handlerService = ServiceDefinition.getServiceDefinition(sContext.getNameSpace(), handlerName);
							if (handlerService != null)
							{
								try
								{
									handlerService.execute(sContext);
								}
								catch (ServiceActionExecutionException se)
								{

								}
							}
						}
					}
					catch (Exception e)
					{
					}
				}
				private void done() throws ServiceActionExecutionException {
					sContext.ActionCompleted("OKAY");
				}
			}, milliseconds, recurrance_seconds);
		}
    }
}

@ALCMLActionClass(
        name="Expressions Extensions for ALC"
)
public class Expressions extends ALCServiceInterfaceImpl
{
    static Logger logger = Logger.getLogger(Expressions.class.getName());
    private final static String LogAttr = "LOGGER";
    private final static String Name = "Expressions";
    public Expressions() { }

    public String getServiceName() { return Name; }

	@ALCMLActionMethod( name="log", isAtomic=true, help="Log a string to standard logging")
	static public void Log(ServiceContext sContext,
	 @ALCMLMethodParameter(name="level", help="Level at which this value should be displayed.  INFO, DEBUG, ERROR",
	 								asAttribute=true, required=true) String LogLevel,
	 @ALCMLMethodParameter(asAttribute=true, help="The ALCMLExpression to be displayed.",
	 								required=true) String value) throws ServiceActionExecutionException
	{
		try
		{
			Level lev = Level.toLevel(LogLevel);
			Logger locLogger = logger;

			Logger myLogger = (Logger)sContext.getAttribute(LogAttr);
			if (myLogger != null)
			{
				locLogger = myLogger;
			}
			locLogger.log(lev, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+value);
			sContext.ActionCompleted();
		}
		catch (IllegalArgumentException e)
		{
			throw new ServiceActionExecutionException("Level not found for logging -- " + LogLevel);
		}
	}

	@ALCMLActionMethod( name="create-timer",  isAtomic=true, help="Creates a timer on a new application session")
	public void CreateTimer(ServiceContext sContext,
							@ALCMLMethodParameter(name="handler",
												help="Name of service handler to be called on timeout.",
												asAttribute=true, required=true) String Handler,
							@ALCMLMethodParameter(name="context-reference",
												help="A literal handle for future reference to this time creation.",
												asAttribute=true) String ContextReference,
							@ALCMLMethodParameter(name="recurrance-ms",
												help="How often this timer should recur in milli-seconds.",
												asAttribute=true) Integer RecurranceMilliSeconds,
							@ALCMLMethodParameter(name="delay-ms",
												help="Delay prior to the start of this timer in milli-seconds.",
												asAttribute=true, required=true) Integer DelayMilliSeconds,
							@ALCMLMethodParameter(name="isPersistent",
												help="Identifies whether to make a timer persistent or not.",
												asAttribute=true, required=true) ALCMLExpression isPersistent
							) throws ServiceActionExecutionException
    {
		TimerService timerService = (TimerService) sContext.getAttribute(SipServlet.TIMER_SERVICE);
		Object myTimer = null;
		 String origCallID =(String) sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if(sContext.getAttribute(SipServiceContextProvider.Context) != null && timerService==null){
			logger.log(Level.DEBUG,"[CALL-ID]"+origCallID+"[CALL-ID] "+"Found timer service to be null. So trying to get it from servlet context.");
            timerService=(TimerService)((ServletContext)sContext.getAttribute(SipServiceContextProvider.Context)).getAttribute(SipServlet.TIMER_SERVICE);
        }
		
		
		if (timerService == null)
		{
			logger.log(Level.DEBUG,"[CALL-ID]"+origCallID+"[CALL-ID] "+ "using java timers for create-timer");
			if (RecurranceMilliSeconds == null)
	        	myTimer = new ExpressionTimer(DelayMilliSeconds.longValue(), 0, Handler, sContext);
	        else
	        	myTimer = new ExpressionTimer(DelayMilliSeconds.longValue(), RecurranceMilliSeconds.longValue(), Handler, sContext);

	        ((ExpressionTimer)myTimer).start();
		}
		else
		{
			logger.log(Level.DEBUG, "[CALL-ID]"+origCallID+"[CALL-ID] "+"using TimerService for create-timer");
			SipApplicationSession appSession = (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session);
			if (appSession == null)
			{
				SipFactory sipFactory = (SipFactory) sContext.getAttribute(SipServlet.SIP_FACTORY);
				appSession = sipFactory.createApplicationSession();
				sContext.setAttribute(SipServiceContextProvider.Session, appSession);
			}

			if (isPersistent == null)
				isPersistent = new ALCMLExpression(sContext, "false");
			
			if (RecurranceMilliSeconds == null)
					myTimer = timerService.createTimer(appSession, DelayMilliSeconds.longValue(), isPersistent.toBoolean(), new ExpressionsTimerListener(sContext, Handler));
			else
					myTimer = timerService.createTimer(appSession, DelayMilliSeconds.longValue(), RecurranceMilliSeconds.longValue(), true, isPersistent.toBoolean(), new ExpressionsTimerListener(sContext, Handler));
			
		}
		if (ContextReference != null)
			sContext.setAttribute(ContextReference, myTimer);

		sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="cancel-timer", isAtomic=true,  help="Cancels a timer, given it's reference")
	public void CancelTimer(ServiceContext sContext,
							@ALCMLMethodParameter(name="context-reference",
								help="The context handle for this timer.",
								asAttribute=true, required=true) String ContextReference
							) throws ServiceActionExecutionException
    {
		Object timer = sContext.getAttribute(ContextReference);
		if (timer != null)
		{
			if (timer instanceof ServletTimer)
			{
				ServletTimer st = (ServletTimer)timer;
				try
				{
					st = (ServletTimer)sContext.getAttribute(ContextReference);
				}
				catch (ClassCastException cce)
				{
					sContext.log(logger, Level.WARN, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"Variable " + ContextReference + " is not a timer");
				}
				if (st != null)
					st.cancel();
			}
			else if (timer instanceof ExpressionTimer)
			{
				((ExpressionTimer)timer).cancel();
			}
		}
		sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="wait", help="Waits for specified time")
	public void StartTimer(ServiceContext sContext, @ALCMLMethodParameter(asAttribute=true) Integer seconds)
    {
        ExpressionTimer timer = new ExpressionTimer(seconds * 1000, 0, null, sContext);
        timer.start();
    }

	@ALCMLActionMethod( name="set", isAtomic=true, help="Sets a context variable to given value or null")
    static public void Set(ServiceContext sContext,
    	@ALCMLMethodParameter(name="variable",
    							help="Name of the variable.",
    							asAttribute=true, required=true) String Variable,
    	@ALCMLMethodParameter(name="equal-to",
    							help="What to set this variable equal to.",
    							asAttribute=true, required=true) ALCMLExpression EqualTo,
    	@ALCMLMethodParameter(name="is-literal",
    							help="Instructs the ALC engine that the equal-to attribute describes a literal that is NOT an ALCMLExpession.  Therefore a ${variable} could be stored without resolution.",
    							asAttribute=true, defaultValue="false", type=ALCMLDefaults.XSDBoolean) Boolean isLiteral) throws ServiceActionExecutionException
    {
		if (Variable == null)
		{
			sContext.ActionFailed("Set variable is null");
		}

		sContext.log(logger, Level.DEBUG,"Setting " + Variable + " == " + EqualTo);
		if (isLiteral == true)
			sContext.setAttribute(Variable, EqualTo.toALCMLExpression());
		else
			sContext.setAttribute(Variable, EqualTo.toObject());

        sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="define-local", isAtomic=true, help="Defines a local context variable to given value or null")
    static public void DefineLocal(ServiceContext sContext,
    	@ALCMLMethodParameter(name="variable",
    							help="Name of the variable.",
    							asAttribute=true, required=true) String Variable,
    	@ALCMLMethodParameter(name="equal-to",
    							help="What to set this variable equal to.",
    							asAttribute=true) ALCMLExpression EqualTo,
    	@ALCMLMethodParameter(name="is-literal",
    							help="Instructs the ALC engine that the equal-to attribute describes a literal that is NOT an ALCMLExpession.  Therefore a ${variable} could be stored without resolution.",
								asAttribute=true, defaultValue="false", type=ALCMLDefaults.XSDBoolean) Boolean isLiteral) throws ServiceActionExecutionException
    {
		if (Variable == null)
		{
			sContext.ActionFailed("Set variable is null");
		}

		if (EqualTo == null)
			EqualTo = new ALCMLExpression(sContext, "null");
		sContext.log(logger, Level.DEBUG,"Setting " + Variable + " == " + EqualTo);
		if (isLiteral == true)
			sContext.defineLocalAttribute(Variable, EqualTo.toALCMLExpression());
		else
			sContext.defineLocalAttribute(Variable, EqualTo.toObject());

        sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="get-current-time", isAtomic=true, help="Defines a local context variable to given value or null")
    static public void getTime(ServiceContext sContext,
    	@ALCMLMethodParameter(name="results-in",
    							help="Where in context to put the results.",
    							asAttribute=true, required=true) String Variable
    	) throws ServiceActionExecutionException
    {
		sContext.setAttribute(Variable, String.valueOf((new Date()).getTime()));
        sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="define-global", isAtomic=true, help="Defines a global context variable to given value or null")
    static public void DefineGlobal(ServiceContext sContext,
    	@ALCMLMethodParameter(name="variable",
    							help="Name of the variable.",
    							asAttribute=true, required=true) String Variable,
    	@ALCMLMethodParameter(name="equal-to",
    							help="What to set this variable equal to.",
    							asAttribute=true) ALCMLExpression EqualTo,
    	@ALCMLMethodParameter(name="is-literal",
    							help="Instructs the ALC engine that the equal-to attribute describes a literal that is NOT an ALCMLExpession.  Therefore a ${variable} could be stored without resolution.",
								asAttribute=true, defaultValue="false", type=ALCMLDefaults.XSDBoolean) Boolean isLiteral) throws ServiceActionExecutionException
    {
		if (Variable == null)
		{
			sContext.ActionFailed("Set variable is null");
		}

		if (EqualTo == null)
			EqualTo = new ALCMLExpression(sContext, "null");
		sContext.log(logger, Level.DEBUG, "Setting " + Variable + " == " + EqualTo);
		
		if (isLiteral == true) {
			
			if(ALCBaseContext.getConfigRepository() !=null){
                             sContext.log(logger, Level.DEBUG, "Setting Literal in ALCConfigurator " + Variable + " == " + EqualTo); 
			    ((ALCConfigurator)ALCBaseContext.getConfigRepository()).setValue(Variable, EqualTo.toALCMLExpression());
		}	
			sContext.defineGlobalAttribute(Variable, EqualTo.toALCMLExpression());
		}else {
			sContext.defineGlobalAttribute(Variable, EqualTo.toObject());
		}

        sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="increment-counter", isAtomic=true, help="Increments a counter variable in the current context.")
    public void IncrementCounter(ServiceContext sContext,
    	@ALCMLMethodParameter(asAttribute=true,
    							help="The counter reference in context.",
    							required=true) String variable) throws ServiceActionExecutionException
    {
        String Results = (String)sContext.getAttribute(variable);
        if (Results == null)
            Results = "0";
        int i = Integer.parseInt(Results.trim());
        i++;
        sContext.log(logger, Level.DEBUG, "IncrementCounter " + variable + " now " + Integer.toString(i));
        sContext.setAttribute(variable, Integer.toString(i));
        sContext.ActionCompleted();
    }

	@ALCMLActionMethod( name="compare", isAtomic=true, help="Compares two values")
    public void Compare(ServiceContext sContext,
    	@ALCMLMethodParameter(name="identifier",
    									help="the left-hand side of comparison",
    									asAttribute=true) String Identifier1,
    	@ALCMLMethodParameter(name="to-identifier",
    									help="the right-hand side of comparison",
    									asAttribute=true) String Identifier2) throws ServiceActionExecutionException
    {
        if (Identifier1.equals(Identifier2))
        {
            sContext.log(logger, Level.DEBUG, "Compare " + Identifier1 + " == " + Identifier2 + " is true");
            sContext.ActionCompleted("true");
        }
        else
        {
            sContext.log(logger, Level.DEBUG, "Compare " + Identifier1 + " == " + Identifier2 + " is false");
            sContext.ActionCompleted("false");
        }
    }

    @ALCMLActionMethod( name="print", isAtomic=true, help="Display a string to standard output")
    static public void Print(ServiceContext sContext,
        @ALCMLMethodParameter(asAttribute=true, help="Value to be sent to standard output") String value) throws ServiceActionExecutionException
    {
        System.out.println(value);
        sContext.ActionCompleted();
    }

    public ServiceContext getContext() { return null; }
}

