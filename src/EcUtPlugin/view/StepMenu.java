/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.view;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

/**
 *
 * @author ENERCOM36
 */
public class StepMenu extends JPopupMenu {

    /**
     * tree
     */
    private JTree tree;

    private MouseEvent evt;

    private static StepMenu stepMenu;

    private Runnable insertNewStepAboveRunnable;
    private Runnable insertNewStepAfterunnable;
    private Runnable deleteStepRunnable;

    private StepMenu(MouseEvent evt, JTree tree, Runnable insertNewStepAboveRunnable, Runnable insertNewStepAfterRunnable, Runnable deleteStepRunnable) {
        this.evt = evt;
        this.tree = tree;
        this.insertNewStepAboveRunnable = insertNewStepAboveRunnable;
        this.insertNewStepAfterunnable = insertNewStepAfterRunnable;
        this.deleteStepRunnable = deleteStepRunnable;
    }

    public static void showMenu(MouseEvent evt, JTree tree, Runnable insertNewStepAboveRunnable, Runnable insertNewStepAfterRunnable, Runnable deleteStepRunnable) {
        stepMenu = new StepMenu(evt, tree, insertNewStepAboveRunnable, insertNewStepAfterRunnable, deleteStepRunnable);

        stepMenu.addMenuItem("Insert New Step Below", insertNewStepAfterRunnable);
        stepMenu.addMenuItem("Insert New Step Above", insertNewStepAboveRunnable);
        stepMenu.addMenuItem("Delete Step", deleteStepRunnable);

        stepMenu.show(tree, evt.getPoint().x, evt.getPoint().y);
    }

    void addMenuItem(String menuItemName, Runnable insertNewStepAboveRunnable) {
        JMenuItem itemExpandAllTree = new JMenuItem(menuItemName);
        itemExpandAllTree.addActionListener((ActionEvent ae) -> insertNewStepAboveRunnable.run());
        stepMenu.add(itemExpandAllTree);
    }
}
