package com.genband.ase.alcx.TelnetInterface;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.genband.ase.alc.alcml.ALCServiceInterface.*;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.ase.common.Registry;

import java.io.Serializable;
import java.io.InputStream;
import java.io.OutputStream;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
@ALCMLActionClass(
    name="Telnet ALC Extensions",
    literalXSDDefinition="<xs:include schemaLocation=\"file://{$implPath}/TelnetALCInterfaces.xsd\"/>"
)
public class TelnetInterface extends ALCServiceInterfaceImpl implements Serializable, ServiceDefinitionListener, CommandHandler
{
    static Logger logger = Logger.getLogger(TelnetInterface.class.getName());
    private static String Name = new String("Telnet Interface");
    private String commandName = null;
    private String commandNameSpace = null;
    private String description = null;
	private OptionsListtype commandOptionsList = null;

    private ServiceContext sc = null;

    public TelnetInterface()
    {
    }

    public TelnetInterface(String commandName, String description, Object commandOptionsList, ServiceContext sc)
    {
        this.commandName = commandName;
        this.commandOptionsList = (OptionsListtype)commandOptionsList;
        this.sc = sc;
        this.description = description;
        this.commandNameSpace = sc.getNameSpace();
        ServiceDefinition.addServiceDefinitionListener(this);
    }

    public String getServiceName()
    {
        return Name;
    }

    @ALCMLActionMethod( name="add-telnet-command", help="adds telnet command\n")
    public void addTelnetCommand(ServiceContext sContext,
        	@ALCMLMethodParameter(	name="command-name",
        	asAttribute=true,
        	required=true,
        	help="name of command.\n")
    			String commandName,

        	@ALCMLMethodParameter(	name="description",
        	help="description of functionality of command.\n")
    			String description,

			@ALCMLMethodParameter( name="options-list",
				type="options-listtype",
				help="options for this command.\n")
				Object commandOptionsList
    			) throws ServiceActionExecutionException
    {
        TelnetServer telnetServer = (TelnetServer)
        Registry.lookup(Constants.NAME_TELNET_SERVER);
        telnetServer.registerHandler(commandName, new TelnetInterface(commandName, description, commandOptionsList, sContext));
        sContext.ActionCompleted();
    }

    public void ServiceNamespaceAdded(String namespace)
    {
    }

    public void ServiceNamespaceRemoved(String namespace)
    {
        if (namespace.equals(commandNameSpace))
        {
            TelnetServer telnetServer = (TelnetServer)
            Registry.lookup(Constants.NAME_TELNET_SERVER);
            telnetServer.unregisterHandler(commandName, this);
        }
    }

	private Optiontype getOption(String option)
	{
		if (commandOptionsList == null)
			return null;

		List<Optiontype> otl = commandOptionsList.getOption();
		for (Optiontype ot : otl)
		{
			String optionName = ot.getName();

			if (commandOptionsList.isAutoAbbreviate())
			{
				Pattern p = Pattern.compile("^" + option);
				logger.log(Level.DEBUG, "AutoAbbreviate -- comparing option regex ^"+option+" against "+ optionName);
				Matcher m = p.matcher(optionName);
				if (m.find())
				{
					logger.log(Level.DEBUG, "AutoAbbreviate -- Matched option regex ^"+option+" against "+ optionName);
					return ot;
				}
			}
			if (optionName.equals(option))
				return ot;

			if (!ot.isTakesArgument())
			{
				optionName = "no" + optionName;
				if (commandOptionsList.isAutoAbbreviate())
				{
					Pattern p = Pattern.compile("^" + option);
					logger.log(Level.DEBUG, "AutoAbbreviate -- comparing option regex ^"+option+" against "+ optionName);
					Matcher m = p.matcher(optionName);
					if (m.find())
					{
						logger.log(Level.DEBUG, "AutoAbbreviate -- Matched option regex ^"+option+" against "+ optionName);
						return ot;
					}
				}
				if (optionName.equals(option))
					return ot;
			}

		}
		return null;
	}

