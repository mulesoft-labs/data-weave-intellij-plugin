package org.mule.tooling.runtime.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.sdk.MuleSdk;
import org.mule.tooling.runtime.sdk.MuleSdkManager;
import org.mule.tooling.runtime.sdk.MuleSdkManagerStore;
import org.mule.tooling.runtime.sdk.ui.MuleSdkComboSelection;

import javax.swing.*;

public class MuleVersionConfiguration extends ModuleWizardStep implements Disposable
{

    private JPanel mainPanel;
    private MuleSdkComboSelection muleSdkCombo;
    private AbstractMuleModuleBuilder moduleBuilder;

    public MuleVersionConfiguration(AbstractMuleModuleBuilder moduleBuilder, String muleVersion)
    {
        this.moduleBuilder = moduleBuilder;
        //muleSdkCombo.setSelectedSdk(MuleSdkManager.getInstance().getSdkByVersion(muleVersion));
        MuleSdk sdk = MuleSdkManagerStore.getInstance().findFromVersion(muleVersion);
        if (sdk != null)
            muleSdkCombo.setSelectedSdk(sdk);
    }

    @Override
    public JComponent getComponent()
    {
        return mainPanel;
    }

    @Override
    public void updateDataModel()
    {
        moduleBuilder.setMuleVersion(getMuleVersion());
    }

    @Override
    public boolean validate() throws ConfigurationException
    {
        return super.validate() && StringUtil.isNotEmpty(getMuleVersion());
    }

    @Nullable
    public String getMuleVersion()
    {
        final MuleSdk selectedSdk = muleSdkCombo.getSelectedSdk();
        if (selectedSdk != null)
        {
            return selectedSdk.getVersion();
        }
        else
        {
            return null;
        }
    }


    @Override
    public void dispose()
    {

    }
}
