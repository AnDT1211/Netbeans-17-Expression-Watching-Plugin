/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JTextArea;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author ENERCOM36
 */
@ServiceProvider(service = CommonLookUp.class)
public class CommonLookUp {

    private boolean isAuto;
    private String qualifiedNameClass;
    private Field[] fields;
    private Method[] methods;
    private JTextArea watchTextArea;

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    public boolean isIsAuto() {
        return isAuto;
    }

    public void setIsAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public String getQualifiedNameClass() {
        return qualifiedNameClass;
    }

    public void setQualifiedNameClass(String qualifiedNameClass) {
        this.qualifiedNameClass = qualifiedNameClass;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public JTextArea getWatchTextArea() {
        return watchTextArea;
    }

    public void setWatchTextArea(JTextArea watchTextArea) {
        this.watchTextArea = watchTextArea;
    }

}
