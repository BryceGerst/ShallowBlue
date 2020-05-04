import java.util.ArrayList;

public class Node2 {
	Node2 parent;
	ArrayList<Node2>children;
	public Node2(Node2 p) {
		children=new ArrayList<Node2>(1);
		parent=p;
		if(p!=null) {
			p.children.add(this);
		}
	}
	
	public int compareTo(Node2 n) {
		return 1;
	}
	
	public int getChildren(Node2 b,int level) {
		if(b.children.size()!=0)
		System.out.format(" %d ",b.children.size());
		int a=0; 
		for(Node2 n:b.children) {
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
