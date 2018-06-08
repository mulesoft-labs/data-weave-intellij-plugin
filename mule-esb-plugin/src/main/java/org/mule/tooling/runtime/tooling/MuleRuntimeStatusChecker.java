package org.mule.tooling.runtime.tooling;

import com.intellij.openapi.progress.ProgressIndicator;
import org.awaitility.Duration;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.runtime.process.controller.MuleProcessController;

import javax.net.ssl.SSLHandshakeException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.with;

public class MuleRuntimeStatusChecker {

    public static final String MULE_TOOLING_RESOURCE = "/mule/tooling";
    private static final String HTTPS_PROTOCOL = "https";


    private MuleProcessController muleProcessController;
    private MuleAgentConfiguration configuration;

    public MuleRuntimeStatusChecker(MuleProcessController muleProcessController, MuleAgentConfiguration configuration) {
        this.muleProcessController = muleProcessController;
        this.configuration = configuration;
    }

    public int getPort() {
        return configuration.getAgentPort();
    }

    public boolean isRunning() {
        return muleProcessController.isRunning() && checkIsAlive();
    }

    boolean checkIsAlive() {
        final URL toolingApiUrl = getToolingApiUrl();
        try {

            final URLConnection urlConnection = toolingApiUrl.openConnection();
            urlConnection.setConnectTimeout(200);
            urlConnection.setReadTimeout(200);
            urlConnection.connect();
            return true;
        } catch (Exception e) {
            return toolingApiUrl.getProtocol().equals(HTTPS_PROTOCOL) && (e instanceof SSLHandshakeException);
        }
    }

    @NotNull
    public URL getToolingApiUrl() {
        return getToolingApiUrl(configuration.getProtocol(), configuration.getAgentPort());
    }


    public void waitUntilIsRunning(ProgressIndicator progressIndicator) {
        progressIndicator.setText2("Waiting for Tooling API URL to be operational...");
        with()
                .timeout(configuration.getStartTimeout() == 0 ? Duration.FOREVER
                        : new Duration(configuration.getStartTimeout(), MILLISECONDS))
                .and().with().pollInterval(configuration.getStartPollInterval(), MILLISECONDS).and().with()
                .pollDelay(configuration.getStartPollDelay(), MILLISECONDS).await("Waiting for Remote Tooling Service to be operational")
                .until(() -> isRunning());
        progressIndicator.setText2("Mule Agent connected");
    }


    private static URL getToolingApiUrl(String protocol, int restAgentPort) {
        InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
        try {
            return new URL(protocol + "://" + loopbackAddress.getHostAddress() + ":" + restAgentPort + MULE_TOOLING_RESOURCE);
        } catch (MalformedURLException e) {
            //This should never happen
            throw new RuntimeException(e);
        }
    }

    public int getAgentPort() {
        return configuration.getAgentPort();
    }
}
