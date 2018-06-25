package org.mule.tooling.runtime.util;

import org.mule.runtime.api.deployment.meta.AbstractMuleArtifactModel;

interface MuleProjectType {
  Class<? extends AbstractMuleArtifactModel> getArtifactJsonDescriptor();

  String getPackaging();
}