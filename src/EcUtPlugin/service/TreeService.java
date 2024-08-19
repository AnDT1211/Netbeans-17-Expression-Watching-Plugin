/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.service;

import EcUtPlugin.model.UTModel;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author ENERCOM36
 */
public interface TreeService {

    /**
     * expand all nodes
     *
     * @param tree
     */
    static void expandAllNodes(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * collapse all nodes
     *
     * @param tree
     */
    static void collapseAllNodes(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.collapseRow(i);
        }
    }

    static DefaultMutableTreeNode initTree(UTModel model) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(model.getUtName());

        model.getUts().forEach((String utName, List<String> steps) -> {
            DefaultMutableTreeNode utNode = new DefaultMutableTreeNode(utName);
            for (int i = 1; i <= steps.size(); i++) {
                DefaultMutableTreeNode utStep = new DefaultMutableTreeNode("#" + i);
                utNode.add(utStep);
            }
            rootNode.add(utNode);
        });
        return rootNode;
    }

    /**
     * utIdx = -1 va stepIdx = -1 -> lay path cua root <br/>
     * utIdx != -1 va stepIdx = -1 -> lay path cua ut <br/>
     * utIdx != -1 va stepIdx != -1 -> lat path cua step
     *
     * @param utIdx
     * @param stepIdx
     */
    static TreePath getTreePathFromIdx(JTree tree, int utIdx, int stepIdx) {
        if (utIdx == -1 && stepIdx == -1) {
            return new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(getRootNode(tree)));
        } else if (utIdx != -1 && stepIdx == -1) {
            return new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(getUtNodeAtIdx(tree, utIdx)));
        }
        return new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(getStepNodeAtUtNodeIdx(tree, utIdx, stepIdx)));
    }

    static DefaultMutableTreeNode getRootNode(JTree tree) {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }

    static TreePath getTreePathFromTreeModel(JTree tree, TreeNode treeNode) {
        return new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(treeNode));
    }

    static DefaultMutableTreeNode getUtNodeAtIdx(JTree tree, int idx) {
        return (DefaultMutableTreeNode) getRootNode(tree).getChildAt(idx);
    }

    static DefaultMutableTreeNode getStepNodeAtUtNodeIdx(JTree tree, int utIdx, int stepidx) {
        return (DefaultMutableTreeNode) getUtNodeAtIdx(tree, utIdx).getChildAt(stepidx);
    }

    static String getNameFromNode(TreeNode utNode) {
        return (String) ((DefaultMutableTreeNode) utNode).getUserObject();
    }
}
