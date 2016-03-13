package com.indexdata.livedocs.manager.ui.window.login;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;

import com.indexdata.livedocs.manager.core.DisplayInitializer;
import com.indexdata.livedocs.manager.core.SpringInitializer;
import com.indexdata.livedocs.manager.core.SpringThreadResult;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.service.UserService;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationManager;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;

public class LoginThreadEX implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginThreadEX.class);

    private final String login, password;
    private final SpringInitializer springInitializer;
    private final DisplayInitializer displayInitializer;
    private final Text loginStatusText;
    private final ProgressBar progressBar;
    private final Shell shell;
    private final SquareButton okButton;

    public LoginThreadEX(final DisplayInitializer displayInitializer, final SpringInitializer springInitializer,
            final Text loginStatusText, final ProgressBar progressBar, final SquareButton okButton, final Shell shell) {
        this.login = LiveDocsResourceManager.getSambaLogin();
        this.password = LiveDocsResourceManager.getSambaPassword();
        this.displayInitializer = displayInitializer;
        this.springInitializer = springInitializer;
        this.loginStatusText = loginStatusText;
        this.progressBar = progressBar;
        this.okButton = okButton;
        this.shell = shell;
    }

    @Override
    public void run() {
        Display.getDefault().syncExec(() -> {
            okButton.setEnabled(false);
        });

        SpringThreadResult springThreadResult = null;
        try {
            springThreadResult = springInitializer.getSpringThreadResult();
        } catch (Exception e) {
            reportError("Could not initialize Spring context\n" + e.getMessage());
            return;
        }

        final String errorMessage = springThreadResult.getErrorMessage();
        if (errorMessage != null) {
            reportError(I18NResources.getProperty("login.window.error") + errorMessage);
            return;
        }

        final User user = springThreadResult.getSpringContext().getBean(UserService.class).getUserByUsername(login);
        if (user == null) {
            reportError(I18NResources.getProperty("login.window.bad.credentials"));
            return;
        }

        try {
            ((UserAuthenticationManager) springThreadResult.getSpringContext().getBean("userAuthenticationManager"))
                    .setAppAuthenticationdata(login, password);
            LOGGER.info("Login confirmed for {}", login);
        } catch (BadCredentialsException be) {
            reportError(I18NResources.getProperty("login.window.bad.credentials"));
            return;
        }

        Display.getDefault().syncExec(() -> {
            progressBar.setVisible(false);
            shell.close();
            shell.dispose();
            new MainWindow(springInitializer, displayInitializer).open();
        });
    }

    private void reportError(final String errorMessage) {
        Display.getDefault().syncExec(() -> {
            progressBar.setVisible(false);
            loginStatusText.setText(errorMessage);
            loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
            okButton.setEnabled(true);
        });
    }
}
