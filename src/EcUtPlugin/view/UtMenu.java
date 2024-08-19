/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.view;

import EcUtPlugin.model.UTModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author ENERCOM36
 */
public class UtMenu extends JPopupMenu {

    private static UtMenu utMenu;

    /**
     * tree model
     */
    private UTModel model;

    /**
     * tree
     */
    private JTree tree;

    private MouseEvent evt;

    Runnable addNewStepAction;

    private UtMenu(MouseEvent evt, JTree tree, UTModel model, Runnable addNewStepAction) {
        this.evt = evt;
        this.tree = tree;
        this.model = model;
        this.addNewStepAction = addNewStepAction;
    }

    public static void showMenu(MouseEvent evt, JTree tree, UTModel model, Runnable addNewStepAction) {
        utMenu = new UtMenu(evt, tree, model, addNewStepAction);
//        utMenu.addMenuItem_AddNewStep("Add new Step");
        utMenu.addMenuItem_RenameUT("Rename UT");
        utMenu.addMenuItem_RemoveUT("Remove UT");
//        utMenu.addMenuItem_RemoveLastStep("Remove Last Step");

        utMenu.show(tree, evt.getPoint().x, evt.getPoint().y);
    }

    private void addMenuItem_RemoveLastStep(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // get current node
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                String selectedNodeName = (String) selectedNode.getUserObject();

                // check size, if size == 1, ignore
                List<String> listSteps = model.getUts().get(selectedNodeName);
                if (listSteps.size() == 1) {
                    JOptionPane.showMessageDialog(null, "Cannot Remove!\nAn UT has to have at least 1 step", "Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // show confirmation dialog
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the last Step?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                // update to model
                List<String> listStepOfOldUt = model.getUts().get(selectedNodeName);
                int oldSize = listStepOfOldUt.size();
                listStepOfOldUt.remove(oldSize - 1);

                // update tree
                DefaultMutableTreeNode nodeToRemove = (DefaultMutableTreeNode) selectedNode.getChildAt(oldSize - 1);
                ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(nodeToRemove);

                // set previous node for node item change event
//                previousNode = null;
            }
        });
        utMenu.add(itemExpandAllTree);
    }

    private void addMenuItem_RemoveUT(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // get current node
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                String selectedNodeName = (String) selectedNode.getUserObject();

                // show confirmation dialog
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove UT: \"" + selectedNodeName + "\"?\nAll steps will be removed!", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                // update to model
                List<String> listStepOfOldUt = model.getUts().get(selectedNodeName);
                model.getUts().remove(selectedNodeName);

                // update tree
                // remove current node
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) selectedNode.getParent();
                ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);
            }
        });
        utMenu.add(itemExpandAllTree);
    }

    private void addMenuItem_RenameUT(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // get current node
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                String selectedNodeName = (String) selectedNode.getUserObject();

                // enter new name
                String utName = (String) JOptionPane.showInputDialog("Enter UT Name (UT{XXX})", selectedNodeName); // TODO
                // neu bam cancel
                if (utName == null) {
                    return;
                }

                // check ten da ton tai chua
                if (model.getUts().containsKey(utName)) {
                    JOptionPane.showMessageDialog(null, "UT Name \"" + utName + "\" is existed", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // update to model
                ArrayList<String> listStepOfOldUt = new ArrayList<>(model.getUts().get(selectedNodeName));
                model.getUts().remove(selectedNodeName);
                model.getUts().put(utName, listStepOfOldUt);

                // update tree
                // remove current node
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) selectedNode.getParent();
                ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);

                // add new node to tree
                DefaultMutableTreeNode newUtNode = new DefaultMutableTreeNode(utName);
                for (int i = 1; i <= listStepOfOldUt.size(); i++) {
                    newUtNode.add(new DefaultMutableTreeNode("#" + i));
                }

                // get index to insert (theo thu tu tang dan String)
                int index = new ArrayList<String>(model.getUts().keySet()).indexOf(utName);
                ((DefaultTreeModel) tree.getModel()).insertNodeInto(newUtNode, rootNode, index);

                // expands that node
                tree.expandPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(newUtNode)));
            }
        });
        utMenu.add(itemExpandAllTree);
    }

    /**
     * register new menu item<br/>
     * Add new Step at That UT
     *
     * @param menuItemName
     */
    private void addMenuItem_AddNewStep(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addNewStepAction.run();
            }
        });
        utMenu.add(itemExpandAllTree);
    }

}
