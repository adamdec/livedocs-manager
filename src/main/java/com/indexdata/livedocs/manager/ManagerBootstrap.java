package com.indexdata.livedocs.manager;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexdata.livedocs.manager.core.DisplayInitializer;
import com.indexdata.livedocs.manager.core.DisplayThread;
import com.indexdata.livedocs.manager.core.SpringInitializer;
import com.indexdata.livedocs.manager.core.utils.KeyChecker;
import com.indexdata.livedocs.manager.core.utils.KeyProvider;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.login.LoginWindowEX;

/**
 * Main application entry point.
 *
 * @author Adam Dec
 * @since 0.0.1
 */
public class ManagerBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerBootstrap.class);

    private final DisplayInitializer displayInitializer = new DisplayInitializer();
    private final SpringInitializer springInitializer = new SpringInitializer();

    public static void main(String[] args) throws InterruptedException {
        KeyChecker.check(KeyProvider.getClientKeys());

        final ManagerBootstrap managerBootstrap = new ManagerBootstrap();
        managerBootstrap.getDisplayInitializer().awaitFor();

        DisplayThread.syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    new LoginWindowEX(managerBootstrap.getSpringInitializer(), managerBootstrap.getDisplayInitializer())
                            .open();
                } catch (final RuntimeException e) {
                    LOGGER.error("Could not launch main application window", e);
                    UIControlUtils.createErrorMessageBoxWithTitle(new Shell(),
                            "Could not launch main application window=" + e.getMessage());
                    System.exit(-1);
                }
            }
        });
    }

    public DisplayInitializer getDisplayInitializer() {
        return displayInitializer;
    }

    public SpringInitializer getSpringInitializer() {
        return springInitializer;
    }
}
