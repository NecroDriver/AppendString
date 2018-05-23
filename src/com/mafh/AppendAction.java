package com.mafh;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.util.registry.Registry;

/**
 * @author creator mafh 2018/5/23 14:39
 * @author updater mafh
 * @version 1.0.0
 * @description 将所选的每行内容首尾追加指定字符串，本质就是新内容覆盖旧内容。所以AppendAction直接继承EditorAction。参考CutAction
 */
public class AppendAction extends EditorAction {

    protected AppendAction() {
        super(new AppendAction.Handler());
    }

    public static class Handler extends EditorWriteActionHandler {
        public Handler() {}

        public void executeWriteAction(final Editor var1, DataContext var2) {
            if(!var1.getSelectionModel().hasSelection(true)) {
                if(Registry.is("editor.skip.copy.and.cut.for.empty.selection")) {
                    return;
                }

                //若没有选中内容，则默认选中光标停留的那一行
                var1.getCaretModel().runForEachCaret(new CaretAction() {
                    public void perform(Caret var1x) {
                        var1.getSelectionModel().selectLineAtCaret();
                    }
                });
            }

            SelectionModel sm = var1.getSelectionModel();
            String txt = sm.getSelectedText(); //获取选中的内容
            if (txt != null && !"".equals(txt.trim())) {
                //弹出输入框
                String aps = Messages.showInputDialog("输入字符串,例：buf.append(\"&\");", "追加字符串",
                        Messages.getInformationIcon(), "buf.append(\"&\");", new NonEmptyInputValidator());
                if (aps == null || "".equals(aps.trim())) return;
                String[] apps = aps.split("&");
                if (apps.length < 2) return;

                String[] txts = txt.split("\n");
                StringBuilder bui = new StringBuilder("");

                //拼接字符串
                for (String str:txts) {
                    String tstr = str.trim();
                    String line = null;
                    if ("".equals(tstr)) {
                        line = "\n";
                    } else {
                        int tind = str.indexOf(tstr);
                        String before = str.substring(0, tind);
                        line = before+apps[0]+tstr+apps[1]+"\n";
                    }
                    bui.append(line);
                }

                //覆盖插入
                EditorModificationUtil.insertStringAtCaret(var1, bui.toString(), true, false);
            }
        }
    }
}
