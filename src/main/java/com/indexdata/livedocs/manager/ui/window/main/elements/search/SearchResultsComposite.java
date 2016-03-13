package com.indexdata.livedocs.manager.ui.window.main.elements.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Order;

import com.indexdata.livedocs.manager.core.Keys;
import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.events.FileQueryData;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;

/**
 * @author Adam Dec
 * @since 0.0.6
 */
public final class SearchResultsComposite {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchResultsComposite.class);

    private final Shell shell;
    private final Composite searchResultsComposite;

    private Label documentsCountLabel, paginationLabel;
    private Text gotoPageText;
    private SquareButton previousPageButton, nextPageButton;
    private int currentPage = 0, maxPage = 0;
    private long currentFilesCount = 0, totalFilesCount = 0;
    private Order selectedFileOrder;

    public SearchResultsComposite(final Shell shell, final Composite mainComposite, final Point position) {
        this.shell = shell;
        this.searchResultsComposite = new Composite(mainComposite, SWT.NONE);
        this.searchResultsComposite.setBounds(position.x, position.y, 609, 30);
        UIControlsRepository.getInstance().setSearchResultsComposite(searchResultsComposite);
        UIControlUtils.applyDefaultBackground(searchResultsComposite);
        UIControlUtils.applyDefaultFont(searchResultsComposite);
    }

    public Composite createContent() {
        final Label selectPageLabel = new Label(searchResultsComposite, SWT.NONE);
        selectPageLabel.setBounds(10, 5, 90, 20);
        selectPageLabel.setText(I18NResources.getProperty("main.window.search.results.goto.page"));
        UIControlUtils.applyDefaultBackground(selectPageLabel);
        UIControlUtils.applyDefaultBoldedFont(selectPageLabel);

        this.gotoPageText = new Text(searchResultsComposite, SWT.BORDER | SWT.CENTER);
        this.gotoPageText.setBounds(100, 5, 80, 20);
        this.gotoPageText.setEnabled(false);
        UIControlUtils.applyDefaultBackground(gotoPageText);
        UIControlUtils.applyDefaultFont(gotoPageText);

        this.previousPageButton = SquareButtonFactory.getWhiteImageButton(searchResultsComposite, 0, 8);
        this.previousPageButton.setBackgroundImage(LiveDocsResourceManager.getImage(Resources.PAGE_PREV_BUTTON
                .getValue()));
        this.previousPageButton.setBounds(353, 5, 16, 16);

        this.paginationLabel = new Label(searchResultsComposite, SWT.NONE);
        this.paginationLabel.setAlignment(SWT.CENTER);
        this.paginationLabel.setBounds(167, 5, 180, 20);
        UIControlUtils.applyDefaultBackground(paginationLabel);
        UIControlUtils.applyDefaultFont(paginationLabel);

        this.nextPageButton = SquareButtonFactory.getWhiteImageButton(searchResultsComposite, 0, 8);
        this.nextPageButton.setBackgroundImage(LiveDocsResourceManager.getImage(Resources.PAGE_NEXT_BUTTON.getValue()));
        this.nextPageButton.setBounds(131, 5, 16, 16);

        this.documentsCountLabel = new Label(searchResultsComposite, SWT.NONE);
        this.documentsCountLabel.setAlignment(SWT.RIGHT);
        this.documentsCountLabel.setBounds(389, 5, 240, 20);
        UIControlUtils.applyDefaultBackground(documentsCountLabel);
        UIControlUtils.applyDefaultFont(documentsCountLabel);

        registerListeners();
        return searchResultsComposite;
    }

    private void requestFilesPage(final int offset, final int limit, final Order order) {
        UIControlsRepository.getInstance().sendSelectionEventToAttributesSearchButton(
                order != null ? new FilePageRequest(offset, limit, order) : new FilePageRequest(offset, limit));
    }

    private void registerListeners() {
        // This is triggered by TableDiscDataAdapter (only on first page), DocumentProfilesComboAdapter, AttributeSearchClearButton
        this.searchResultsComposite.addListener(AppEvents.PAGINATION_AND_DOCS_NO.getValue(), new Listener() {

            @Override
            public void handleEvent(final Event event) {
                LOGGER.debug("Received 'TOTAL_DISCS' event {}", event);
                totalFilesCount = ((FileQueryData) event.data).getTotalFilesNumber();
                selectedFileOrder = ((FileQueryData) event.data).getOrder();

                if (totalFilesCount != 0) {
                    if (totalFilesCount > ManagerConfiguration.getGridPaginationLimit()) {
                        currentPage = 0;
                        currentFilesCount = ManagerConfiguration.getGridPaginationLimit();
                        maxPage = (int) totalFilesCount / ManagerConfiguration.getGridPaginationLimit();
                        if (totalFilesCount % ManagerConfiguration.getGridPaginationLimit() != 0) {
                            maxPage++;
                        }
                    }
                    else if (totalFilesCount <= ManagerConfiguration.getGridPaginationLimit()) {
                        currentFilesCount = totalFilesCount;
                        maxPage = 1;
                        currentPage = 0;
                    }
                    previousPageButton.setEnabled(true);
                    nextPageButton.setEnabled(true);
                    gotoPageText.setEnabled(true);
                    gotoPageText.setText(String.valueOf(currentPage + 1));
                }
                else {
                    currentPage = -1;
                    maxPage = 0;
                    currentFilesCount = 0;
                    gotoPageText.setEnabled(false);
                    previousPageButton.setEnabled(false);
                    nextPageButton.setEnabled(false);
                }

                UIControlUtils.setPagination(paginationLabel, currentPage, maxPage);
                UIControlUtils.setDocumentsCount(documentsCountLabel, currentFilesCount, totalFilesCount);
            }
        });

        this.gotoPageText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent event) {
                int selectedPage = 0;
                try {
                    selectedPage = Integer.valueOf(gotoPageText.getText());
                } catch (NumberFormatException exc) {
                    gotoPageText.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    return;
                }

                gotoPageText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
                if (selectedPage >= 1 && selectedPage <= maxPage) {
                    if (event.keyCode == Keys.ENTER.getValue()) {
                        currentPage = selectedPage - 1;
                        long offset = selectedPage * currentFilesCount;
                        LOGGER.debug("Offset={}, selectedPage={}", offset, selectedPage);
                        UIControlUtils.setDocumentsCount(documentsCountLabel, offset, totalFilesCount);
                        UIControlUtils.setPagination(paginationLabel, selectedPage - 1, maxPage);
                        requestFilesPage(selectedPage - 1, ManagerConfiguration.getGridPaginationLimit(),
                                selectedFileOrder);
                    }
                }
                else {
                    gotoPageText.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                }
            }

        });

        this.previousPageButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                currentPage--;
                if (currentPage >= 0) {
                    long offset = (currentPage + 1) * currentFilesCount;
                    LOGGER.debug("Offset={}, currentPage={}", offset, currentPage);
                    UIControlUtils.setDocumentsCount(documentsCountLabel, offset, totalFilesCount);
                    UIControlUtils.setPagination(paginationLabel, currentPage, maxPage);
                    requestFilesPage(currentPage, ManagerConfiguration.getGridPaginationLimit(), selectedFileOrder);
                }
                else if (currentPage < 0) {
                    currentPage = 0;
                }
            }
        });

        this.nextPageButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                currentPage++;
                if (currentPage < maxPage) {
                    long offset = (currentPage + 1) * currentFilesCount;
                    offset = offset < totalFilesCount ? offset : totalFilesCount;
                    LOGGER.debug("Offset={}, currentPage={}", offset, currentPage);
                    UIControlUtils.setDocumentsCount(documentsCountLabel, offset, totalFilesCount);
                    UIControlUtils.setPagination(paginationLabel, currentPage, maxPage);
                    requestFilesPage(currentPage, ManagerConfiguration.getGridPaginationLimit(), selectedFileOrder);
                }
                else if (currentPage >= maxPage) {
                    currentPage = maxPage - 1;
                }
            }
        });

        final KeyAdapter keyAdapter = new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_LEFT) {
                    previousPageButton.notifyListeners(SWT.Selection, new Event());
                }
                else if (e.keyCode == SWT.ARROW_RIGHT) {
                    nextPageButton.notifyListeners(SWT.Selection, new Event());
                }
            }
        };

        this.previousPageButton.addKeyListener(keyAdapter);
        this.nextPageButton.addKeyListener(keyAdapter);

        this.shell.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event e) {
                final Rectangle mainWindowRectangle = shell.getClientArea();

                // Search results
                searchResultsComposite.setSize(mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING
                        - searchResultsComposite.getLocation().x, searchResultsComposite.getSize().y);

                previousPageButton.setLocation(mainWindowRectangle.width
                        - (2 * MainWindow.RIGHT_SIDE_PADDING + 3 * MainWindow.PAGING_MIDDLE_MARGIN)
                        - documentsCountLabel.getSize().x - nextPageButton.getSize().x - paginationLabel.getSize().x
                        - (searchResultsComposite.getLocation().x + previousPageButton.getSize().x),
                        previousPageButton.getLocation().y);

                paginationLabel.setLocation(
                        mainWindowRectangle.width
                                - (2 * MainWindow.RIGHT_SIDE_PADDING + 2 * MainWindow.PAGING_MIDDLE_MARGIN)
                                - documentsCountLabel.getSize().x - nextPageButton.getSize().x
                                - (searchResultsComposite.getLocation().x + paginationLabel.getSize().x),
                        paginationLabel.getLocation().y);

                nextPageButton.setLocation(
                        mainWindowRectangle.width
                                - (2 * MainWindow.RIGHT_SIDE_PADDING + MainWindow.PAGING_MIDDLE_MARGIN)
                                - documentsCountLabel.getSize().x
                                - (searchResultsComposite.getLocation().x + nextPageButton.getSize().x),
                        nextPageButton.getLocation().y);

                documentsCountLabel.setLocation(mainWindowRectangle.width - 2 * MainWindow.RIGHT_SIDE_PADDING
                        - (searchResultsComposite.getLocation().x + documentsCountLabel.getSize().x),
                        documentsCountLabel.getLocation().y);
            }
        });
    }
}
