import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

// Draws the StudentBST as a tree diagram (circles + lines)
public class BSTVisualizerPanel extends JPanel {

    private static class DrawNode {
        int rollNumber;
        int x, y;
        DrawNode left, right;
    }

    private DrawNode drawRoot;
    private static final int NODE_RADIUS = 22;
    private static final int VERTICAL_GAP = 80;

    public BSTVisualizerPanel(StudentBST tree) {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 450));
        refreshTree(tree);
    }

    public void refreshTree(StudentBST tree) {
        StudentBST.Node root = (tree == null) ? null : tree.getRoot();
        drawRoot = buildDrawTree(root);
        assignPositions();
        repaint();
    }

    private DrawNode buildDrawTree(StudentBST.Node node) {
        if (node == null) return null;
        DrawNode d = new DrawNode();
        d.rollNumber = node.rollNumber;
        d.left = buildDrawTree(node.left);
        d.right = buildDrawTree(node.right);
        return d;
    }

    private int nextX;
    private static final int LEAF_SPACING = 70;

    private void assignPositions() {
        nextX = 60;
        assignPositionsRec(drawRoot, 0);
        centerTreeInPanel();
    }

    private void assignPositionsRec(DrawNode node, int depth) {
        if (node == null) return;

        assignPositionsRec(node.left, depth + 1);

        if (node.left == null && node.right == null) {
            node.x = nextX;
            nextX += LEAF_SPACING;
        }

        assignPositionsRec(node.right, depth + 1);

        if (node.left != null && node.right != null) {
            node.x = (node.left.x + node.right.x) / 2;
        } else if (node.left != null) {
            node.x = node.left.x + LEAF_SPACING / 2;
        } else if (node.right != null) {
            node.x = node.right.x - LEAF_SPACING / 2;
        }

        node.y = 50 + depth * VERTICAL_GAP;
    }

    private void centerTreeInPanel() {
        if (drawRoot == null) return;
        int[] minMax = { Integer.MAX_VALUE, Integer.MIN_VALUE };
        collectMinMaxX(drawRoot, minMax);

        int treeCenter = (minMax[0] + minMax[1]) / 2;
        int panelCenter = getPreferredSize().width / 2;
        int shift = panelCenter - treeCenter;

        shiftX(drawRoot, shift);
    }

    private void collectMinMaxX(DrawNode node, int[] minMax) {
        if (node == null) return;
        minMax[0] = Math.min(minMax[0], node.x);
        minMax[1] = Math.max(minMax[1], node.x);
        collectMinMaxX(node.left, minMax);
        collectMinMaxX(node.right, minMax);
    }

    private void shiftX(DrawNode node, int shift) {
        if (node == null) return;
        node.x += shift;
        shiftX(node.left, shift);
        shiftX(node.right, shift);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("Binary Search Tree", getWidth() / 2 - 60, 25);

        if (drawRoot == null) {
            g2.drawString("No students yet", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }

        drawEdges(g2, drawRoot);
        drawNodes(g2, drawRoot);
    }

    private void drawEdges(Graphics2D g2, DrawNode node) {
        if (node == null) return;
        g2.setColor(Color.DARK_GRAY);
        if (node.left != null) {
            g2.drawLine(node.x, node.y, node.left.x, node.left.y);
            drawEdges(g2, node.left);
        }
        if (node.right != null) {
            g2.drawLine(node.x, node.y, node.right.x, node.right.y);
            drawEdges(g2, node.right);
        }
    }

    private void drawNodes(Graphics2D g2, DrawNode node) {
        if (node == null) return;

        g2.setColor(new Color(198, 224, 198));
        g2.fillOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        g2.setColor(Color.BLACK);
        g2.drawOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        String text = String.valueOf(node.rollNumber);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, node.x - textWidth / 2, node.y + fm.getAscent() / 2 - 2);

        drawNodes(g2, node.left);
        drawNodes(g2, node.right);
    }
}