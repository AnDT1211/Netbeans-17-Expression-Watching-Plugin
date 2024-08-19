/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.common;

import EcUtPlugin.model.UTModel;
import EcUtPlugin.service.FileService;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.modules.OnStop;

/**
 * auto save to file when closing netbeans app
 *
 * @author ENERCOM36
 */
@OnStop
public class SaveOnCloseNetbeans implements Runnable {

    public static UTModel model;
    public static File file;

    public static void setInfo(UTModel model, File file) {
        SaveOnCloseNetbeans.model = model;
        SaveOnCloseNetbeans.file = file;
    }

    @Override
    public void run() {
        if (file != null) {
            saveToFile(file);
        }
    }

    private void saveToFile(File file) {
        // save
        if (file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("UT Files (*.xlsx)", "xlsx"));
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getName();
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                String fileExtension = ".xlsx";
                if (!fileName.contains(fileExtension)) {
                    filePath += fileExtension;
                }

                file = new File(filePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                        file = null;
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "file đã tồn tại", "Error", JOptionPane.ERROR_MESSAGE);
                    file = null;
                    saveToFile(file);
                    return;
                }
            } else {
                return;
            }
        }
        // save object Model to file
        FileService.writeExcel(file, model);
    }

}
