package EcUtPlugin;

import EcUtPlugin.common.CommonLookUp;
import EcUtPlugin.service.ExpressionService;
import EcUtPlugin.service.ScanService;
import EcUtPlugin.service.WatchService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

@ActionID(
        category = "Tools",
        id = "EcUtPlugin.AppendWatchExpressions"
)
@ActionRegistration(
        displayName = "#CTL_AppendWatchExpressions"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = -125),
    @ActionReference(path = "Shortcuts", name = "OS-G")
})
@Messages("CTL_AppendWatchExpressions=Append Watch Expressions")
public final class AppendWatchExpressions implements ActionListener {

    CommonLookUp commonLookup;

    @Override
    public void actionPerformed(ActionEvent e) {
        commonLookup = Lookup.getDefault().lookup(CommonLookUp.class);

        JTextComponent editorComponent = EditorRegistry.lastFocusedComponent();
        commonLookup.setQualifiedNameClass(ScanService.getFullQualifiedClassName(editorComponent));
        commonLookup.setFields(ScanService.getAllFields(commonLookup.getQualifiedNameClass()));
        commonLookup.setMethods(ScanService.getAllMethods(commonLookup.getQualifiedNameClass()));
        handleGetAllExpression(editorComponent);
    }

    private void handleGetAllExpression(JTextComponent editorComponent) {

        String selectedText = ExpressionService.getSelectedText(editorComponent);
        if (selectedText == null) {
            return;
        }
        selectedText = selectedText.lines().map(line -> {
            if (line.contains("//")) {
                int idx = line.indexOf("//");
                return line.substring(0, idx);
            }
            return line;
        }).map(String::trim).reduce("", (a, b) -> a + b);
        List<String> exps = ExpressionService.getAllExpressionsFromStatement(selectedText, commonLookup.getFields(), commonLookup.getMethods());
        if (exps == null) {
            return;
        }

        if (NbPreferences.forModule(ECUTOptionPanel.class).getBoolean("autoFlag", false)) {
            WatchService.appendValueToWatchesWindow(exps.stream().collect(Collectors.joining("\n")));
        }
        commonLookup.getWatchTextArea().setText(Stream.concat(commonLookup.getWatchTextArea().getText().lines(), exps.stream()).collect(Collectors.joining("\n")));
    }
}
