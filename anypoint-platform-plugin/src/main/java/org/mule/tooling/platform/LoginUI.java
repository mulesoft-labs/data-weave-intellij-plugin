package org.mule.tooling.platform;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.DefaultLoadHandler;
import com.teamdev.jxbrowser.chromium.LoadParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

public class LoginUI extends DialogWrapper {

    private int width = 400;
    private int height = 600;

    private PlatformUser loggedUser = null;

    protected LoginUI() {
        super(false);
        setTitle("Anypoint Platform Login");
        setOKActionEnabled(false);
        setAutoAdjustable(true);
        setResizable(false);
        init();
    }


    @NotNull
    @Override
    protected DialogStyle getStyle() {
        return DialogStyle.COMPACT;
    }

    @Override
    protected JComponent createSouthPanel() {
        return new JPanel();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        final JPanel result = new JPanel(new BorderLayout());
        final Browser browser = new Browser();
        browser.getCookieStorage().deleteAll();
        final BrowserView view = new BrowserView(browser);
        browser.loadURL(loginUrl());
        browser.setLoadHandler(new DefaultLoadHandler() {
            @Override
            public boolean onLoad(LoadParams params) {
                if (params.getURL().startsWith(PlatformUrls.getRedirectCsUrl())) {
                    try {
                        final List<NameValuePair> parse = URLEncodedUtils.parse(new URL(params.getURL()).getQuery(), Charset.forName("UTF-8"));
                        final Optional<PlatformUser> user = parse.stream()
                                .filter(nameValuePair -> nameValuePair.getName().equals("code"))
                                .findFirst()
                                .map(nameValuePair -> new PlatformUser(nameValuePair.getValue()));
                        loggedUser = user.orElseGet(null);
                        browser.stop();
                    } catch (Exception e) {
                        //Ignore it should
                    }
                    SwingUtilities.invokeLater(() -> LoginUI.this.close(DialogWrapper.OK_EXIT_CODE, true));
                    browser.dispose();
                    return true;
                }
                return super.onLoad(params);
            }
        });
        result.setPreferredSize(JBUI.size(this.width, this.height));
        result.add(view, BorderLayout.CENTER);
        return result;
    }

    @Nullable
    public PlatformUser getLoggedUser() {
        return loggedUser;
    }

    private String loginUrl() {
        String activePlatformUrl = PlatformUrls.getActivePlatformUrl();
        String businessOrgDomain = businessOrgDomain();
        String studioClientId = PlatformUrls.getStudioClientId();
        String fileUrl = activePlatformUrl + "accounts/oauth2/authorize" + businessOrgDomain + "?response_type=code&redirect_uri=" + PlatformUrls.getRedirectCsUrl() + "&client_id="
                + studioClientId + "&state=";
        return fileUrl;
    }

    private String businessOrgDomain() {
        String businessOrgDomain = "";
//        IPreferenceStore preferenceStore = AuthenticationActivator.getDefault().getPreferenceStore();
//        if (preferenceStore.getBoolean(PlatformUrlPreferenceInitializer.USES_EXTERNAL_IDENTITY)) {
//            return "/" + preferenceStore.getString(PlatformUrlPreferenceInitializer.EXTERNAL_IDENTITY_BUSINESS_ORG);
//        }
        return businessOrgDomain;
    }
}
