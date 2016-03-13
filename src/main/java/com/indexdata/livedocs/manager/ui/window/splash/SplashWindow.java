package com.indexdata.livedocs.manager.ui.window.splash;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.indexdata.livedocs.manager.core.DisplayThread;
import com.indexdata.livedocs.manager.core.SpringInitializer;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

/**
 * Splash window.
 *
 * @author Adam Dec
 * @since 0.0.2
 */
@Deprecated
public class SplashWindow {

    public SplashWindow(final SpringInitializer springInitializer) {
        final Shell shell = new Shell(DisplayThread.getDisplay(), SWT.SYSTEM_MODAL);
        shell.setImage(LiveDocsResourceManager.getImage(Resources.ICON.getValue()));
        UIControlUtils.applyDefaultBackground(shell);
        shell.setAlpha(230);
        shell.setSize(314, 195);

        final Composite logoComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(logoComposite);
        logoComposite.setBounds(10, 10, 293, 108);

        final Label logoLabel = new Label(logoComposite, SWT.NONE);
        logoLabel.setImage(LiveDocsResourceManager.getImage(Resources.LOGO_LABEL.getValue()));
        logoLabel.setBounds(10, 10, 273, 88);
        UIControlUtils.applyDefaultBackground(logoLabel);

        final Composite loadingComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(loadingComposite);
        loadingComposite.setBounds(10, 124, 293, 60);

        final Label loadingLabel = new Label(loadingComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(loadingLabel);
        UIControlUtils.applyDefaultFont(loadingLabel);
        loadingLabel.setBounds(10, 10, 250, 18);
        loadingLabel.setText(I18NResources.getProperty("loading.label"));

        final ProgressBar progressBar = new ProgressBar(loadingComposite, SWT.SMOOTH | SWT.INDETERMINATE);
        progressBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
        progressBar.setBounds(10, 34, 273, 21);

        UIControlUtils.centerWindowPosition(shell);

        shell.open();
        shell.layout();

        final Display display = Display.getDefault();
        while (!springInitializer.pollFor()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        shell.close();
        shell.dispose();
    }
}