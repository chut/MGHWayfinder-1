package com.MGHWayFinder;

import java.util.ArrayList;

public class Dijkstra {
    
	private static final int INFINITY = Integer.MAX_VALUE;
	private ArrayList<node> S = new ArrayList<node>();					//list of settled nodes		(shortest distance found)
	private ArrayList<node> Q = new ArrayList<node>();					//list of unsettled nodes	(distances not yet found)
	
	private node STARTNODE;												//ALL POTENTIAL PATHS ARE BUILT FROM THE ORIGIN NODE
	private static final int OFFSET = 90;								//MAP "ORIGIN" of Y+ = 0*, INSTEAD OF X+

	public Dijkstra(node START){
		this.STARTNODE = START;
		dijkstraAlgorithm(START);
		calculateNodeAngles();
	}
	 
    public ArrayList<node> getPath(node END){
    	ArrayList<node> P = new ArrayList<node>();
    	
    	P.add(END);														//initialize end node
		while(P.get(0) != STARTNODE){									//loop backwards from end node until beginning node
			P.add(0, P.get(0).getPreviousNode());						//reverse stacking of nodes
		}		
		return P;
    }
    
	private void dijkstraAlgorithm(node START){
		node u;															//node place holder in the loop
		
		Q.add(START);													//starts by adding the starting point to the Q of unsettled nodes 	(EMPTY BEFORE ADD)
		START.setBestDistance(0);										//initializes starting distance of the start node to 0				(BEST DISTANCE FROM STARTING POINT = 0)
		
		while(!Q.isEmpty()){											//loops so long as there are elements in Q 							(ELEMENTS ARE REMOVED FROM Q IN getMinimumNode() AND ADDED in relaxNeighbors())
			u = getMinimumNode();										//set u to the min node distance in ArrayList Q
			S.add(u);													//add u to the ArrayList S											(NODES WITH MINIMUM DISTANCES FOUND)
			relaxNeighbors(u);											//tests neighbor nodes, see function below							
		}
	}
	
	private void relaxNeighbors(node v){
		node o = null;
		double dist;
		
		for(int i = 0; i < v.getNeighbors().size(); i++){						//loop through neighbors of node v
			o = v.getNeighbors().get(i);
			if(!S.contains(o)) { 												//only look at neighbors NOT in S
				dist = cDistance(v, o);											//calculate distance between v and o
				if(o.getBestDistance() > (v.getBestDistance() + dist)){			//shorter distance found
					o.setBestDistance(dist + v.getBestDistance());				//set best distance of node o from start
					o.setPNode(v);												//set best previous node to v
					o.setPNodeDistance(dist);									//set intermediate distance from o-v
					Q.add(o);													//add node o to Q
				}
			}
		}
	}
	
	private double cDistance(node a, node b){									//calculates distances between nodes
		double x = a.getX()-b.getX();
		double y = a.getY()-b.getY();
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	private node getMinimumNode(){												//returns the node from the arrayList Q with the smallest distance from the starting point
		node out = null;
		double min = INFINITY;
		
		for(int i = 0; i < Q.size(); i++){
			if(Q.get(i).getBestDistance() < min){
				min = Q.get(i).getBestDistance();
				out = Q.get(i);
			}
		}
		Q.remove(out);															//removes the minimum node from Q
		return out;
	}
	
	private void calculateNodeAngles(){											//called after full table is built, calculates all of the nodes angles to their previous node in the shortest path.
		int angle;
		int x, y;
    	for(node it:S){
    		if(it != STARTNODE){
    			x = (it.getX() - it.getPreviousNode().getX());
    			y = (it.getY() - it.getPreviousNode().getY());
    			
    			angle = (int)Math.round(Math.toDegrees(Math.atan2(y, x)));
    			angle += OFFSET;
    			it.setPNodeAngle(angle);
    		}
    	}
    }
	
}
