package org.mule.tooling.lang.dw.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.psi.PsiFile;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "DataWeave", storages = @Storage(file = "Anypoint/DataWeave.xml"))
public class DataWeaveSettingsState implements PersistentStateComponent<DataWeaveSettingsState> {

    public static final String DOT_PATH = "/usr/local/bin/dot";
    public static final int MAX_AMOUNT_OF_CHARACTERS = 40000;

    private String cmdPath = DOT_PATH;

    private Boolean showParametersName = true;
    private Boolean showTypeInference = true;
    private int maxAmountOfCharsForSemanticAnalysis = MAX_AMOUNT_OF_CHARACTERS;

    public DataWeaveSettingsState() {
    }

    public static DataWeaveSettingsState getInstance() {
        return ServiceManager.getService(DataWeaveSettingsState.class);
    }

    public String getCmdPath() {
        return cmdPath;
    }

    public void setCmdPath(String cmdPath) {
        this.cmdPath = cmdPath;
    }

    public Boolean getShowParametersName() {
        return showParametersName;
    }

    public void setShowParametersName(Boolean showParametersName) {
        this.showParametersName = showParametersName;
    }

    public Boolean getShowTypeInference() {
        return showTypeInference;
    }

    public int getMaxAmountOfCharsForSemanticAnalysis() {
        return maxAmountOfCharsForSemanticAnalysis;
    }

    public void setMaxAmountOfCharsForSemanticAnalysis(int maxAmountOfCharsForSemanticAnalysis) {
        this.maxAmountOfCharsForSemanticAnalysis = maxAmountOfCharsForSemanticAnalysis;
    }

    public boolean isBigFileForSemanticAnalysis(PsiFile file){

        return file.getText().length() > getMaxAmountOfCharsForSemanticAnalysis();
    }

    public void setShowTypeInference(Boolean showTypeInference) {
        this.showTypeInference = showTypeInference;
    }

    @Nullable
    @Override
    public DataWeaveSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DataWeaveSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }


}
