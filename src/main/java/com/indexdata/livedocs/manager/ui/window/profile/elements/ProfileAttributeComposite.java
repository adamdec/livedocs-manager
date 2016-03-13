package com.indexdata.livedocs.manager.ui.window.profile.elements;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.AttributeService;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.profile.attribute.EditAttributeWindow;
import com.indexdata.livedocs.manager.ui.window.profile.attribute.RemoveAttributeWindow;

/**
 * Populates all attributes for given profile (with actions).
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ProfileAttributeComposite {

    private static Logger LOGGER = LoggerFactory.getLogger(ProfileAttributeComposite.class);

    @Autowired
    private AttributeService attributeService;

    private final ScrolledComposite profileAttributesScrolledComposite;
    private final Composite profileAttributeComposite;
    private final Profile profile;

    public ProfileAttributeComposite(final ScrolledComposite profileAttributesScrolledComposite, final Profile profile) {
        this.profileAttributesScrolledComposite = profileAttributesScrolledComposite;
        this.profile = profile;
        this.profileAttributeComposite = new Composite(profileAttributesScrolledComposite, SWT.NONE);
    }

    public Composite create() {
        UIControlUtils.applyDefaultBackground(profileAttributeComposite);

        // Triggered by EditProfileWindow
        profileAttributeComposite.addListener(AppEvents.REFRESH_ATTRIBUTES.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_ATTRIBUTES' event {}", event);
                UIControlUtils.disposeChildren(profileAttributeComposite);
                populateData(profileAttributeComposite);
                profileAttributesScrolledComposite.setMinSize(profileAttributeComposite.computeSize(SWT.DEFAULT,
                        SWT.DEFAULT));
                profileAttributesScrolledComposite.layout(true, true);
            }
        });

        populateData(profileAttributeComposite);
        return profileAttributeComposite;
    }

    private void populateData(Composite profileAttributeComposite) {
        final List<Attribute> attributeList = attributeService.getAttributesForProfile(profile.getId());
        if (attributeList.size() == 0) {
            profileAttributeComposite.setLayout(new GridLayout(1, true));
            final Label noAttributesLabel = new Label(profileAttributeComposite, SWT.NONE);
            noAttributesLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            noAttributesLabel.setText(I18NResources.getProperty("manage.profile.window.no.attributes"));
            UIControlUtils.applyDefaultBackground(noAttributesLabel);
            UIControlUtils.applyDefaultFont(noAttributesLabel);
        }
        else {
            profileAttributeComposite.setLayout(new GridLayout(3, false));
            for (final Attribute attribute : attributeList) {
                final Label discAttribute = new Label(profileAttributeComposite, SWT.NONE);
                discAttribute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                UIControlUtils.applyDefaultBackground(discAttribute);
                UIControlUtils.applyDefaultFont(discAttribute);
                discAttribute.setText(attribute.getName());

                final SquareButton detailsButton = SquareButtonFactory.getWhiteImageButton(profileAttributeComposite,
                        0, 0);
                detailsButton.setImage(LiveDocsResourceManager.getImage(Resources.EDIT_BUTTON.getValue()));
                detailsButton.setData(discAttribute.getText());
                detailsButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        new EditAttributeWindow(profileAttributeComposite, profile, attribute).open();
                    }
                });
                detailsButton.setLayoutData(new GridData(16, 16));

                final SquareButton deleteButton = SquareButtonFactory.getWhiteImageButton(profileAttributeComposite, 0,
                        0);
                deleteButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));
                deleteButton.setData(discAttribute.getText());
                deleteButton.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        new RemoveAttributeWindow(profileAttributeComposite, profile, attribute).open();
                    }
                });
                deleteButton.setLayoutData(new GridData(16, 16));
            }
        }
    }
}
