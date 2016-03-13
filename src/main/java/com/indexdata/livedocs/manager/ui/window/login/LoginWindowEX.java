package com.indexdata.livedocs.manager.ui.window.login;

import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexdata.livedocs.manager.core.DisplayInitializer;
import com.indexdata.livedocs.manager.core.Keys;
import com.indexdata.livedocs.manager.core.SpringInitializer;
import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.DatabaseConnectionUtils;
import com.indexdata.livedocs.manager.core.utils.DiscFileUtils;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

/**
 * This window will provide UI to enter user credential. This credential will be stored in <code>SecurityContext</code>
 *
 * @author Adam Dec
 * @since 0.0.8
 */
public class LoginWindowEX {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginWindowEX.class);

    protected final Shell shell;
    private final DisplayInitializer displayInitializer;
    private final SpringInitializer springInitializer;
    private Text loginText, passwordText, ipAddressText, portText, loginStatusText;
    private ProgressBar progressBar;
    private SquareButton okButton, cancelButton;

    public LoginWindowEX(final SpringInitializer springInitializer, final DisplayInitializer displayInitializer) {
        this.springInitializer = springInitializer;
        this.displayInitializer = displayInitializer;
        this.shell = new Shell(Display.getDefault(), SWT.SYSTEM_MODAL);
        this.shell.setImage(LiveDocsResourceManager.getImage(Resources.ICON.getValue()));
        this.shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        this.shell.setSize(618, 204);
        UIControlUtils.centerWindowPosition(shell);
        UIControlUtils.applyDefaultBackground(shell);
    }

    protected void createContents() {
        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setBounds(304, 40, 303, 116);
        UIControlUtils.applyDefaultBackground(composite);

        final Label loginLabel = new Label(composite, SWT.NONE);
        loginLabel.setBounds(10, 10, 83, 22);
        loginLabel.setText(I18NResources.getProperty("login.window.login.label"));
        UIControlUtils.applyDefaultBackground(loginLabel);
        UIControlUtils.applyDefaultFont(loginLabel);

        this.loginText = new Text(composite, SWT.BORDER | SWT.CENTER);
        this.loginText.setBounds(99, 10, 194, 21);
        this.loginText.setText("admin");
        UIControlUtils.applyDefaultBackground(loginText);
        UIControlUtils.applyDefaultFont(loginText);

        final Label passwordLabel = new Label(composite, SWT.NONE);
        passwordLabel.setBounds(10, 38, 83, 22);
        passwordLabel.setText(I18NResources.getProperty("login.window.password.label"));
        UIControlUtils.applyDefaultBackground(passwordLabel);
        UIControlUtils.applyDefaultFont(passwordLabel);

        this.passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        this.passwordText.setBounds(99, 37, 194, 21);
        this.passwordText.setText("admin");
        UIControlUtils.applyDefaultBackground(passwordText);
        UIControlUtils.applyDefaultFont(passwordText);

        final Label ipAddressLabel = new Label(composite, SWT.NONE);
        ipAddressLabel.setBounds(10, 66, 83, 22);
        ipAddressLabel.setText(I18NResources.getProperty("login.window.ip.label"));
        UIControlUtils.applyDefaultBackground(ipAddressLabel);
        UIControlUtils.applyDefaultFont(ipAddressLabel);

        this.ipAddressText = new Text(composite, SWT.BORDER | SWT.CENTER);
        this.ipAddressText.setBounds(99, 64, 194, 21);
        this.ipAddressText.setText(DatabaseConnectionUtils.getIPAddres());
        UIControlUtils.applyDefaultBackground(ipAddressText);
        UIControlUtils.applyDefaultFont(ipAddressText);

        final Label portLabel = new Label(composite, SWT.NONE);
        portLabel.setBounds(10, 94, 83, 22);
        portLabel.setText(I18NResources.getProperty("login.window.port.label"));
        UIControlUtils.applyDefaultBackground(portLabel);
        UIControlUtils.applyDefaultFont(portLabel);

        this.portText = new Text(composite, SWT.BORDER | SWT.CENTER);
        this.portText.setBounds(99, 91, 194, 21);
        this.portText.setText(DatabaseConnectionUtils.getPort());
        UIControlUtils.applyDefaultBackground(portText);
        UIControlUtils.applyDefaultFont(portText);

        this.okButton = SquareButtonFactory.getWhiteButton(shell, 6, 35);
        this.okButton.setBounds(304, 162, 86, 32);
        this.okButton.setText(I18NResources.getProperty("ok.button.label"));

        this.cancelButton = SquareButtonFactory.getWhiteButton(shell);
        this.cancelButton.setBounds(520, 162, 86, 32);
        this.cancelButton.setText(I18NResources.getProperty("cancel.button.label"));

        final Label descriptionLabel = new Label(shell, SWT.LEFT);
        descriptionLabel.setText(I18NResources.getProperty("login.window.description.label"));
        descriptionLabel.setBounds(304, 10, 302, 24);
        UIControlUtils.applyDefaultBackground(descriptionLabel);
        UIControlUtils.applyBiggerBoldedFont(descriptionLabel);

        final Label imageLabel = new Label(shell, SWT.NONE);
        imageLabel.setImage(LiveDocsResourceManager.getImage(Resources.LOGO_LABEL.getValue()));
        imageLabel.setBounds(10, 10, 273, 88);
        UIControlUtils.applyDefaultBackground(imageLabel);

        this.loginStatusText = new Text(shell, SWT.WRAP | SWT.CENTER | SWT.MULTI);
        this.loginStatusText.setBounds(10, 127, 288, 67);
        UIControlUtils.applyDefaultBackground(loginStatusText);
        UIControlUtils.applyDefaultFont(loginStatusText);

        this.progressBar = new ProgressBar(shell, SWT.SMOOTH | SWT.INDETERMINATE);
        this.progressBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        this.progressBar.setVisible(false);
        this.progressBar.setBounds(10, 104, 288, 17);
        registerListeners();
    }

    private void registerListeners() {
        cancelButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                LOGGER.warn("Cancelled login phase, application will exit.");
                System.exit(0);
            }
        });

        final KeyAdapter keyAdapter = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == Keys.ENTER.getValue()) {
                    UIControlUtils.refreshControl(okButton, SWT.Selection, new Event());
                }
            }
        };

        this.loginText.addKeyListener(keyAdapter);
        this.passwordText.addKeyListener(keyAdapter);

        okButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (StringUtils.isEmpty(loginText.getText())) {
                    loginStatusText.setText(I18NResources.getProperty("login.window.empty.login"));
                    loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    return;
                }

                if (StringUtils.isEmpty(passwordText.getText())) {
                    loginStatusText.setText(I18NResources.getProperty("login.window.empty.password"));
                    loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    return;
                }

                if (StringUtils.isEmpty(ipAddressText.getText())) {
                    loginStatusText.setText(I18NResources.getProperty("login.window.empty.ip.address"));
                    loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    return;
                }

                if (!InetAddressValidator.getInstance().isValidInet4Address(ipAddressText.getText())) {
                    loginStatusText.setText(I18NResources.getProperty("login.window.invalid.ip.address"));
                    loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    return;
                }

                if (StringUtils.isEmpty(portText.getText())) {
                    loginStatusText.setText(I18NResources.getProperty("login.window.empty.port"));
                    loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    return;
                }

                try {
                    Integer.valueOf(portText.getText());
                } catch (NumberFormatException exc) {
                    loginStatusText.setText(I18NResources.getProperty("login.window.invalid.port"));
                    loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                }

                DatabaseConnectionUtils.storeProperties(ipAddressText.getText(), portText.getText());
                if ("H2".equals(ManagerConfiguration.hibernateAppDialect())) {
                    final String jdbcUrl = DatabaseConnectionUtils.createH2Url(ipAddressText.getText(),
                            portText.getText());
                    LOGGER.debug("Setting jdbc.url to {}", jdbcUrl);
                    System.setProperty("jdbc.url", jdbcUrl);
                    System.setProperty("spring.profiles.active", "H2");
                }
                else {
                    DatabaseConnectionUtils.createPostgresConnectionProperties(ipAddressText.getText(),
                            portText.getText());
                    System.setProperty("spring.profiles.active", "POSTGRES");
                }

                progressBar.setVisible(true);
                progressBar.redraw();
                progressBar.update();

                loginStatusText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                loginStatusText.setText(I18NResources.getProperty("loading.label"));

                DiscFileUtils.removeSambaManager();
                LiveDocsResourceManager.setSambaLogin(loginText.getText());
                LiveDocsResourceManager.setSambaPassword(passwordText.getText());
                LiveDocsResourceManager.setSambaServer(ipAddressText.getText());

                try {
                    springInitializer.init();
                } catch (Exception exc) {
                    UIControlUtils.createErrorMessageBoxWithTitle(new Shell(),
                            "Could not initialize Spring=" + exc.getMessage());
                    return;
                }

                Executors.newSingleThreadScheduledExecutor().execute(
                        new LoginThreadEX(displayInitializer, springInitializer, loginStatusText, progressBar,
                                okButton, shell));
            }
        });
    }

    public void open() {
        this.open(false);
    }

    public void open(boolean isStandalone) {
        createContents();
        shell.open();
        shell.layout();

        if (isStandalone) {
            final Display display = Display.getCurrent();
            while (!display.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        }
    }
}
