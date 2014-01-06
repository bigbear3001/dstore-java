package com.dc2f.dstore.test.hierachynodestore;

import static com.dc2f.dstore.test.TreeAssertions.assertTree;
import static com.dc2f.dstore.test.TreeAssertions.node;
import static com.dc2f.dstore.test.TreeAssertions.properties;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.dc2f.dstore.hierachynodestore.HierarchicalNodeStore;
import com.dc2f.dstore.hierachynodestore.WorkingTree;
import com.dc2f.dstore.hierachynodestore.WorkingTreeNode;
import com.dc2f.dstore.storage.StorageBackend;
import com.dc2f.dstore.test.TreeAssertions.ExpectedNode;

public abstract class AbstractHierarchicalNodeStoreTest {

	private HierarchicalNodeStore nodeStore;
	
	@Before
	public void setupDstore() throws IOException {
		StorageBackend storageBackend = initStorageBackend();
		nodeStore = new HierarchicalNodeStore(storageBackend);
	}

	protected abstract StorageBackend initStorageBackend();
	
	@Test
	public void testWorkingTreeVersions() {
		WorkingTree wt1 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root1 = wt1.getRootNode();

		ExpectedNode onlyRoot = node(properties("name", ""));

		assertTree("wt1 must only see the root node", onlyRoot, root1);
		
		WorkingTreeNode a = root1.addChild("A");
		WorkingTreeNode b = root1.addChild("B");
		
		ExpectedNode expectedAB = node(properties("name", ""),
				node(properties("name", "A")),
				node(properties("name", "B"))
			);
		
		assertTree("Changes are visible inside uncommited working tree wt1", expectedAB, root1);
		assertSame("Get rootNode() must always return the same root node", root1, wt1.getRootNode());
		
		WorkingTree wt2 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root2 = wt2.getRootNode();
		assertTree("Changes are not visible in wt2 which was checked out before commiting wt1", onlyRoot, root2);

		wt1.commit("Commiting wt1, nothing must have changed in already existing working trees");
		assertTree("root1 data didn't change during commit", expectedAB, root1);
		assertSame("Get rootNode() must return the same root node as before commiting", root1, wt1.getRootNode());
		
		WorkingTree wt3 = nodeStore.checkoutBranch("master");
		WorkingTreeNode root3 = wt3.getRootNode();
		
		assertTree("wt3 must see changes commited by wt1", expectedAB, root3);
	}
}
