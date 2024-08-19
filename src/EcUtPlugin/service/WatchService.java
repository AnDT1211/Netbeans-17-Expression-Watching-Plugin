/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Watch;

/**
 *
 * @author ENERCOM36
 */
public interface WatchService {

    /**
     * add expressions to watches window
     *
     * @param values expressions separated by new lines
     */
    static void setValueToWatchesWindow(String values) {
        String[] watches = values.split("\n");

        DebuggerManager debug = DebuggerManager.getDebuggerManager();
        debug.removeAllWatches();

        for (String watch : watches) {
            if (StringUtils.isNotBlank(watch)) {
                debug.createWatch(watch.trim());
            }
        }
    }

    static void appendValueToWatchesWindow(String values) {
        String[] watches = values.split("\n");

        DebuggerManager debug = DebuggerManager.getDebuggerManager();

        for (String watch : watches) {
            if (StringUtils.isNotBlank(watch)) {
                watch = watch.trim();
                if (!Arrays.stream(debug.getWatches()).map(Watch::getExpression).anyMatch(watch::equals)) {
                    debug.createWatch(debug.getWatches().length, watch.trim());
                }
            }
        }
    }

    static String getValueToWatchTextArea() {
        DebuggerManager debug = DebuggerManager.getDebuggerManager();
        return Stream.of(debug.getWatches()).map(x -> x.getExpression()).collect(Collectors.joining("\n", "\n", "\n"));
    }
}
