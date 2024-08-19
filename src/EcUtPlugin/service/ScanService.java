/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;

/**
 *
 * @author ENERCOM36
 */
public interface ScanService {

    static Method[] getAllMethods(String fullQualifiedClassName) {
        Class cls = getClasRef(fullQualifiedClassName);
        if (cls == null) {
            return null;
        }

        List<Method> listMethod = new ArrayList<>();
        listMethod.addAll(List.of(cls.getDeclaredMethods()));
        listMethod.addAll(List.of(cls.getMethods()));

        return listMethod.stream().distinct().toArray(Method[]::new);
    }

    private static Class getClasRef(String fullQualifiedClassName) {
        Project[] projects = OpenProjects.getDefault().getOpenProjects();
        for (Project project : projects) {
            Sources source = ProjectUtils.getSources(project);

            SourceGroup[] sgs = source.getSourceGroups("java");
            for (SourceGroup sg : sgs) {
                try {
                    FileObject fileObject = sg.getRootFolder();
                    ClassPath cp = ClassPath.getClassPath(fileObject, ClassPath.EXECUTE);
                    ClassLoader cl = cp.getClassLoader(false);
                    Class cls = cl.loadClass(fullQualifiedClassName);
                    if (cls == null) {
                        continue;
                    }
                    return cls;
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        return null;
    }

    static Field[] getAllFields(String fullQualifiedClassName) {
        Class cls = getClasRef(fullQualifiedClassName);
        if (cls == null) {
            return null;
        }
        List<Field> fields = new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));
        fields.addAll(new ArrayList<>(Arrays.asList(cls.getFields())));
        cls = cls.getSuperclass();
        while (cls != Object.class) {
            fields.addAll(new ArrayList<>(Arrays.asList(cls.getDeclaredFields())));
            fields.addAll(new ArrayList<>(Arrays.asList(cls.getFields())));
            cls = cls.getSuperclass();
        }

        return fields.stream().distinct().toArray(Field[]::new);
    }

    static String getFullQualifiedClassName(JTextComponent editorComponent) {
        String content = editorComponent.getText();
        String[] lines = content.split("\r\n");

        String packName = "";
        String className = "";
        for (String line : lines) {
            line = line.trim();
            // get package name
            if (line.startsWith("package")) {
                packName = line.split(" ")[1];
                packName = packName.substring(0, packName.length() - 1);
            } else if (line.startsWith("public class")) {
                int idxClass = line.indexOf("class");
                line = line.substring(idxClass, line.length());
                className = line.split(" ")[1];
                break;
            }
        }
        return packName + "." + className;
    }
}
