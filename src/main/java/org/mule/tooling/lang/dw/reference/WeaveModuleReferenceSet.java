package org.mule.tooling.lang.dw.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveModuleReference;

import java.util.List;


public class WeaveModuleReferenceSet extends FileReferenceSet {

  public WeaveModuleReferenceSet(@NotNull WeaveModuleReference moduleReference) {
    super(moduleReference.getModuleFQN(), moduleReference, 0, null, true);
  }

  protected List<FileReference> reparse(String path, int startInElement) {
    int wsHead = 0;
    int wsTail = 0;
    TextRange valueRange = TextRange.from(startInElement, path.length());
    List<FileReference> referencesList = ContainerUtil.newArrayList();
    for (int i = wsHead; i < path.length() && Character.isWhitespace(path.charAt(i)); i++) {
      wsHead++;     // skip head white spaces
    }
    for (int i = path.length() - 1; i >= 0 && Character.isWhitespace(path.charAt(i)); i--) {
      wsTail++;     // skip tail white spaces
    }

    int index = 0;
    int curSep = findSeparatorOffset(path, wsHead);
    int sepLen = curSep >= wsHead ? findSeparatorLength(path, curSep) : 0;

    if (curSep >= 0 && path.length() == wsHead + sepLen + wsTail) {
      // add extra reference for the only & leading "/"
      TextRange r = TextRange.create(startInElement, offset(curSep + Math.max(0, sepLen - 1), valueRange) + 1);
      referencesList.add(createFileReference(r, index++, path.subSequence(curSep, curSep + sepLen).toString()));
    }
    curSep = curSep == wsHead ? curSep + sepLen : wsHead; // reset offsets & start again for simplicity
    sepLen = 0;
    while (curSep >= 0) {
      int nextSep = findSeparatorOffset(path, curSep + sepLen);
      int start = curSep + sepLen;
      int endTrimmed = nextSep > 0 ? nextSep : Math.max(start, path.length() - wsTail);
      int endInclusive = nextSep > 0 ? nextSep : Math.max(start, path.length() - 1 - wsTail);
      String refText;
      if (index == 0 && nextSep < 0 && !StringUtil.contains(path, path)) {
        refText = path;
      } else {
        refText = path.subSequence(start, endTrimmed).toString();
      }
      TextRange textRange = new TextRange(offset(start, valueRange), offset(endInclusive, valueRange) + (nextSep < 0 && refText.length() > 0 ? 1 : 0));
      if (nextSep <= 0) {
        refText = refText + "." + WeaveFileType.WeaveFileExtension;
      }
      referencesList.add(createFileReference(textRange, index++, refText));
      curSep = nextSep;
      sepLen = curSep > 0 ? findSeparatorLength(path, curSep) : 0;
    }

    return referencesList;
  }

  @Override
  public String getSeparatorString() {
    return "::";
  }

  private static int offset(int offset, TextRange valueRange) {
    return offset + valueRange.getStartOffset();
  }

  @Override
  public boolean absoluteUrlNeedsStartSlash() {
    return false;
  }

}
