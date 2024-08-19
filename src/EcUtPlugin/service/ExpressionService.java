/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EcUtPlugin.service;

import EcUtPlugin.ECUTOptionPanel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.swing.text.JTextComponent;
import org.openide.util.NbPreferences;

/**
 *
 * @author ENERCOM36
 */
public class ExpressionService {

    private static List<String> listExps = new ArrayList<>();

    public static List<String> getAllExpressionsFromStatement(String statements, Field[] fields, Method[] methods) {

        List<String> mapGetKeywords = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("mapGetKeywords", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();
        List<String> notWatchKeywords = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("notWatchKeywords", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();
        List<String> toStringKeywords = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("toStringKeywords", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();
        List<String> methodChains = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("methodChains", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();

        List<String> exps = new ArrayList<>();
        List<String> listStatements = getAllStatement(statements);

        for (String exp : listStatements) {
            listExps = new LinkedList<>();

            // set method reflection
            String caller = exp.substring(0, exp.indexOf("("));
            getterMethod(caller, fields, methods, listExps);
            // get all expressions
            getAllExpressions(exp);

            /*
            map.put
             */
            String mapGet = mapGetKeywords.stream().filter(x -> exp.startsWith(x + ".put(")).findFirst().orElse(null);
            if (mapGet != null) {
                ((LinkedList) listExps).addFirst(mapGet + ".get(" + exp.substring(exp.indexOf("(") + 1, exp.indexOf(",")) + ")");
            }

            /*
            extract "="
             */
            if (exp.contains(" = ")) {
                String watch = exp.substring(exp.indexOf("=") + 1, exp.length());
                ((LinkedList) listExps).addFirst(watch.trim());
            }

            /*
            equals() - method chains
             */
            List<Boolean> outerBrace = new ArrayList<>();
            int leftBrace = 0;
            for (int i = 0; i < exp.length(); i++) {
                char ch = exp.charAt(i);
                if (ch == '(') {
                    leftBrace++;
                } else if (ch == ')') {
                    leftBrace--;
                }

                outerBrace.add(leftBrace == 0);
            }

            for (String mt : methodChains) {
                if (exp.contains(mt + "(")) {
                    int idxEquals = exp.indexOf(mt + "(");
                    if (outerBrace.get(idxEquals)) {
                        String befEqual = exp.substring(0, idxEquals - 1);
                        if (befEqual.contains("=")) {
                            befEqual = befEqual.substring(befEqual.indexOf("=") + 1, idxEquals - 1);
                        }
                        ((LinkedList) listExps).addFirst(befEqual.trim());
                    }
                }
            }

            // add All
            exps.addAll(listExps);
        }

        /**
         * filtering
         */
        /*
        notWatchKeywords
        toStringKeywords
         */
        return exps.stream()
                .filter(Predicate.not(String::isBlank))
                .map(String::trim)
                .distinct()
                .filter(ExpressionService::isNotStringLiteral)
                .filter(ExpressionService::isNotNullLiteral)
                .filter(ExpressionService::isNotNumberLiteral)
                .filter(ExpressionService::isNotBooleanLiteral)
                .distinct()
                .filter(exp -> !exp.startsWith("new "))
                .filter(exp -> !notWatchKeywords.stream().anyMatch(x -> exp.startsWith(x)))
                // remove casting
                .map(x -> {
                    if (x.startsWith("(")) {
                        int leftBrace = 0;
                        for (int i = 0; i < x.length(); i++) {
                            char ch = x.charAt(i);
                            if (ch == '(') {
                                leftBrace++;
                                if (leftBrace > 1) {
                                    break;
                                }
                                continue;
                            } else if (ch == ')') {
                                leftBrace--;
                                continue;
                            }

                            if (leftBrace == 0 && ch == ' ') {
                                return x.substring(i + 1, x.length());
                            }
                        }
                    }
                    return x;
                })
                .map(exp -> {
                    if (toStringKeywords.stream().anyMatch(x -> x.contains(exp)) && !exp.endsWith(".toString()")) {
                        return exp + ".toString()";
                    }
                    return exp;
                })
                .toList();
    }

    private static void getterMethod(String caller, Field[] fields, Method[] methods, List<String> expWatches) {
        // check reflection model
        if (fields != null) {
            String modelGet = null;
            if (caller.contains(".")) {
                String[] callerArr = caller.split("\\.");
                Field field = Stream.of(fields).filter(x -> x.getName().equals(callerArr[0])).findFirst().orElse(null);

                if (field != null) {
                    if (callerArr[1].startsWith("set")) {
                        String fieldName = callerArr[1].substring(3, callerArr[1].length());
                        String funct = Stream.of(field.getType().getDeclaredMethods()).filter(x -> x.getName().startsWith("get") || x.getName().startsWith("is"))
                                .filter(x -> x.getName().contains(fieldName))
                                .map(x -> x.getName())
                                .filter(x -> x.endsWith(fieldName))
                                .findFirst().orElse(null);
                        if (funct != null) {
                            modelGet = callerArr[0] + "." + funct + "()";
                        }
                    }
                }
            } else {
                if (caller.startsWith("set")) {
                    String getMethod = "get" + caller.substring(3, caller.length());
                    String isMethod = "is" + caller.substring(3, caller.length());

                    String funct = Stream.of(methods).filter(x -> x.getName().equals(getMethod) || x.getName().equals(isMethod)).map(x -> x.getName()).findFirst().orElse(null);
                    if (funct != null) {
                        modelGet = funct + "()";
                    }
                }
            }
            if (modelGet != null) {
                expWatches.add(modelGet);
            }
        }
    }

    static void getAllExpressions(String exp) {
        List<String> methodChains = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("methodChains", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();
        int leftBrace = 0;
        int idxFrom = 0;
        int idxTo = 0;

        for (int i = 0; i < exp.length(); i++) {
            char ch = exp.charAt(i);
            if (ch == '(' && leftBrace == 0) {
                idxFrom = i + 1;
            }

            if (ch == '(') {
                leftBrace++;
            } else if (ch == ')') {
                leftBrace--;
            }

            if (ch == ')' && leftBrace == 0) {
                idxTo = i;
            }
        }

        /*
        exp = a(b(c(aa, bb(aaa, bbb)), cc(ee, ff), dd), xx(a(2, 3), b), x(1));
        expInsideBraces = b(c(aa, bb(aaa, bbb)), cc(ee, ff), dd), xx(a(2, 3), b), x(1)
         */
        String expInsideBraces = exp.substring(idxFrom, idxTo);

        leftBrace = 0;
        idxFrom = 0;
        idxTo = 0;

        for (int i = 0; i < expInsideBraces.length(); i++) {
            char ch = expInsideBraces.charAt(i);
            if (ch == '(') {
                leftBrace++;
            } else if (ch == ')') {
                leftBrace--;
            } else if (ch == ',' && leftBrace == 0) {
                idxTo = i;
                String ex = expInsideBraces.substring(idxFrom, idxTo);
                idxFrom = i + 1;
                if (ex.contains("(")) {
                    getAllExpressions(ex);
                }

                for (String mt : methodChains) {
                    if (ex.contains(mt + "(") && leftBrace == 0) {
                        int idxEquals = ex.indexOf(mt + "(");
                        String befEqual = ex.substring(0, idxEquals - 1);
                        if (befEqual.contains("=")) {
                            befEqual = befEqual.substring(befEqual.indexOf("=") + 1, idxEquals - 1);
                        }
                        listExps.add(befEqual.trim());
                    }
                }
                listExps.add(ex);
            } // ? :
            else if (ch == '?' && leftBrace == 0) {
                int idxChamHoi = i;
                int idxHaiCham = expInsideBraces.indexOf(":");

                listExps.add(expInsideBraces.substring(0, idxChamHoi));
                listExps.add(expInsideBraces.substring(idxChamHoi + 1, idxHaiCham));
                listExps.add(expInsideBraces.substring(idxHaiCham + 1, expInsideBraces.length()));
            }
        }
        String ex = expInsideBraces.substring(idxFrom, expInsideBraces.length());
        if (ex.contains("(")) {
            getAllExpressions(ex);
        }

        // ? :
        for (int i = 0; i < exp.length(); i++) {
            char ch = exp.charAt(i);
            if (ch == '(') {
                leftBrace++;
            } else if (ch == ')') {
                leftBrace--;
            }
            if (ch == '?' && leftBrace == 0) {
                int idxChamHoi = i;
                int idxHaiCham = exp.indexOf(":");

                int idxBegin = 0;
                int idxEqual = exp.indexOf("=");
                if (idxEqual != -1 && idxEqual < idxChamHoi) {
                    idxBegin = idxEqual + 1;
                }
                listExps.add(exp.substring(idxBegin, idxChamHoi));
                listExps.add(exp.substring(idxChamHoi + 1, idxHaiCham));
                String lastExp = exp.substring(idxHaiCham + 1, exp.length()).trim();
                if (lastExp.endsWith(";")) {
                    lastExp = lastExp.substring(0, lastExp.length() - 1);
                }
                listExps.add(lastExp);
            }
        }

        // method chains
        for (String mt : methodChains) {
            if (ex.contains(mt + "(") && leftBrace == 0) {
                int idxEquals = ex.indexOf(mt + "(");
                String befEqual = ex.substring(0, idxEquals - 1);
                if (befEqual.contains("=")) {
                    befEqual = befEqual.substring(befEqual.indexOf("=") + 1, idxEquals - 1);
                }
                listExps.add(befEqual.trim());
            }
        }
        listExps.add(ex);
    }

    static List<String> getAllExpressionsFromStatement2(String statements, Field[] fields, Method[] methods) {
        List<String> listStatements = getAllStatement(statements);

        List<String> expWatches = new ArrayList<>();
        for (String exp : listStatements) {

            String caller = "";

            LinkedList<String> queue = new LinkedList<>();
            int idxFrom = 0;
            int idxTo = 0;
            for (int i = 0; i < exp.length(); i++) {
                if (exp.charAt(i) == '(') {
                    if (queue.isEmpty()) {
                        idxFrom = i + 1;
                        caller = exp.substring(0, i);
                    }
                    queue.addFirst("(");
                } else if (exp.charAt(i) == ')') {
                    if (queue.size() == 1) {
                        idxTo = i;
                    }
                    queue.pollFirst();
                }
            }

            {
                // check reflection model
                if (fields != null) {
                    String modelGet = null;
                    if (caller.contains(".")) {
                        String[] callerArr = caller.split("\\.");
                        Field field = Stream.of(fields).filter(x -> x.getName().equals(callerArr[0])).findFirst().orElse(null);

                        if (field != null) {
                            if (callerArr[1].startsWith("set")) {
                                String fieldName = callerArr[1].substring(3, callerArr[1].length());
                                String funct = Stream.of(field.getType().getDeclaredMethods()).filter(x -> x.getName().startsWith("get") || x.getName().startsWith("is"))
                                        .filter(x -> x.getName().contains(fieldName))
                                        .map(x -> x.getName()).findFirst().orElse(null);

                                if (funct != null) {
                                    modelGet = callerArr[0] + "." + funct + "()";
                                }
                            }
                        }
                    } else {
                        if (caller.startsWith("set")) {
                            String getMethod = "get" + caller.substring(3, caller.length());
                            String isMethod = "is" + caller.substring(3, caller.length());

                            String funct = Stream.of(methods).filter(x -> x.getName().equals(getMethod) || x.getName().equals(isMethod)).map(x -> x.getName()).findFirst().orElse(null);
                            if (funct != null) {
                                modelGet = funct + "()";
                            }
                        }
                    }
                    if (modelGet != null) {
                        expWatches.add(modelGet);
                    }
                }
            }

            LinkedList<String> queue2 = new LinkedList<>();
            String expsInside = exp.substring(idxFrom, idxTo);

            idxFrom = 0;
            for (int i = 0; i < expsInside.length(); i++) {
                if (expsInside.charAt(i) == ',' && queue2.isEmpty()) {
                    expWatches.add(expsInside.substring(idxFrom, i));
                    idxFrom = i + 1;
                } else if (expsInside.charAt(i) == '(') {
                    queue2.addFirst("(");
                } else if (expsInside.charAt(i) == ')') {
                    queue2.removeLast();
                }
            }
            expWatches.add(expsInside.substring(idxFrom, expsInside.length()));
            // map.put
            if (exp.contains("params.put(")) {
                expWatches.add("params.get(" + exp.substring(exp.indexOf("(") + 1, exp.indexOf(",")) + ")");
            }
        }

        for (String exp : listStatements) {

            String caller = "";

            LinkedList<String> queue = new LinkedList<>();
            int idxFrom = 0;
            int idxTo = 0;
            for (int i = 0; i < exp.length(); i++) {
                if (exp.charAt(i) == '(') {
                    if (queue.size() == 1) {
                        idxFrom = i + 1;
                        caller = exp.substring(0, i);
                    }
                    queue.addFirst("(");
                } else if (exp.charAt(i) == ')') {
                    if (queue.size() == 2) {
                        idxTo = i;
                    }
                    queue.pollFirst();
                }
            }

            {
                // check reflection model
                if (fields != null) {
                    String modelGet = null;
                    if (caller.contains(".")) {
                        String[] callerArr = caller.split("\\.");
                        Field field = Stream.of(fields).filter(x -> x.getName().equals(callerArr[0])).findFirst().orElse(null);

                        if (field != null) {
                            if (callerArr[1].startsWith("set")) {
                                String fieldName = callerArr[1].substring(3, callerArr[1].length());
                                String funct = Stream.of(field.getType().getDeclaredMethods()).filter(x -> x.getName().startsWith("get") || x.getName().startsWith("is"))
                                        .filter(x -> x.getName().contains(fieldName))
                                        .map(x -> x.getName()).findFirst().orElse(null);

                                if (funct != null) {
                                    modelGet = callerArr[0] + "." + funct + "()";
                                }
                            }
                        }
                    } else {
                        if (caller.startsWith("set")) {
                            String getMethod = "get" + caller.substring(3, caller.length());
                            String isMethod = "is" + caller.substring(3, caller.length());

                            String funct = Stream.of(methods).filter(x -> x.getName().equals(getMethod) || x.getName().equals(isMethod)).map(x -> x.getName()).findFirst().orElse(null);
                            if (funct != null) {
                                modelGet = funct + "()";
                            }
                        }
                    }
                    if (modelGet != null) {
                        expWatches.add(modelGet);
                    }
                }
            }

            LinkedList<String> queue2 = new LinkedList<>();
            String expsInside = exp.substring(idxFrom, idxTo);

            idxFrom = 0;
            for (int i = 0; i < expsInside.length(); i++) {
                if (expsInside.charAt(i) == ',' && queue2.size() == 1) {
                    expWatches.add(expsInside.substring(idxFrom, i));
                    idxFrom = i + 1;
                } else if (expsInside.charAt(i) == '(') {
                    queue2.addFirst("(");
                } else if (expsInside.charAt(i) == ')') {
                    queue2.removeLast();
                }
            }

            expWatches.add(expsInside.substring(idxFrom, expsInside.length()));
        }

        /*
        notWatchKeywords
        toStringKeywords
         */
        List<String> notWatchKeywords = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("notWatchKeywords", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();
        List<String> toStringKeywords = Arrays.stream(NbPreferences.forModule(ECUTOptionPanel.class).get("toStringKeywords", "").split("\n"))
                .map(String::trim).filter(Predicate.not(String::isBlank)).toList();

        return expWatches
                .stream()
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .filter(ExpressionService::isNotLIterals)
                .filter(Predicate.not(notWatchKeywords::contains))
                .filter(exp -> {
                    return !notWatchKeywords.stream().anyMatch(x -> exp.startsWith(x));
                })
                .map(String::trim)
                .map(exp -> {
                    if (toStringKeywords.stream().anyMatch(x -> x.contains(exp)) && !exp.endsWith(".toString()")) {
                        return exp + ".toString()";
                    }
                    return exp;
                })
                .map(String::trim)
                .map(ExpressionService::removeCasting)
                .map(String::trim)
                .distinct().toList();
    }

    private static String removeCasting(String exp) {
        if (exp.startsWith("(") && !exp.contains("->")) {
            return exp.substring(exp.indexOf(")") + 2, exp.length());
        }
        return exp;
    }

    private static boolean isNotNumberLiteral(String exp) {
        try {
            int num = Integer.parseInt(exp);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    private static boolean isNotNullLiteral(String exp) {
        return !exp.equals("null");
    }

    private static boolean isNotBooleanLiteral(String exp) {
        return !exp.equals("true") && !exp.equals("false");
    }

    private static int countCharInString(String src, char ch) {
        return (int) src.chars().filter(x -> x == ch).count();
    }

    private static boolean isNotStringLiteral(String exp) {
        if (exp.startsWith("\"")) {
            if (countCharInString(exp, '\"') == 2) {
                if (!exp.contains(" + ")) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isNotLIterals(String exp) {
        return !("1234567890".chars().anyMatch(x -> exp.length() != 0 && x == exp.charAt(0))
                || exp.startsWith("null")
                || (exp.startsWith("\"") && exp.endsWith("\""))
                || exp.startsWith("true")
                || exp.startsWith("false")
                || (exp.startsWith("'") && exp.endsWith("'")));
    }

    private static List<String> getAllStatement(String statements) {
        return Stream.of(statements.split(";"))
                .map(x -> x.replace("\n", "")
                //                        .replace(" ", "")
                .trim())
                .filter(x -> !x.isBlank()).toList();
    }

    public static String getSelectedText(JTextComponent editorComponent) {
        try {
            int idxStartSelectedTxt = editorComponent.getSelectionStart();
            int idxEndSelectedTxt = editorComponent.getSelectionEnd();

            String wholeText = editorComponent.getText();

            String[] linesWholeText = wholeText.split("\r\n");
            int idxStart = 0;
            int idxEnd = 0;
            String textSelected = "";
            boolean hasStart = false;

            for (String lineWholeText : linesWholeText) {
                idxEnd += lineWholeText.length() + 1;
                if (!hasStart && idxStartSelectedTxt >= idxStart && idxStartSelectedTxt <= idxEnd) {
                    idxStartSelectedTxt = idxStart;
                    hasStart = true;
                }

                if (hasStart) {
                    textSelected += lineWholeText;
                }
                idxStart += lineWholeText.length() + 1;

                if (idxEndSelectedTxt < idxEnd) {
                    break;
                }

                if (hasStart) {
                    textSelected += "\r\n";
                }
            }

//            System.out.println(textSelected);
            return textSelected;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
