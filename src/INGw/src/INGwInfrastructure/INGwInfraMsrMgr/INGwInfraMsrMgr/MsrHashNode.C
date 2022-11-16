#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement hashNode - implementation

     Type:     C implementation file

     Desc:     This file provides access to Measurement Hash Node

     File:     MsrHashNode.C

     Sid:      MsrHashNode.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrHashNode.h>
#include <INGwInfraMsrMgr/MsrValueMgr.h>

//C'tor
MsrHashNode::MsrHashNode (int aiCounterType,
                 std::string &astrCounter,
                 std::string &astrEntity,
                 std::string &astrParam,
                 unsigned long aulHashId):
mstrCounter (astrCounter),
mstrEntity (astrEntity),
mstrParam (astrParam),
mulHashId (aulHashId),
miCounterType (aiCounterType),
miPoolListIndex (0),
miPoolValueIndex (0),
mpNext (0)
{
  mpValueMgr = MsrValueMgr::getInstance();

  miPoolListIndex = aiCounterType;
  miPoolValueIndex = mpValueMgr->createValue (miPoolListIndex);
}

//D'tor
MsrHashNode::~MsrHashNode ()
{
}

int
MsrHashNode::getIndex ()
{
  return miPoolValueIndex;
}

int 
MsrHashNode::getDetails (std::string &astrCounter,
                    std::string &astrEntity,
                    std::string &astrParam)
{
  astrCounter = mstrCounter;
  astrEntity = mstrEntity;
  astrParam = mstrParam;

  return MSR_SUCCESS;
}

//update the param
int 
MsrHashNode::setValue (unsigned long aulValue)
{
  return mpValueMgr->setValue (miPoolListIndex, miPoolValueIndex, aulValue);
}

int 
MsrHashNode::increment (int aiFactor)
{
  return mpValueMgr->increment (miPoolListIndex, miPoolValueIndex, aiFactor);
}

int 
MsrHashNode::decrement (int aiFactor)
{
  return mpValueMgr->decrement (miPoolListIndex, miPoolValueIndex, aiFactor);
}

int 
MsrHashNode::setStatus (int aiStatus)
{
  return mpValueMgr->setStatus (miPoolListIndex, miPoolValueIndex, aiStatus);
}

//get the param info
unsigned long 
MsrHashNode::getValue ()
{
  unsigned long lulValue = 0;
  if (mpValueMgr->getValue (miPoolListIndex, miPoolValueIndex, lulValue)
      != MSR_SUCCESS)
    lulValue = 0;

  return lulValue;
}

int 
MsrHashNode::getStatus ()
{
  int liStatus = 0;
  if (mpValueMgr->getStatus (miPoolListIndex, miPoolValueIndex, liStatus)
      != MSR_SUCCESS)
    liStatus = 0;

  return liStatus;
}

