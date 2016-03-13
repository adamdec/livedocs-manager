package com.indexdata.livedocs.manager.ui.window.profile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractWindow;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.profile.elements.DocumentProfilesComposite;

/**
 * This component provides profiles management.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ManageProfileWindow extends AccessAbstractWindow {

    public ManageProfileWindow() {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL));
        this.shell.setSize(450, 285);
        this.shell.setText(I18NResources.getProperty("manage.profile.window.title"));
        UIControlUtils.centerWindowPosition(shell);
        UIControlUtils.applyDefaultBackground(shell);
    }

    protected void createContents() {
        final Composite documentAddProfileComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(documentAddProfileComposite);
        documentAddProfileComposite.setBounds(10, 10, 424, 35);

        final Label documentProfilesLabel = new Label(documentAddProfileComposite, SWT.NONE);
        documentProfilesLabel.setBounds(0, 10, 134, 21);
        documentProfilesLabel.setText(I18NResources.getProperty("manage.profile.window.document.profiles.label"));
        UIControlUtils.applyDefaultBackground(documentProfilesLabel);
        UIControlUtils.applyDefaultFont(documentProfilesLabel);

        final ScrolledComposite documentProfilesScrolledComposite = new ScrolledComposite(shell, SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        documentProfilesScrolledComposite.setBounds(10, 45, 424, 169);
        documentProfilesScrolledComposite.setExpandHorizontal(true);
        documentProfilesScrolledComposite.setExpandVertical(true);
        UIControlUtils.applyDefaultBackground(documentProfilesScrolledComposite);

        final Composite documentProfilesComposite = new DocumentProfilesComposite(documentProfilesScrolledComposite)
                .create();
        documentProfilesScrolledComposite.setContent(documentProfilesComposite);
        documentProfilesScrolledComposite.setMinSize(documentProfilesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        if (getProfileAccess().equals(AccessMode.Manage)) {
            final SquareButton addProfileButton = SquareButtonFactory.getWhiteImageButton(documentAddProfileComposite,
                    0, 8);
            addProfileButton.setBackgroundImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
            addProfileButton.setBounds(404, 6, 16, 16);
            addProfileButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    new AddProfileWindow(documentProfilesComposite).open();
                }
            });
        }

        final SquareButton closeButton = SquareButtonFactory.getWhiteButton(shell);
        closeButton.setBounds(354, 220, 80, 32);
        closeButton.setText(I18NResources.getProperty("close.button.label"));
        UIControlUtils.addCloseAction(shell, closeButton);
    }

    public static void main(String[] args) {
        new ManageProfileWindow().open(true);
    }
}
