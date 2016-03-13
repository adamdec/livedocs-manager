package com.indexdata.livedocs.manager.ui.window.profile.elements;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.utils.DateFormatterUtils;
import com.indexdata.livedocs.manager.service.DiscService;
import com.indexdata.livedocs.manager.service.model.Disc;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

/**
 * Populates all discs data for given profile.
 * 
 * @author Adam Dec
 * @since 0.0.6
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ProfileDetailsDiscsComposite {

    private static Logger LOGGER = LoggerFactory.getLogger(ProfileDetailsDiscsComposite.class);

    @Autowired
    private DiscService discService;

    private final ScrolledComposite discsScrolledComposite;
    private final Profile profile;
    private final SquareButton removeDiscButton;

    private volatile Disc selectedDisc;

    public ProfileDetailsDiscsComposite(final ScrolledComposite discsScrolledComposite, final Profile profile,
            final SquareButton removeDiscButton) {
        this.discsScrolledComposite = discsScrolledComposite;
        this.profile = profile;
        this.removeDiscButton = removeDiscButton;
    }

    public Table create() {
        discsScrolledComposite.addListener(AppEvents.REFRESH_DISCS.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_DISCS' event {}", event);
                UIControlUtils.disposeChildren(discsScrolledComposite);
                final Table table = populateData(discsScrolledComposite);
                discsScrolledComposite.setContent(table);
                discsScrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                discsScrolledComposite.layout(true, true);
            }
        });
        return populateData(discsScrolledComposite);
    }

    private Table populateData(ScrolledComposite composite) {
        final Table discTable = new Table(composite, SWT.FULL_SELECTION);
        composite.setContent(discTable);
        discTable.setLinesVisible(true);
        discTable.setHeaderVisible(true);
        UIControlUtils.applyDefaultFont(discTable);

        final TableColumn discTitleColumn = new TableColumn(discTable, SWT.CENTER);
        discTitleColumn.setWidth(130);
        discTitleColumn.setText(I18NResources.getProperty("details.profile.window.disc.title.column"));

        final TableColumn batchNumberColumn = new TableColumn(discTable, SWT.CENTER);
        batchNumberColumn.setWidth(160);
        batchNumberColumn.setText(I18NResources.getProperty("details.profile.window.batch.number.column"));

        final TableColumn dateImportedColumn = new TableColumn(discTable, SWT.CENTER);
        dateImportedColumn.setWidth(105);
        dateImportedColumn.setText(I18NResources.getProperty("details.profile.window.date.imported.column"));

        final TableColumn filesColumn = new TableColumn(discTable, SWT.CENTER);
        filesColumn.setWidth(55);
        filesColumn.setText(I18NResources.getProperty("details.profile.window.files.column"));

        final List<Disc> discList = discService.getAllDiscsByProfileId(profile.getId());
        for (Disc disc : discList) {
            final TableItem item = new TableItem(discTable, SWT.NONE);
            item.setData(disc);
            item.setText(new String[] {
                    disc.getTitle(), disc.getBatchNumber(), DateFormatterUtils.convertDate(disc.getImportDate()),
                    String.valueOf(disc.getFilesNumber())
            });
        }

        discTable.addListener(SWT.MouseDown, new Listener() {

            @Override
            public void handleEvent(Event e) {
                final Point pt = new Point(e.x, e.y);
                final TableItem selection = discTable.getItem(pt);
                if (selection == null) {
                    removeDiscButton.setEnabled(false);
                    return;
                }
                selectedDisc = (Disc) selection.getData();
                if (selectedDisc != null) {
                    removeDiscButton.setEnabled(true);
                }
            }
        });
        return discTable;
    }

    public Disc getSelectedDisc() {
        return selectedDisc;
    }
}