package org.mule.tooling.runtime.util;

import com.intellij.openapi.application.Result;

public class ResultHolder<T> extends Result<T> {

  public T getResult() {
    return myResult;
  }
}
