
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileView;

public class A51FileTree extends JPanel {
	
	protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;

    public A51FileTree(File rootFolder) {
    	super(new GridLayout(1,0));
        
    	rootNode = new DefaultMutableTreeNode(rootFolder);
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
   
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				/*TreePath selectedPath = e.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
				File selectedFile = (File)node.getUserObject();
	        	if(selectedFile.isDirectory())
	        	{
	        		tree.setEditable(true);
	        		System.out.println("Tree set to editable");
	        	}
	        	else
	        	{
	        		tree.setEditable(false);
	        		System.out.println("Tree set to ineditable");
	        	}*/
				
			}});
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new FileTreeCellRenderer());
        
        add(tree);
    }
    

    /** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }
    }

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
            
        } else {
        	// Modified so that adding to a child node DOES NOT convert child node to parent node
        	//		since this behavior is NOT mirrored on the hard disk
        	//		Will need a method to create new folders if this is desired.
            parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }
	
        //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(index));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
    
    // This class is used to tell the tree how it should render its nodes
    // If file
    private static class FileTreeCellRenderer extends DefaultTreeCellRenderer 
    {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                File fileNode;
                //if(node.getUserObject() instanceof String)
                //{
                	//fileNode = new File((String)node.getUserObject());
                //}
                //else
                //{
                	fileNode = (File)node.getUserObject();
                //}
                if (fileNode.isDirectory()) {
                	if(expanded) {
                		// Should display openIcon, but since both icons are the same it doesn't really matter
                		setIcon(UIManager.getIcon("Tree.openIcon"));
                	} else {
                		// Displays closed icon
                		setIcon(UIManager.getIcon("Tree.closedIcon"));
                	}
                	
                	// If node is root node, displays hard drive icon
                	if(node.isRoot()) {
                		setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
                	}
                } else {
                    // Any other node is a leaf node                
                    setIcon(UIManager.getIcon("Tree.leafIcon"));
                } 
                
                setText(fileNode.getName());
            }
            return this;
        }
    }
}