import java.util.ArrayList;

public class Node {
	Node parent;
	ArrayList<Node>children;
	public Node(Node p) {
		children=new ArrayList<Node>(1);
		parent=p;
		if(p!=null) {
			p.children.add(this);
		}
	}
	
	public int compareTo(Node n) {
		return 1;
	}
	
	public int getChildren(Node b,int level) {
		if(b.children.size()!=0)
		System.out.format(" %d ",b.children.size());
		int a=0; 
		for(Node n:b.children) {
			a+=n.children.size();
			if(n.children.size()!=0)
				System.out.print("  l: "+level);
			a+=getChildren(n,(level+1));
		}
		if(b.children.size()!=0)
		System.out.println();
		
		return a;
	}
}
