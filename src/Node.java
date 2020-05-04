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
	
	public int[] getBestOrder() {
		int[] ret = new int[bestOrder.size()];
		for (int i = 0; i < bestOrder.size(); i++) {
			ret[i] = bestOrder.get(i).getInd();
		}
		return ret;
	}
}
