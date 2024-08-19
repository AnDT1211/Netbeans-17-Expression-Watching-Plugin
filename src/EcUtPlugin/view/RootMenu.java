/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.view;

//import EcUtPlugin.controller.EcUtController;
import EcUtPlugin.model.UTModel;
import EcUtPlugin.service.TreeService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

/**
 *
 * @author ENERCOM36
 */
public class RootMenu extends JPopupMenu {

    /**
     * singleton instance
     */
    private static RootMenu rootMenu;

    /**
     * tree model
     */
    private UTModel model;

    /**
     * tree
     */
    private JTree tree;

    /**
     * mouse event
     */
    MouseEvent evt;

    Consumer<String> addNewNodeConsumer;

    Runnable openAction;
    
    Runnable createNewSetAction;

    private RootMenu(MouseEvent evt, JTree tree, UTModel model, Consumer<String> addNewNodeConsumer, Runnable openAction, Runnable createNewSetAction) {
        super();
        this.evt = evt;
        this.tree = tree;
        this.model = model;
        this.addNewNodeConsumer = addNewNodeConsumer;
        this.openAction = openAction;
        this.createNewSetAction = createNewSetAction;
    }

    public static void showMenu(MouseEvent evt, JTree tree, UTModel model, Consumer<String> addNewNodeConsumer,
            Runnable saveAction, Runnable saveAsAction, Runnable openAction, Runnable createNewSetAction) {
        rootMenu = new RootMenu(evt, tree, model, addNewNodeConsumer, openAction, createNewSetAction);
        rootMenu.addMenuItem_AddNewUt("Add new UT");
        rootMenu.addMenuItem_Save("Save", saveAction);
        rootMenu.addMenuItem_SaveAs("Save As", saveAsAction);
        rootMenu.addMenuItem_CollapseAllTree("Collapse All");
        rootMenu.addMenuItem_ExpandAllTree("Expand All");
        rootMenu.addMenuItem_Open("Open");
        rootMenu.addMenuItem_CreateNewSet("Create New Set");

        rootMenu.show(tree, evt.getPoint().x, evt.getPoint().y);
    }

    private void addMenuItem_CreateNewSet(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener((ActionEvent ae) -> {
            createNewSetAction.run();
        });
        rootMenu.add(itemExpandAllTree);
    }
    
    private void addMenuItem_Open(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener((ActionEvent ae) -> {
            openAction.run();
        });
        rootMenu.add(itemExpandAllTree);
    }

    /**
     * register new menu item<br/>
     * expand all tree
     *
     * @param menuItemName
     */
    private void addMenuItem_ExpandAllTree(String menuItemName) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener((ActionEvent ae) -> {
            TreeService.expandAllNodes(tree);
        });
        rootMenu.add(itemExpandAllTree);
    }

    /**
     * register new menu item<br/>
     * Save
     *
     * @param menuItemName
     */
    private void addMenuItem_Save(String menuItemName, Runnable saveAction) {
        JMenuItem itemSave = new JMenuItem(menuItemName);
        itemSave.addActionListener((ActionEvent ae) -> {
            saveAction.run();
        });
        rootMenu.add(itemSave);
    }

    /**
     * register new menu item<br/>
     * Save as
     *
     * @param menuItemName
     */
    private void addMenuItem_SaveAs(String menuItemName, Runnable saveAsAction) {
        JMenuItem itemSaveAs = new JMenuItem(menuItemName);
        itemSaveAs.addActionListener((ActionEvent ae) -> {
            saveAsAction.run();
        });
        rootMenu.add(itemSaveAs);
    }

    /**
     * register new menu item<br/>
     * collapse all tree
     *
     * @param menuItemName
     */
    private void addMenuItem_CollapseAllTree(String menuItemName) {
        JMenuItem itemCollapseAllTree = new JMenuItem(menuItemName);
        itemCollapseAllTree.addActionListener((ActionEvent ae) -> {
            TreeService.collapseAllNodes(tree);
        });
        rootMenu.add(itemCollapseAllTree);
    }

    /**
     * register new menu item<br/>
     * add new UT
     *
     * @param menuItemName
     */
    private void addMenuItem_AddNewUt(String menuItemName) {
        JMenuItem itemAddNewUt = new JMenuItem(menuItemName);
        itemAddNewUt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // enter new name
                String nameOfNextNode = (String) JOptionPane.showInputDialog("Enter UT Name (UT{XXX})");
                nameOfNextNode = nameOfNextNode.trim();

                // neu bam cancel hoac blank
                if (nameOfNextNode == null || nameOfNextNode.isBlank()) {
                    return;
                }

                // check ten da ton tai chua
                if (model.getUts().containsKey(nameOfNextNode)) {
                    JOptionPane.showMessageDialog(null, "UT Name \"" + nameOfNextNode + "\" is existed", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // save to model
                model.getUts().putIfAbsent(nameOfNextNode, new ArrayList<String>(Arrays.asList("")));

                // insert new node
                addNewNodeConsumer.accept(nameOfNextNode);
//                ecUtController.addNewUtNode(tree, nameOfNextNode);
//                // insert new node
//                DefaultMutableTreeNode nextNode = new DefaultMutableTreeNode(nameOfNextNode);
//                DefaultMutableTreeNode nextChildNode = new DefaultMutableTreeNode("#1");
//                nextNode.add(nextChildNode);
//
//                // get index to insert (theo thu tu tang dan String)
//                int index = new ArrayList<String>(model.getUts().keySet()).indexOf(nameOfNextNode);
//                ((DefaultTreeModel) tree.getModel()).insertNodeInto(nextNode, TreeService.getRootNode(tree), index);
//
//                // select added node
//                TreePath newPath = TreeService.getTreePathFromTreeModel(tree, nextChildNode);
//                tree.setSelectionPath(newPath);
//                selectedStepNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
            }
        });
        rootMenu.add(itemAddNewUt);
    }

}
