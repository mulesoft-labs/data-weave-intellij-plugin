package org.mule.tooling.runtime.util;

import org.mule.runtime.api.deployment.meta.AbstractMuleArtifactModel;
import org.mule.runtime.api.deployment.meta.MuleApplicationModel;
import org.mule.runtime.api.deployment.meta.MuleDomainModel;
import org.mule.runtime.api.deployment.meta.MulePluginModel;
import org.mule.runtime.api.deployment.meta.MuleServiceModel;

public enum ProjectType implements MuleProjectType {

  MULE_APP {
    @Override
    public Class<? extends AbstractMuleArtifactModel> getArtifactJsonDescriptor() {
      return MuleApplicationModel.class;
    }

    @Override
    public String getPackaging() {
      return "mule-application";
    }
  }, MULE_PLUGIN {
    @Override
    public Class<? extends AbstractMuleArtifactModel> getArtifactJsonDescriptor() {
      return MulePluginModel.class;
    }

    @Override
    public String getPackaging() {
      return "mule-plugin";
    }
  }, MULE_SERVICE {
    @Override
    public Class<? extends AbstractMuleArtifactModel> getArtifactJsonDescriptor() {
      return MuleServiceModel.class;
    }

    @Override
    public String getPackaging() {
      return "mule-service";
    }
  }, MULE_DOMAIN {
    @Override
    public Class<? extends AbstractMuleArtifactModel> getArtifactJsonDescriptor() {
      return MuleDomainModel.class;
    }

    @Override
    public String getPackaging() {
      return "mule-domain";
    }
  };


}
