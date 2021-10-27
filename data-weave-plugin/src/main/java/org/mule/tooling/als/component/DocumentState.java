package org.mule.tooling.als.component;

import org.mulesoft.lsp.feature.diagnostic.Diagnostic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentState {
  private String url;
  private AtomicInteger version;
  private List<Diagnostic> messages;

  public DocumentState(String url) {
    this.url = url;
    this.version = new AtomicInteger(0);
    this.messages = new ArrayList<>();
  }

  public void clanDiagnostic() {
    messages.clear();
  }

  public void addDiagnostic(Diagnostic d) {
    messages.add(d);
  }

  public int version() {
    return version.get();
  }

  public int changed() {
    return version.incrementAndGet();
  }


  public Collection<? extends Diagnostic> diagnostics() {
    return Collections.unmodifiableList(messages);
  }
}
