///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/actionListener.java to edit this template
// */
//package EcUtPlugin;
//
//import java.awt.MouseInfo;
//import java.awt.Point;
//import java.awt.Robot;
//import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//import javax.swing.JOptionPane;
//import org.openide.awt.ActionID;
//import org.openide.awt.ActionReference;
//import org.openide.awt.ActionReferences;
//import org.openide.awt.ActionRegistration;
//import org.openide.util.NbBundle.Messages;
//
//@ActionID(
//        category = "Tools",
//        id = "EcUtPlugin.ScreenShotAction"
//)
//@ActionRegistration(
//        displayName = "#CTL_ScreenShotAction"
//)
//@ActionReferences({
//    @ActionReference(path = "Menu/Tools", position = -300, separatorBefore = -350, separatorAfter = -250),
//    @ActionReference(path = "Shortcuts", name = "DOS-O")
//})
//@Messages("CTL_ScreenShotAction=ScreenShot")
//public final class ScreenShotAction implements ActionListener {
//    private Robot robot;
//    static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
//    
//    
//    {
//        try {
//            robot = new Robot();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    
//    
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if (robot == null) {
//            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//        
//        try {
//            Point currentPoint = MouseInfo.getPointerInfo().getLocation();
//            int xPos = (int) currentPoint.getX();
//            int yPos = (int) currentPoint.getY();
//            
//            robot.keyPress(KeyEvent.VK_CONTROL);
//            robot.keyPress(KeyEvent.VK_SHIFT);
//            robot.keyPress(KeyEvent.VK_ALT);
//            robot.keyPress(KeyEvent.VK_PRINTSCREEN);
//
//            robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
//            robot.keyRelease(KeyEvent.VK_ALT);
//            robot.keyRelease(KeyEvent.VK_SHIFT);
//            robot.keyRelease(KeyEvent.VK_CONTROL);
//            
//            Thread.sleep(400);
//            
//            robot.mouseMove(0, (int) screenHeight);
//            
//            Thread.sleep(100);
//            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//            
//            robot.mouseMove(xPos, yPos);
//            
//            
//        } catch (Exception ex) {
//            
//        }
//        
//        
//        
//        
//    }
//}
