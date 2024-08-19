/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.service;

import EcUtPlugin.model.OptionModel;
import EcUtPlugin.model.UTModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ENERCOM36
 */
public interface FileService {

    public static String optionPathFile = System.getenv("APPDATA") + "\\option_ec_ut_netbeans.setting";
    public static final String TEMP_PATH_FILE = System.getenv("APPDATA") + "\\ut";

    static boolean writeSettingObjectToFile(OptionModel model) {
        File optionFile = new File(optionPathFile);
        try {
            if (!optionFile.exists()) {
                optionFile.createNewFile();
                File.createTempFile("ecut", "netbeans");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(optionPathFile))) {
            outputStream.writeObject(model);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
    }

    static OptionModel readObjectFromSettingFile() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(optionPathFile)))) {
            OptionModel model = (OptionModel) inputStream.readObject();
            return model;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Hãy xóa file \"option_ec_ut_netbeans.setting\" ở \"%appdata%/roaming\" \nđể có được trải nghiệm tốt nhất!");
            return null;
        }
    }

//    static boolean writeObjectToFile(File file, UTModel model) {
//        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
//            outputStream.writeObject(model);
//            return true;
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(null, e.toString());
//            return false;
//        }
//    }
//
//    static UTModel readObjectFromFile(File file) {
//        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
//            UTModel model = (UTModel) inputStream.readObject();
//            return model;
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, e.toString());
//            return null;
//        }
//    }
    public static boolean checkTempDirExist() {
        File tempDir = new File(TEMP_PATH_FILE);
        try {
            if (!tempDir.exists()) {
                Files.createDirectory(Path.of(TEMP_PATH_FILE));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
        return true;
    }

    public static boolean writeExcel(File file, UTModel model) {
        try {
            // set UT Name for model
            model.setUtName(file.getName().substring(0, file.getName().length() - 5));

            Workbook wb = writeSheet(model);

            // create temp file
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String tempFileName = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".xlsx";

            File tempFile = new File(TEMP_PATH_FILE + "\\" + model.getUtName() + "-" + tempFileName);
            FileOutputStream fos = new FileOutputStream(tempFile);
            wb.write(fos);
            wb.close();

            // replace main file
            try {
                Files.copy(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "File đang mở, hãy tắt file\n" + "File backup được lưu ở:\n" + tempFile.toString());
                return false;
            }
            // delete temp file
            tempFile.delete();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }

    }

    private static Workbook writeSheet(UTModel model) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Map<String, List<String>> uts = model.getUts();
        for (String sheetName : uts.keySet()) {
            Sheet utSheet = wb.createSheet(sheetName);
            List<String> listExps = uts.get(sheetName);
            int startRowIdx = 2;
            for (int i = 0; i < listExps.size(); i++) {
                Row row = utSheet.createRow(startRowIdx++);
                Cell stepCell = row.createCell(2);
                stepCell.setCellValue("#" + (i + 1));

                String[] exps = listExps.get(i).split("\n");
                for (String exp : exps) {
                    if (!exp.isBlank()) {
                        Row rowExp = utSheet.createRow(startRowIdx++);
                        Cell cellExp = rowExp.createCell(3);
                        cellExp.setCellValue(exp);
                    }
                }
            }
            Row row = utSheet.createRow(startRowIdx);
            Cell stepCell = row.createCell(2);
            stepCell.setCellValue("END");
        }
        return wb;
    }

    public static UTModel readExcel(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fis);
            UTModel model = new UTModel();
            model.setUtName(file.getName().substring(0, file.getName().length() - 5));
            Map<String, List<String>> utsMap = model.getUts();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet utSheet = wb.getSheetAt(i);
                String utName = utSheet.getSheetName();
                List<String> listExps = new ArrayList<>();
                readSheet(utSheet, listExps);
                utsMap.put(utName, listExps);
            }
            return model;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return null;
        }

    }

    private static void readSheet(Sheet utSheet, List<String> listExps) throws Exception {
        for (int i = 0;; i++) {
            Row row = utSheet.getRow(i);
            if (row != null) {
                Cell cellUTName = row.getCell(2);
                if (cellUTName != null) {
                    String stepName = cellUTName.getStringCellValue();
                    if (stepName.equals("END")) {
                        break;
                    } else {
                        listExps.add("");
                    }
                }

                Cell cellExp = row.getCell(3);
                if (cellExp != null) {
                    String expValue = cellExp.getStringCellValue();
                    int idx = listExps.size() - 1;
                    String expValueModel = listExps.get(idx);
                    if (expValueModel.isBlank()) {
                        listExps.set(idx, expValue);
                    } else {
                        listExps.set(idx, expValueModel + "\n" + expValue);
                    }
                }
            }
        }
    }
}
