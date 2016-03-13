package com.indexdata.livedocs.manager.ui.window.main.elements.attributes;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.events.FileQueryData;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.profiles.DocumentProfilesComboAdapter;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.TableFileQueryData;

/**
 * Clears values in attributeSearchComposite.
 *
 * @author Adam Dec
 * @since 0.0.5
 */
public final class AttributeSearchClearButton {

    private static Logger LOGGER = LoggerFactory.getLogger(AttributeSearchClearButton.class);

    private final Composite attributeSearchComposite;
    private final ProfileAttributesGroupAdapter profileAttributesGroup;
    private final DocumentProfilesComboAdapter documentProfilesComboAdapter;

    private SquareButton attributeSearchClearButton;

    public AttributeSearchClearButton(final Composite attributeSearchComposite,
            final ProfileAttributesGroupAdapter profileAttributesGroup,
            final DocumentProfilesComboAdapter documentProfilesComboAdapter) {
        this.attributeSearchComposite = attributeSearchComposite;
        this.profileAttributesGroup = profileAttributesGroup;
        this.documentProfilesComboAdapter = documentProfilesComboAdapter;
    }

    public SquareButton create() {
        this.attributeSearchClearButton = SquareButtonFactory.getWhiteButton(attributeSearchComposite, 1, 18);
        this.attributeSearchClearButton.setBounds(96, 10, 65, 22);
        this.attributeSearchClearButton.setText(I18NResources.getProperty("clear.button.label"));
        UIControlsRepository.getInstance().setAttributeSearchClearButton(attributeSearchClearButton);
        registerListeners();
        return attributeSearchClearButton;
    }

    private void registerListeners() {
        attributeSearchClearButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                LOGGER.debug("Received 'SELECTION' event {}", event);
                if (profileAttributesGroup != null) {
                    profileAttributesGroup.clear();
                }
                final Profile currentProfile = documentProfilesComboAdapter.getCurrentProfile();
                if (currentProfile != null) {

                    // Reset files in table
                    UIControlsRepository.getInstance().sendRefreshAttributesEventToScrolledCompositeDiscTable(
                            new TableFileQueryData(currentProfile));

                    // Reset pagination and documents number
                    UIControlsRepository.getInstance().sendPaginationAndDocsNoEventToSearchResultsComposite(
                            new FileQueryData());
                }
            }
        });
    }
}
