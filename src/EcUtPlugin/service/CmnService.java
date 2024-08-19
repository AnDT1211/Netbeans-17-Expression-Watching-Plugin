/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.service;

/**
 *
 * @author ENERCOM36
 */
public interface CmnService {

    // "#1" -> 1
    public static int strToNum(String numStr) {
        String numStrAfter = new StringBuilder(numStr).delete(0, 1).toString();
        return Integer.parseInt(numStrAfter);
    }
}