    public String execute(String command, String[] args, InputStream in, OutputStream out)
    throws CommandFailedException
    {
        StringBuffer buffer = new StringBuffer();
        try
        {
            ServiceContext sdContext = sc;
            ServiceDefinition iService = ServiceDefinition.getServiceDefinition(commandNameSpace, commandName);
			String oneBigArgString = " ";
			for (String arg : args)
			{
				oneBigArgString += arg;
				oneBigArgString += " ";
			}

			if (commandOptionsList != null)
			{
				/* check mandatory options */
				Pattern p = Pattern.compile("\\s+-(([\\w-_\\.]+)(\\s*=([\\w-_\\.]+|(?:\\\".*?\\\"))){0,1})");
				Matcher m = p.matcher(oneBigArgString);
				LinkedList<String> mandatoryOptions = new LinkedList();

				List<Optiontype> otl = commandOptionsList.getOption();
				for (Optiontype ot : otl)
				{
					if (ot.getUse().equals("optional"))
						continue;
					mandatoryOptions.add(ot.getName());
				}

				while (m.find())
				{
					logger.log(Level.DEBUG, "looking up parameter " + m.group(2));
					Optiontype ot = getOption(m.group(2));
					if (ot == null)
						return "Unkwown option "+m.group(2) + "\n" + getUsage(command);

					if (ot.getUse().equals("optional"))
						continue;

					logger.log(Level.DEBUG, "found mandatory parameter " + ot.getName());
					mandatoryOptions.remove(ot.getName());
				}

				boolean missingMandatory = false;
				String returnMandatory = "";
				for (String optionName : mandatoryOptions)
				{
					missingMandatory = true;
					returnMandatory += "Missing mandatory option "+optionName+".\n";
				}
				if (missingMandatory)
					return returnMandatory + getUsage(command);

				Pattern setp = Pattern.compile("\\s+-(([\\w-_\\.]+)(\\s*=([\\w-_\\.]+|(?:\\\".*?\\\"))){0,1})");
				Matcher setm = setp.matcher(oneBigArgString);
				LinkedList<String> options = new LinkedList();
				while (setm.find())
				{
					String rhs = setm.group(4);
					if (rhs == null)
					{
						Pattern no_p = Pattern.compile("^no(\\w+)");
						logger.log(Level.DEBUG, "considering option " + setm.group(2) + " against no<option>");
						Matcher no_m = no_p.matcher(setm.group(2));
						if (no_m.find())
							rhs = "false";
						else
							rhs = "true";
					}
					rhs = rhs.replaceAll("\"","");

					logger.log(Level.DEBUG, "setting option " + setm.group(2) + " as opt_" + getOption(setm.group(2)).getName() + " to " + rhs);
					sdContext.setAttribute("opt_" + getOption(setm.group(2)).getName(), rhs);
				}
			}
			else
			{
	            int size = 0;
	            for (String arg : args)
	            {
	                sdContext.setAttribute("arg"+size, arg);
	                size++;
	            }
			}

			iService.execute(sdContext);
			if (sdContext.getAttribute("buffer") == null)
				buffer.append("Command executed successfully.");
			else
				buffer.append(sdContext.getAttribute("buffer").toString());
        }

        catch (Exception e)
        {
            buffer.append("Command failed. " + e.getMessage());
            logger.log(Level.ERROR, "Command failed.", e);
        }

        return buffer.toString();
    }

    public String getUsage(String command)
    {
        StringBuffer buffer = new StringBuffer();

		if (commandOptionsList != null)
		{
			buffer.append("Usage: " + commandName + " ");
			String optionDescription = "";
			List<Optiontype> otl = commandOptionsList.getOption();
			for (Optiontype ot : otl)
			{
				if (ot.getUse().equals("optional"))
					buffer.append("[");

				if (ot.isTakesArgument())
					buffer.append("-" + ot.getName() + "=<" + ot.getName() + ">");
				else
					buffer.append("-" + ot.getName() + "|no" + ot.getName());

				if (ot.getDescription() != null)
					optionDescription += ot.getDescription() + "\n";

				if (ot.getUse().equals("optional"))
					buffer.append("]");

				buffer.append(" ");
			}

			if (description != null)
				buffer.append("\nCommand Desctription:\n" + description);

			if (!optionDescription.equals(""))
				buffer.append("Option Descriptions:\n" + optionDescription);
		}
		else
			buffer.append("No help available.");

        return buffer.toString();
    }
}

