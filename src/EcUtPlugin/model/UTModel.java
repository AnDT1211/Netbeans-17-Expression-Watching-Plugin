/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ENERCOM36
 */
public class UTModel implements Serializable {

    private static final long serialVersionUID = 32819L;
    private String utName;
    private Map<String, List<String>> uts = new TreeMap<>();

    public UTModel() {
    }

    public UTModel(String utName, Map<String, List<String>> uts) {
        this.utName = utName;
        this.uts = uts;
    }

    public String getUtName() {
        return utName;
    }

    public void setUtName(String utName) {
        this.utName = utName;
    }

    public Map<String, List<String>> getUts() {
        return uts;
    }

    public void setUts(Map<String, List<String>> uts) {
        this.uts = uts;
    }

    @Override
    public String toString() {
        return "UTModel{" + "utName=" + utName + ", uts=" + uts + '}';
    }

    /**
     * init the model
     */
    public static UTModel initModel() {
        return new UTModel() {
            {
                setUtName("ROOT");
                setUts(new TreeMap<>() {
                    {
                        put("UT001", new ArrayList<String>(Arrays.asList("")));
                    }
                });
            }
        };
    }

    public static String getExpressions(UTModel model, String utName, String stepName) {
        Map<String, List<String>> mapUts = model.getUts();
        List<String> uts = mapUts.get(utName);
        String exps = uts.get(Integer.parseInt(new StringBuilder(stepName).delete(0, 1).toString()) - 1);
        return exps;
    }

}
