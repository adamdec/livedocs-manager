package com.indexdata.livedocs.manager.ui.window.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.repository.utils.QueryUtils;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractComponent;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.attributes.AttributeSearchButton;
import com.indexdata.livedocs.manager.ui.window.main.elements.attributes.AttributeSearchClearButton;
import com.indexdata.livedocs.manager.ui.window.main.elements.attributes.ProfileAttributesGroupAdapter;
import com.indexdata.livedocs.manager.ui.window.main.elements.profiles.DocumentProfilesComboAdapter;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.SearchResultsComposite;
import com.indexdata.livedocs.manager.ui.window.main.elements.table.DiscTable;

/**
 * This is the main content of the application. It shows profiles and attributes.
 *
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class MainContent extends AccessAbstractComponent {

    @Autowired
    private QueryUtils queryListUtils;

    // This is the with of Combo and attributes search
    public static final int FIRST_PANEL_WIDTH = 181;
    public static final int SECOND_PANEL_X = 197;

    private final Shell mainWindow;
    private final Composite mainComposite;
    private final Composite footerComposite;

    public MainContent(final Shell mainWindow, final Composite mainComposite, final Composite footerComposite) {
        this.mainWindow = mainWindow;
        this.mainComposite = mainComposite;
        this.footerComposite = footerComposite;
    }

    private ScrolledComposite scrolledCompositeDiscTable;
    private Composite searchResultsComposite, attributeSearchComposite;
    private Listener mainContentListener;
    private ProfileAttributesGroupAdapter profileAttributesGroupAdapter;

    public void createContent() {
        // Document profiles
        final DocumentProfilesComboAdapter documentProfilesComboAdapter = new DocumentProfilesComboAdapter(
                mainComposite);
        documentProfilesComboAdapter.createContent();

        // Profile attributes
        this.profileAttributesGroupAdapter = new ProfileAttributesGroupAdapter(mainComposite,
                documentProfilesComboAdapter);
        this.profileAttributesGroupAdapter.createContent();

        // Search buttons composite
        this.attributeSearchComposite = new Composite(mainComposite, SWT.NONE);
        this.attributeSearchComposite.setBounds(10, profileAttributesGroupAdapter.getAttributesScrolledComposite()
                .getLocation().y
                + profileAttributesGroupAdapter.getAttributesScrolledComposite().getSize().y
                + MainWindow.VERTICAL_MARGIN, FIRST_PANEL_WIDTH, 45);
        UIControlUtils.applyDefaultBackground(attributeSearchComposite);

        new AttributeSearchButton(attributeSearchComposite, profileAttributesGroupAdapter,
                documentProfilesComboAdapter, queryListUtils).createContent();
        new AttributeSearchClearButton(attributeSearchComposite, profileAttributesGroupAdapter,
                documentProfilesComboAdapter).create();

        // Search files section (previous, next, search information)
        this.searchResultsComposite = new SearchResultsComposite(mainWindow, mainComposite,
                new Point(SECOND_PANEL_X, 0)).createContent();

        // Disc table (for given profile)
        this.scrolledCompositeDiscTable = new ScrolledComposite(mainComposite, SWT.NONE);
        this.scrolledCompositeDiscTable.setAlwaysShowScrollBars(true);
        this.scrolledCompositeDiscTable.setExpandVertical(true);
        this.scrolledCompositeDiscTable.setExpandHorizontal(true);
        this.scrolledCompositeDiscTable.setLocation(SECOND_PANEL_X, searchResultsComposite.getSize().y
                + MainWindow.VERTICAL_MARGIN);
        UIControlsRepository.getInstance().setScrolledCompositeDiscTable(scrolledCompositeDiscTable);
        UIControlUtils.applyDefaultBackground(scrolledCompositeDiscTable);

        new DiscTable(scrolledCompositeDiscTable).create();

        registerMainContentListeners();

        Display.getDefault().asyncExec(() -> {
            mainComposite.notifyListeners(AppEvents.FOCUS.getValue(), new Event());
        });
    }

    private void registerMainContentListeners() {
        this.mainContentListener = new Listener() {

            @Override
            public void handleEvent(Event e) {
                final Rectangle mainWindowRectangle = mainWindow.getClientArea();

                // Horizontal scaling
                searchResultsComposite.setSize(mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING
                        - searchResultsComposite.getLocation().x, searchResultsComposite.getSize().y);

                final int newCompositeWidth = mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING
                        - scrolledCompositeDiscTable.getLocation().x;
                scrolledCompositeDiscTable.setSize(newCompositeWidth, scrolledCompositeDiscTable.getSize().y);

                // Vertical scaling
                attributeSearchComposite.setLocation(attributeSearchComposite.getLocation().x,
                        mainWindowRectangle.height - footerComposite.getSize().y - attributeSearchComposite.getSize().y
                                - mainComposite.getLocation().y);

                profileAttributesGroupAdapter.getAttributesScrolledComposite().setSize(
                        profileAttributesGroupAdapter.getAttributesScrolledComposite().getSize().x,
                        mainWindowRectangle.height - footerComposite.getSize().y
                                - profileAttributesGroupAdapter.getAttributesScrolledComposite().getLocation().y
                                - attributeSearchComposite.getSize().y - mainComposite.getLocation().y);

                scrolledCompositeDiscTable.setSize(scrolledCompositeDiscTable.getSize().x, mainWindowRectangle.height
                        - attributeSearchComposite.getSize().y - footerComposite.getSize().y
                        - scrolledCompositeDiscTable.getLocation().y - mainComposite.getLocation().y);
            }
        };
        mainWindow.addListener(SWT.Resize, getMainContentListener());
    }

    public Listener getMainContentListener() {
        return mainContentListener;
    }
}
