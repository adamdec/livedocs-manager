package com.indexdata.livedocs.manager.ui.window.profile;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.AbstractWindow;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.profile.attribute.AddAttributeWindow;
import com.indexdata.livedocs.manager.ui.window.profile.elements.ProfileAttributeComposite;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class EditProfileWindow extends AbstractWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(EditProfileWindow.class);

    private final Composite documentProfilesComposite;
    private final Profile profile;
    private Composite profileAttributesComposite;
    private final List<Control> tabList = new ArrayList<Control>(5);

    public EditProfileWindow(final Composite documentProfilesComposite, final Profile profile) {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL));
        this.shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        this.shell.setSize(450, 340);
        this.shell.setText(messageSourceAdapter.getProperty("edit.profile.window.title", profile.getName()));
        UIControlUtils.centerWindowPosition(shell);
        this.documentProfilesComposite = documentProfilesComposite;
        this.profile = profile;
    }

    protected void createContents() {
        final Label profileNameLabel = new Label(shell, SWT.NONE);
        profileNameLabel.setBounds(10, 10, 100, 21);
        profileNameLabel.setText(I18NResources.getProperty("edit.profile.window.profile.name.label"));
        UIControlUtils.applyDefaultBackground(profileNameLabel);
        UIControlUtils.applyDefaultFont(profileNameLabel);

        final Text profileNameText = new Text(shell, SWT.BORDER | SWT.CENTER);
        profileNameText.setText(profile.getName());
        profileNameText.setBounds(10, 37, 424, 21);
        UIControlUtils.applyDefaultBackground(profileNameText);
        UIControlUtils.applyDefaultFont(profileNameText);

        final Label attributeLabel = new Label(shell, SWT.NONE);
        attributeLabel.setBounds(10, 70, 75, 21);
        attributeLabel.setText(I18NResources.getProperty("edit.profile.window.attributes.label"));
        UIControlUtils.applyDefaultBackground(attributeLabel);
        UIControlUtils.applyDefaultFont(attributeLabel);

        final SquareButton addAttributeButton = SquareButtonFactory.getWhiteImageButton(shell, 0, 8);
        addAttributeButton.setBackgroundImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
        addAttributeButton.setBounds(414, 70, 16, 16);
        addAttributeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                new AddAttributeWindow(profileAttributesComposite, profile).open();
            }
        });

        final ScrolledComposite profileAttributesScrolledComposite = new ScrolledComposite(shell, SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        profileAttributesScrolledComposite.setBounds(10, 95, 424, 168);
        profileAttributesScrolledComposite.setExpandHorizontal(true);
        profileAttributesScrolledComposite.setExpandVertical(true);
        UIControlUtils.applyDefaultBackground(profileAttributesScrolledComposite);

        this.profileAttributesComposite = new ProfileAttributeComposite(profileAttributesScrolledComposite, profile)
                .create();
        this.profileAttributesComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        this.profileAttributesComposite.setLayout(new GridLayout(3, false));
        profileAttributesScrolledComposite.setContent(profileAttributesComposite);
        profileAttributesScrolledComposite.setMinSize(profileAttributesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        final SquareButton cancelButton = SquareButtonFactory.getWhiteButton(shell);
        cancelButton.setBounds(354, 270, 80, 32);
        UIControlUtils.addCloseAction(shell, cancelButton, I18NResources.getProperty("cancel.button.label"));

        final SquareButton saveAndCloseProfileButton = SquareButtonFactory.getWhiteButton(shell);
        saveAndCloseProfileButton.setBounds(10, 270, 130, 32);
        saveAndCloseProfileButton.setText(I18NResources.getProperty("save.and.close.button.label"));
        saveAndCloseProfileButton.setImage(LiveDocsResourceManager.getImage(Resources.SAVE_BUTTON.getValue()));
        saveAndCloseProfileButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (profileNameText.getText().isEmpty()) {
                    UIControlUtils.createErrorMessageBox(shell,
                            I18NResources.getProperty("edit.profile.window.empty.profile"));
                    return;
                }

                if (!profile.getName().equals(profileNameText.getText())) {
                    profileService.changeProfileName(profile.getId(), profileNameText.getText());
                    LOGGER.debug("Changed profile name with id [{}] to name [{}]", profile.getId(),
                            profileNameText.getText());
                    UIControlUtils.refreshControl(documentProfilesComposite, AppEvents.REFRESH_PROFILES);
                    UIControlsRepository.getInstance().sendRefreshProfilesEventToDocumentProfilesCombo();
                }

                cancelButton.notifyListeners(SWT.Selection, new Event());
            }
        });

        this.tabList.add(0, profileNameText);
        this.tabList.add(1, addAttributeButton);
        this.tabList.add(2, profileAttributesScrolledComposite);
        this.tabList.add(3, saveAndCloseProfileButton);
        this.tabList.add(4, cancelButton);
        this.shell.setTabList(tabList.toArray(new Control[0]));
    }
}
