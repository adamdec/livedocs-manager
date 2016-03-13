package com.indexdata.livedocs.manager.ui.window.main.elements.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.Keys;
import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.MainContent;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;
import com.indexdata.livedocs.manager.ui.window.main.elements.profiles.DocumentProfilesComboAdapter;

/**
 * Populates all attributes with default values for given profile.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public final class ProfileAttributesGroupAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(ProfileAttributesGroupAdapter.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserAuthenticationContext userAuthenticationContext;

    private final ScrolledComposite attributesScrolledComposite;
    private final Group profileAttributesGroup;
    private List<Text> attributeTextList;

    public ProfileAttributesGroupAdapter(Composite mainComposite,
            DocumentProfilesComboAdapter documentProfilesComboAdapter) {
        this.attributesScrolledComposite = new ScrolledComposite(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
        this.attributesScrolledComposite.setBounds(MainWindow.LEFT_SIDE_PADDING, documentProfilesComboAdapter
                .getDocumentProfilesGroup().getSize().y + MainWindow.VERTICAL_MARGIN, MainContent.FIRST_PANEL_WIDTH,
                415);
        this.attributesScrolledComposite.setExpandHorizontal(true);
        this.attributesScrolledComposite.setExpandVertical(true);

        this.profileAttributesGroup = new Group(attributesScrolledComposite, SWT.NONE);
        this.profileAttributesGroup.setText(I18NResources.getProperty("main.window.attribute.attributes.group.label"));
        this.profileAttributesGroup.setLayout(new GridLayout(1, true));
        UIControlUtils.applyDefaultBoldedFont(profileAttributesGroup);
        UIControlUtils.applyDefaultBackground(profileAttributesGroup);
        UIControlsRepository.getInstance().setProfileAttributesGroup(profileAttributesGroup);
        this.attributesScrolledComposite.setContent(profileAttributesGroup);
        this.attributesScrolledComposite.setMinSize(profileAttributesGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public void createContent() {
        profileAttributesGroup.addListener(AppEvents.REFRESH_ATTRIBUTES.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_ATTRIBUTES' event {}", event);
                UIControlUtils.disposeChildren(profileAttributesGroup);
                if (event.data != null) {
                    populateProfileAttributesData(profileAttributesGroup, (Long) event.data);
                }
                attributesScrolledComposite.setMinSize(profileAttributesGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                attributesScrolledComposite.layout(true, true);
            }
        });
        populateProfileAttributesData(profileAttributesGroup, null);
    }

    private void populateProfileAttributesData(final Group profileAttributesGroup, final Long profileId) {
        final List<Attribute> attributeList = getProfileAttributes(profileId);
        this.attributeTextList = new ArrayList<Text>(attributeList.size());
        for (Attribute attribute : attributeList) {
            final Label attributeLabel = new Label(profileAttributesGroup, SWT.NONE);
            attributeLabel.setText(attribute.getName());
            attributeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            UIControlUtils.applyDefaultBackground(attributeLabel);
            UIControlUtils.applyDefaultFont(attributeLabel);

            final Text attributeText = new Text(profileAttributesGroup, SWT.BORDER | SWT.CENTER);
            attributeText.setData(attribute);
            attributeText.setText("");
            attributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            attributeText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.keyCode == Keys.ENTER.getValue()) {
                        UIControlsRepository.getInstance().sendSelectionEventToAttributesSearchButton();
                    }
                }
            });
            if (!attribute.getMapped()) {
                attributeText.setEnabled(false);
            }
            UIControlUtils.applyDefaultBackground(attributeText);
            UIControlUtils.applyDefaultItalicFont(attributeText);
            UIControlUtils.defaultTextAfterFocus(attributeText, "");

            attributeTextList.add(attributeText);
        }
    }

    /**
     * Used by AttributeSearchButton
     */
    public List<Text> getAttributeTextList() {
        return attributeTextList;
    }

    public ScrolledComposite getAttributesScrolledComposite() {
        return attributesScrolledComposite;
    }

    public void clear() {
        for (Text attributeText : attributeTextList) {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (!attributeText.isDisposed()) {
                        attributeText.setText("");
                        attributeText.update();
                    }
                }
            });
        }
    }

    private List<Attribute> getProfileAttributes(final Long profileId) {
        final Profile profile = profileId == null ? profileService.getLatestProfile() : profileService
                .getProfileById(profileId);
        if (profile != null) {
            final AccessMode accessToProfile = userAuthenticationContext.getAccessToProfiles(profile.getId());
            if (AccessMode.Denied.equals(accessToProfile)) {
                LOGGER.warn("You do not have access to profile with id={} due to access mode={}", profile.getId(),
                        accessToProfile);
                return Collections.emptyList();
            }
            return profile.getAttributes();
        }
        return Collections.emptyList();
    }
}
