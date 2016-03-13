package com.indexdata.livedocs.manager.ui.window.main.elements.attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.repository.utils.AttributeNameValue;
import com.indexdata.livedocs.manager.repository.utils.QueryUtils;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.profiles.DocumentProfilesComboAdapter;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.FilePageRequest;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.TableFileQueryData;

/**
 * Creates a DB query based on the values taken from attributes. <br />
 * Main responsibility is to create query filter.
 *
 * @author Adam Dec
 * @since 0.0.5
 */
public final class AttributeSearchButton {

    private static Logger LOGGER = LoggerFactory.getLogger(AttributeSearchButton.class);

    private final Composite attributeSearchComposite;
    private final ProfileAttributesGroupAdapter profileAttributesGroup;
    private final DocumentProfilesComboAdapter documentProfilesComboAdapter;
    private final QueryUtils queryUtils;

    private SquareButton attributeSearchButton;

    public AttributeSearchButton(final Composite attributeSearchComposite,
            final ProfileAttributesGroupAdapter profileAttributesGroup,
            final DocumentProfilesComboAdapter documentProfilesComboAdapter, final QueryUtils queryUtils) {
        this.attributeSearchComposite = attributeSearchComposite;
        this.profileAttributesGroup = profileAttributesGroup;
        this.documentProfilesComboAdapter = documentProfilesComboAdapter;
        this.queryUtils = queryUtils;
    }

    public SquareButton createContent() {
        this.attributeSearchButton = SquareButtonFactory.getWhiteButton(attributeSearchComposite, 1, 12);
        this.attributeSearchButton.setBounds(20, 10, 65, 22);
        this.attributeSearchButton.setText(I18NResources.getProperty("search.button.label"));
        UIControlsRepository.getInstance().setAttributeSearchButton(attributeSearchButton);
        registerListeners();
        return attributeSearchButton;
    }

    private void registerListeners() {
        // This is triggered by SearchResultsComposite, FileSortListener
        this.attributeSearchButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                LOGGER.debug("Received 'SELECTION' event {}", event);
                final Profile currentProfile = documentProfilesComboAdapter.getCurrentProfile();
                if (currentProfile == null) {
                    LOGGER.warn("Current profile is null");
                    return;
                }

                final List<Text> attributeTextList = profileAttributesGroup.getAttributeTextList();
                final List<AttributeNameValue> attributeNameValueList = new ArrayList<AttributeNameValue>(
                        attributeTextList.size());
                for (Text attributeText : attributeTextList) {
                    if (StringUtils.isEmpty(attributeText.getText())) {
                        // Attributes order selection in UI must be maintained thats why we are having nulls here
                        attributeNameValueList.add(new AttributeNameValue());
                    }
                    else {
                        attributeNameValueList.add(new AttributeNameValue(((Attribute) attributeText.getData())
                                .getName(), attributeText.getText()));
                    }
                }

                // Query filter created using UI text boxes
                final Map<String, QueryFilter> queryFilterMap = queryUtils.createFilterList(attributeNameValueList);
                LOGGER.debug("Created query filter map\n{}", queryFilterMap);
                final List<QueryFilter> queryFilterList = queryUtils.preserveAttributeNameOrdering(queryFilterMap,
                        currentProfile.getId());
                LOGGER.debug("Created query filter list after ordering\n{}", queryFilterList);

                // Send by clicking sorting on the column (FileSortListener) or default if none
                final FilePageRequest filePageRequest = event.data != null ? queryUtils.preserveFieldOrdering(
                        (FilePageRequest) event.data, currentProfile.getId()) : new FilePageRequest();
                LOGGER.debug("Created filePageRequest\n{}", filePageRequest);

                UIControlsRepository.getInstance().sendTableDataEventToScrolledCompositeDiscTable(
                        new TableFileQueryData(currentProfile, queryFilterList, filePageRequest));
            }
        });
    }
}
