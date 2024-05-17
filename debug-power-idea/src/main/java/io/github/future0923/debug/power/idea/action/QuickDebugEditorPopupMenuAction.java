package io.github.future0923.debug.power.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import io.github.future0923.debug.power.idea.context.ClassDataContext;
import io.github.future0923.debug.power.idea.context.DataContext;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.ui.main.MainDialog;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 右键菜单
 *
 * @author future0923
 */
public class QuickDebugEditorPopupMenuAction extends AnAction {

    private static final Logger log = Logger.getInstance(QuickDebugEditorPopupMenuAction.class);

    private final static Key<PsiMethod> USER_DATE_ELEMENT_KEY = new Key<>("user.psi.Element");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (null == project || editor == null) {
            throw new IllegalArgumentException("idea arg error (project or editor is null)");
        }

        try {
            PsiMethod psiMethod = null;
            if (e.getDataContext() instanceof UserDataHolder) {
                psiMethod = ((UserDataHolder) e.getDataContext()).getUserData(USER_DATE_ELEMENT_KEY);
            }
            if (psiMethod == null) {
                PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
                psiMethod = PsiTreeUtil.getParentOfType(getElement(editor, file), PsiMethod.class);
                if (psiMethod == null) {
                    throw new IllegalArgumentException("idea arg error (method is null)");
                }
            }

            DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
            if (settingState == null) {
                DebugPowerNotifierUtil.notifyError(project, "state not exists");
                return;
            }
            PsiClass psiClass = (PsiClass) psiMethod.getParent();

            ClassDataContext classDataContext = DataContext.instance(project).getClassDataContext(psiClass.getQualifiedName());
            MethodDataContext methodDataContext = new MethodDataContext(classDataContext, psiMethod, project);
            MainDialog dialog = new MainDialog(methodDataContext, project);
            dialog.show();
        } catch (Exception exception) {
            log.error("debug power invoke exception", exception);
            DebugPowerNotifierUtil.notifyError(project, "invoke exception " + exception.getMessage());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 当前项目
        Project project = e.getProject();
        // 当前编辑器
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        // 当前文件
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        // 获取光标所在方法
        PsiMethod method = PsiTreeUtil.getParentOfType(getElement(editor, file), PsiMethod.class);
        boolean enabled = project != null && editor != null && method != null;
        // 如果是启用状态，则将光标所在方法保存到数据上下文中
        if (enabled && e.getDataContext() instanceof UserDataHolder) {
            ((UserDataHolder) e.getDataContext()).putUserData(USER_DATE_ELEMENT_KEY, method);
        }
        // 启动禁用按钮
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Nullable
    public static PsiElement getElement(Editor editor, PsiFile file) {
        if (editor == null || file == null) {
            return null;
        }
        // 获取光标模型 CaretModel 对象。
        CaretModel caretModel = editor.getCaretModel();
        // 获取光标当前的偏移量（即光标在文件中的位置）
        int position = caretModel.getOffset();
        // 根据光标的位置在文件中查找对应的 PsiElement 对象
        return file.findElementAt(position);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        // EDT 调用线程。表示更新应该在事件调度线程（Event Dispatch Thread，也称为 UI 线程）中进行。这是默认值，适用于大多数情况下，特别是当更新涉及到 UI 元素时。
        // BGT 后台线程。表示更新应该在后台线程（Background Thread）中进行。适用于一些耗时操作，以避免阻塞 UI 线程。
        return ActionUpdateThread.BGT;
    }
}
