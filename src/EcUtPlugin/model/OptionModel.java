package EcUtPlugin.model;

import java.io.Serializable;

public class OptionModel implements Serializable {

    private static final long serialVersionUID = 32819_2L;
    private boolean autoFlag;
    private String notWatchKeywords;
    private String toStringKeywords;
    private String mapGetKeywords;
    private String methodChains;

    public static OptionModel init() {
        boolean autoFlag = false;
        String notWatchKeywords = "CommonModule.numberingProc\n";
        String toStringKeywords = "wflModule.getUpdateTimeStamp()\n"
                + "updateTimeStamp\n"
                + "params.get(\"upd_tim_stp\")\n";
        String mapGetKeywords = "params\nreturnValue\n";
        String methodChains = "equals\ncompareTo";
        return new OptionModel(autoFlag, notWatchKeywords, toStringKeywords, mapGetKeywords, methodChains);
    }

    public OptionModel(boolean autoFlag, String notWatchKeywords, String toStringKeywords, String mapGetKeywords, String methodChains) {
        this.autoFlag = autoFlag;
        this.notWatchKeywords = notWatchKeywords;
        this.toStringKeywords = toStringKeywords;
        this.mapGetKeywords = mapGetKeywords;
        this.methodChains = methodChains;
    }

    public String getMethodChains() {
        return methodChains;
    }

    public void setMethodChains(String methodChains) {
        this.methodChains = methodChains;
    }

    public String getMapGetKeywords() {
        return mapGetKeywords;
    }

    public void setMapGetKeywords(String mapGetKeywords) {
        this.mapGetKeywords = mapGetKeywords;
    }

    public boolean isAutoFlag() {
        return autoFlag;
    }

    public void setAutoFlag(boolean autoFlag) {
        this.autoFlag = autoFlag;
    }

    public String getNotWatchKeywords() {
        return notWatchKeywords;
    }

    public void setNotWatchKeywords(String notWatchKeywords) {
        this.notWatchKeywords = notWatchKeywords;
    }

    public String getToStringKeywords() {
        return toStringKeywords;
    }

    public void setToStringKeywords(String toStringKeywords) {
        this.toStringKeywords = toStringKeywords;
    }

}
