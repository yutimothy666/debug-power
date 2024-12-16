package io.github.future0923.debug.tools.idea.api;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import io.github.future0923.debug.tools.idea.api.beans.ApiItem;
import io.github.future0923.debug.tools.idea.api.enums.HttpMethod;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class RequestFilteringGotoByModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {

    public RequestFilteringGotoByModel(@NotNull Project project, ChooseByNameContributor @NotNull [] contributors) {
        super(project, contributors);
    }

    @Override
    protected @Nullable HttpMethod filterValueFor(NavigationItem item) {
        if (item instanceof ApiItem apiItem) {
            return apiItem.getMethod();
        }
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getPromptText() {
        return "Enter api";
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotInMessage() {
        return IdeBundle.message("label.no.matches.found", getProject().getName());
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotFoundMessage() {
        return IdeBundle.message("label.no.matches.found");
    }

    @Override
    public @Nullable @NlsContexts.Label String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return true;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {

    }

    @Override
    public String @NotNull [] getSeparators() {
        return new String[]{"/", "?"};
    }

    @Override
    public @Nullable String getFullName(@NotNull Object element) {
        return getElementName(element);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        return true;
    }
}
