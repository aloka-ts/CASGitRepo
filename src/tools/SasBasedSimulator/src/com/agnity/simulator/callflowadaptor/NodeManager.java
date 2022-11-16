package com.agnity.simulator.callflowadaptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.IfNode;

public class NodeManager {
	private static Logger logger = Logger.getLogger(NodeManager.class);

	private List<Node> nodeList;
	CallFlowParser cfParser = new CallFlowParser();

	

	public Node getNodeWithID(int id){
		return (Node) getNodeList().get(id);
		//		Node matchedNode=null;
		//		Iterator itr = nodeList.iterator();
		//		while(itr.hasNext()){
		//			Node node = (Node)itr.next();
		//			if(node.getNodeId()==id){
		//				matchedNode=node;
		//				break;
		//			}
		//		}
		//		return matchedNode;
	}

	public void parseCallFlow(String fileName){
		nodeList = new ArrayList<Node>(8192);
		List<Node> nodes = cfParser.parseCallFlow(fileName);
		logger.debug("NODE LIST "+nodes);
		arrangeNodes(nodes);
		//arrangeChilds(nodeList);
	}

	private void arrangeNodes(List<Node> nodes) {
		List<Node> arrangedList=new ArrayList<Node>();
		for(int index=0; index<nodes.size(); index++){
			Iterator<Node> itr = nodes.iterator();
			while(itr.hasNext()){
				Node node = (Node)itr.next();
				if(node !=null && node.getNodeId()==index){
					//					logger.debug("***********************  arrange node at the index "+index +" and  node "+node);
					arrangedList.add(index, node);
				}
			}
		}
		arrangeChilds(arrangedList);
	}

	private void arrangeChilds(List<Node> nodes) {
		for(int index=0;index<nodes.size();index++){
//			Iterator<Node> itr = nodes.iterator();
//			while(itr.hasNext()){
//				Node node = (Node)itr.next();
			Node node= nodes.get(index);
//				if(node !=null && node.getNodeId()==index){
			if(node !=null ){
				logger.debug("++++++++++++++ Adding node at the index "+index);
				int[] prevNodeIds=node.getPrevNodeIds();
				for(int i=0;i<prevNodeIds.length;i++){
					//logger.debug("++++++++++++++ Adding node at the index "+index);
					int preNodeId=prevNodeIds[i];
					//						logger.debug("previous node id "+preNodeId);
					if(preNodeId>=0){
						//if(node.getPrevNodeId()>=0){
						//logger.debug("Adding node parent node"+preNodeId "+" object "+);
						Node parentNode = (Node)nodes.get(preNodeId);
						//							logger.debug("PARENT NODE "+parentNode);
						logger.debug("Adding node <" +node +"> at index "+index +" as child to parent node" +preNodeId +" object "+parentNode);
						parentNode.addChildNode(node);
					}
				}
				//logger.debug("++++++++++++++ Adding node at the index "+index);
				getNodeList().add(index, node);
			}
//			}
		}
	}

	public Node getCurrentNode(int currentNodeId){
		logger.debug("Inside getCurrentNode for node Id "+currentNodeId);
		// It means that it is the starting point of the application.
		if(currentNodeId<0){
			return null;
		}
		return (Node)getNodeList().get(currentNodeId);
	}

	public Node getNextNode(int currentNodeId){
		logger.debug("Inside getNextNode for node "+currentNodeId);
		// It means that it is the starting point of the application.
		if(currentNodeId<0){
			return (Node)getNodeList().get(0);
		}
		return getNextNode((Node)getNodeList().get(currentNodeId));
	}

	public Node getNextNode(Node currentNode){
		logger.debug("Inside getNextNode for node " + currentNode );
		Node childNode=null;
		// It means that it is the starting point of the application.
		if(currentNode==null){
			return (Node)getNodeList().get(0);
		}
		List<Node> childNodes = currentNode.getChildNodes();
		if(childNodes==null || childNodes.size()<0){
			logger.debug("no child node found");
			return null;
		}

		if(childNodes.size()==1 && !((Node)childNodes.get(0) instanceof IfNode)){
			childNode = (Node)childNodes.get(0);
		}
		else{
			Iterator<Node> itr = childNodes.iterator();
			while(itr.hasNext()){
				Node nextChild = (Node)itr.next();
				if(nextChild instanceof IfNode){
					//TODO handle IF nodes
//					IfNode ifNode = (IfNode)nextChild;
//					//if(dtmf!= null && ifNode.isMatched(dtmf)){
//					if(ifNode.isMatched(dtmf)){
//						int nextNodeId = ifNode.getNextNodeId();
//						childNode = (Node)nodeList.get(nextNodeId);
//						break;
//					}
				}else{
					childNode=nextChild;
				}
			}
		}
		logger.debug("Returning child node "+childNode);
		return childNode;
	}


	/**
	 * @return the nodeList
	 */
	public List<Node> getNodeList() {
		return nodeList;
	}
}
