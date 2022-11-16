package com.baypackets.ase.sbb.impl;

import java.io.Serializable;
import java.util.ArrayList;

public class SBBSubscriptionState implements Serializable
{
	private static final long serialVersionUID = 811801827859843890L;
	private ArrayList subscriptionList = new ArrayList();

	private boolean isByeReceived = false;

	public void addToList(String subID)
	{
		if(!(subscriptionList.contains((Object)subID)))
		{
			subscriptionList.add((Object)subID);
		}
	}

	public boolean isSubExist(String subID)
	{
		if(!(subscriptionList.contains((Object)subID)))
			return false;
		else
			return true;
	}

	public void removeFromList(String subID)
	{
		if(isSubExist(subID))
		{
			subscriptionList.remove(subID);
		}
	}

	public void byeRecieved()
	{
		isByeReceived = true;
	}

	public boolean isByeRecieved()
	{
		return isByeReceived;
	}

	public boolean isListEmpty()
	{
		if(subscriptionList.size()==0)
			return true;
		else
			return false;
	}
}
