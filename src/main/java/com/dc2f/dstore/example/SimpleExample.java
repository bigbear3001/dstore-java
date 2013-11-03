package com.dc2f.dstore.example;

import com.dc2f.dstore.hierachynodestore.Commit;
import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.hierachynodestore.WorkingTreeUtils;
import com.dc2f.dstore.storage.map.HashMapStorage;

public class SimpleExample {
	public static void main(String[] args) {
		HierarchicalNodeStore nodeStore = new HierarchicalNodeStore(new HashMapStorage());
		
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		
		WorkingTreeNode rootNode1 = wt1.getRootNode();
		WorkingTreeNode a = rootNode1.addChild("A");
		WorkingTreeNode b = rootNode1.addChild("B");
		
		WorkingTreeNode a1 = a.addChild("A1");
		a.addChild("A2");
		
		WorkingTreeNode b1 = b.addChild("B1");
		b.addChild("B2");
		
		analyze("created some nodes in wt1", wt1);
		
		
		WorkingTree wt2 = nodeStore.checkoutBranch("master");
		analyze("new wt2 must see only original root node", wt2);
		
		System.out.println("(1) commit wt1");
		Commit c1 = wt1.commit("");
		analyze("root node id of wt1 must have changed", wt1);
		
		System.out.println("(2) commit wt1 a second time without changing anything");
		Commit c2 = wt1.commit("");
		
		analyze("nothing has to be changed by the second commit", wt1);
		
		analyze("wt2 must not see any changes", wt2);
		
		b1.addChild("B11");
		Commit c3 = wt1.commit("");
		
		analyze("(3) B1, B, and root node must have changed after adding B11", wt1);
		
		WorkingTree wt3 = nodeStore.checkoutBranch("master");
		
		analyze("even a new wt3 of master must not see any changes", wt3);

		
//		a1.addChild("A11");
//		wt1.commit("");
//		
//		analyze("Committed A11", wt1);
	}

	private static void analyze(String message, WorkingTree wt) {
		System.out.println();
		System.out.println(message);
		System.out.println(WorkingTreeUtils.debugRecursiveTree(wt.getRootNode()));
	}
}