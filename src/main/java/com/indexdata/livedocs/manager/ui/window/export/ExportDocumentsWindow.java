package com.indexdata.livedocs.manager.ui.window.export;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.AbstractWindow;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

/**
 * @author Adam Dec
 * @since 0.0.1
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ExportDocumentsWindow extends AbstractWindow {

    @Autowired
    private ProfileService profileService;

    private final AtomicBoolean interruptionFlag = new AtomicBoolean(false);
    private final Profile profile;

    public ExportDocumentsWindow(Long currentProfileId) {
        super(new Shell(Display.getDefault(), SWT.TITLE | SWT.SYSTEM_MODAL));
        this.profile = profileService.getProfileById(currentProfileId);
        this.shell.setSize(685, 253);
        this.shell.setText(messageSourceAdapter.getProperty("export.documents.window.title", profile.getName()));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
    }

    @Override
    protected void createContents() {
        final Composite selectDiscComposite = new Composite(shell, SWT.NONE);
        selectDiscComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        selectDiscComposite.setBounds(10, 10, 664, 67);

        final Label selectDiscLabel = new Label(selectDiscComposite, SWT.NONE);
        selectDiscLabel.setBounds(10, 10, 138, 21);
        selectDiscLabel.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
        selectDiscLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        selectDiscLabel.setText("Select Disc to Export:");

        final Combo selectDiscCombo = new Combo(selectDiscComposite, SWT.NONE);
        selectDiscCombo.setBounds(10, 37, 644, 23);
        selectDiscCombo.setText("Select disc to export");

        final Composite outputLocationComposite = new Composite(shell, SWT.NONE);
        outputLocationComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        outputLocationComposite.setBounds(10, 83, 664, 64);

        final Label outputLocationLabel = new Label(outputLocationComposite, SWT.NONE);
        outputLocationLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        outputLocationLabel.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
        outputLocationLabel.setBounds(10, 10, 108, 21);
        outputLocationLabel.setText("Output location:");

        final Text outputLocationText = new Text(outputLocationComposite, SWT.BORDER);
        outputLocationText.setText(LiveDocsResourceManager.getCurrentApplicationDirectory());
        outputLocationText.setBounds(10, 33, 563, 21);

        final Button browseButton = new Button(outputLocationComposite, SWT.FLAT | SWT.CENTER);
        UIControlUtils.addBrowseDirAction(shell, browseButton, outputLocationText);
        browseButton.setBounds(579, 31, 75, 25);
        browseButton.setText("Browse");

        final Button exportDiscsButton = new Button(shell, SWT.FLAT | SWT.CENTER);
        exportDiscsButton.setImage(LiveDocsResourceManager.getImage("export_documents_20x20.png"));
        exportDiscsButton.setBounds(10, 153, 128, 64);
        exportDiscsButton.setText("Export Discs");

        final Composite progressComposite = new Composite(shell, SWT.NONE);
        progressComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        progressComposite.setBounds(144, 153, 396, 64);

        final Label exportLabel = new Label(progressComposite, SWT.NONE);
        exportLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        exportLabel.setBounds(10, 10, 376, 15);

        final ProgressBar progressBar = new ProgressBar(progressComposite, SWT.SMOOTH);
        progressBar.setEnabled(false);
        progressBar.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
        progressBar.setBounds(10, 31, 376, 23);

        final Button closeAndCancelButton = new Button(shell, SWT.FLAT);
        UIControlUtils.addCloseAndCancelAction(shell, closeAndCancelButton, interruptionFlag);
        closeAndCancelButton.setBounds(546, 153, 128, 64);
        closeAndCancelButton.setText("Close");
    }
}
