import java.util.ArrayList;

public class Node {
	private int index;
	private ArrayList<Node> bestOrder;
	private ArrayList<Integer> values;
	private boolean sortByBest;
	public Node (int ind, boolean sortByBest) {
		index = ind;
		bestOrder = new ArrayList<Node>();
		values = new ArrayList<Integer>();
		this.sortByBest = sortByBest;
	}
	
	public Node goToNode(int ind) {
		for(Node n:bestOrder) 
			if(n.index==ind)
				return n;
		System.out.println("Error locating node");
		return null;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getHeight() {
		int height = 0;
		Node current = this;
		System.out.println("-------------------------");
		while (current.bestOrder.size() != 0) {
			System.out.println(current);
			height++;
			current = current.bestOrder.get(0);
			
		}
		System.out.println("-------------------------");
		return height;
		
	}
	
	public Node removeNode() {
		Node n = bestOrder.remove(0);
		values.remove(0);
		//System.out.println("Error locating node");
		return n;
	}
	
	public Node getNode(int ind) {
		return bestOrder.get(ind);
	}
	
	public Node removeNode(int ind) {
		for (int i = 0; i < bestOrder.size(); i++) {
			Node c = bestOrder.get(i);
			if (c.index == ind) {
				bestOrder.remove(i);
				values.remove(i);
				return c;
			}
		}
		System.out.println("Error deleting node");
		return null;
	}
	
	
	public void addNodeToEnd(Node n) {
		values.add(values.get(values.size()-1));
		bestOrder.add(n);
	}
	
	public void addNode(Node n, int val) {
		if(values.size() == 0) {
			values.add(val);
			bestOrder.add(n);
		}
		else {
			boolean added = false;
			for (int i = 0; i < values.size(); i++) {
				if(sortByBest) {
					if (val > values.get(i)) {
						values.add(i, val);
						bestOrder.add(i, n);
						added = true;
						i = values.size();
					}
				}
				else {
					if (val < values.get(i)) {
						values.add(i, val);
						bestOrder.add(i, n);
						added = true;
						i = values.size();
					}
				}
			}
			if (!added) {
				values.add(val);
				bestOrder.add(n);
				added = true;
			}
		}
	}
	
	public int getInd() {
		return index;
	}
	
	public String toString() {
		return "this should be true: " + sortByBest + " and this should match the real and node " + index;
	}
	
	public int[] getBestOrder() {
		int[] ret = new int[bestOrder.size()];
		for (int i = 0; i < bestOrder.size(); i++) {
			ret[i] = bestOrder.get(i).getInd();
		}
		return ret;
	}
}
