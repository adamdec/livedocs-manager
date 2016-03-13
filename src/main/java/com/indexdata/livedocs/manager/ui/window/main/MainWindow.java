package com.indexdata.livedocs.manager.ui.window.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.wb.swt.SWTResourceManager;
import org.mihalis.opal.panels.DarkPanel;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.DisplayInitializer;
import com.indexdata.livedocs.manager.core.SpringInitializer;
import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.ui.common.AbstractWindow;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.ClientImageLabel;
import com.indexdata.livedocs.manager.ui.window.main.elements.LiveDocsImageLabel;
import com.indexdata.livedocs.manager.ui.window.main.elements.MainToolbar;

/**
 * Layouts: <br/>
 * http://www.eclipse.org/articles/article.php?file=Article-Understanding -Layouts/index.html <br/>
 * https://gist.github.com/fappel/9168399
 *
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class MainWindow extends AbstractWindow {

    public static final int MAIN_WINDOW_X_LOCATION = 0;
    public static final int MAIN_WINDOW_Y_LOCATION = 0;
    public static final int MAIN_WINDOW_HEIGHT = 700;
    public static final int MAIN_WINDOW_WIDTH = 900;

    public static final int FOOTER_HEIGHT = 16;
    public static final int TOOLBAR_HEIGHT = 26;
    public static final int TOOLBAR_Y_LOCATION = 105;
    public static final int CONTENT_Y_LOCATION = 135;
    public static final int LEFT_SIDE_PADDING = 10;
    public static final int RIGHT_SIDE_PADDING = 10;
    public static final int BOTTOM_SIDE_PADDING = 10;
    public static final int PAGING_MIDDLE_MARGIN = 6;
    public static final int VERTICAL_MARGIN = 6;

    private ToolBar toolBar;
    private Label clientLogo, versionLabel, dateTimeLabel;
    private Composite mainComposite, usersComposite, footerComposite;
    private final DarkPanel darkPanel;
    private final DisplayInitializer displayInitializer;
    private final SpringInitializer springInitializer;

    public MainWindow(final SpringInitializer springInitializer, final DisplayInitializer displayInitializer) {
        super(new Shell(Display.getDefault()));
        this.shell.setImage(LiveDocsResourceManager.getImage(Resources.ICON.getValue()));
        this.shell.setBounds(MAIN_WINDOW_X_LOCATION, MAIN_WINDOW_Y_LOCATION, MAIN_WINDOW_WIDTH,
                MAIN_WINDOW_HEIGHT);
        this.shell.setMinimumSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        this.shell.setText(ManagerConfiguration.getAppName());
        this.shell.setMaximized(true);
        this.displayInitializer = displayInitializer;
        this.springInitializer = springInitializer;

        UIControlsRepository.getInstance().setMainWindowShell(shell);
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.applyDefaultFont(shell);
        UIControlUtils.addDisposeAction(shell, displayInitializer);
        this.darkPanel = new DarkPanel(shell);
        this.darkPanel.setAlpha(90);
    }

    protected void createContents() {
        // Logos
        LiveDocsImageLabel.createLabel(shell);
        this.clientLogo = ClientImageLabel.createLabel(shell);

        // Footer
        this.footerComposite = new Composite(shell, SWT.NONE);
        this.footerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
        this.footerComposite.setSize(MAIN_WINDOW_WIDTH, FOOTER_HEIGHT);
        this.footerComposite.setLocation(0, MAIN_WINDOW_HEIGHT - footerComposite.getSize().y);

        this.versionLabel = new Label(footerComposite, SWT.NONE);
        this.versionLabel.setAlignment(SWT.CENTER);
        this.versionLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        this.versionLabel.setBounds(0, 2, 140, 15);
        this.versionLabel.setText(I18NResources.getProperty("footer.version") + ManagerConfiguration.getAppVersion());
        UIControlUtils.applyFooterStyle(versionLabel);

        this.dateTimeLabel = new Label(footerComposite, SWT.NONE);
        this.dateTimeLabel.setAlignment(SWT.RIGHT);
        this.dateTimeLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        this.dateTimeLabel.setBounds(672, 2, 134, 15);
        UIControlUtils.applyFooterStyle(dateTimeLabel);
        UIControlUtils.updateTime(dateTimeLabel);

        // Main content
        this.mainComposite = new Composite(shell, SWT.NONE);
        this.mainComposite.setLocation(0, CONTENT_Y_LOCATION);
        this.mainComposite.setSize(shell.getClientArea().width,
                MAIN_WINDOW_HEIGHT - mainComposite.getLocation().y - footerComposite.getSize().y);
        UIControlUtils.applyDefaultBackground(mainComposite);

        this.usersComposite = new Composite(shell, SWT.NONE);
        this.usersComposite.setLocation(0, CONTENT_Y_LOCATION);
        this.usersComposite.setSize(shell.getClientArea().width,
                MAIN_WINDOW_HEIGHT - usersComposite.getLocation().y - footerComposite.getSize().y);
        UIControlUtils.applyDefaultBackground(usersComposite);

        // Toolbar
        this.toolBar = new MainToolbar(shell, mainComposite, usersComposite, footerComposite, displayInitializer,
                springInitializer)
                .createToolBar();

        registerMainWindowListeners();
    }

    private void registerMainWindowListeners() {
        this.shell.addListener(AppEvents.DARK_PANEL_SHOW.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                darkPanel.show();
            }
        });

        this.shell.addListener(AppEvents.DARK_PANEL_HIDE.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                darkPanel.hide();
            }
        });

        this.shell.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event e) {
                final Rectangle mainWindowRectangle = shell.getClientArea();

                mainComposite.setSize(
                        mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING - mainComposite.getLocation().x,
                        mainWindowRectangle.height - mainComposite.getLocation().y - footerComposite.getSize().y);

                usersComposite.setSize(
                        mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING - usersComposite.getLocation().x,
                        mainWindowRectangle.height - usersComposite.getLocation().y - footerComposite.getSize().y);

                // Horizontal scaling
                clientLogo.setLocation(mainWindowRectangle.width - RIGHT_SIDE_PADDING - clientLogo.getSize().x,
                        clientLogo.getLocation().y);

                toolBar.setSize(mainWindowRectangle.width - toolBar.getLocation().x, toolBar.getSize().y);

                // Footer
                footerComposite.setSize(mainWindowRectangle.width, footerComposite.getSize().y);

                dateTimeLabel.setLocation(
                        mainWindowRectangle.width - RIGHT_SIDE_PADDING
                                - (footerComposite.getLocation().x + dateTimeLabel.getSize().x),
                        dateTimeLabel.getLocation().y);

                // Vertical scaling
                footerComposite.setLocation(0, mainWindowRectangle.height - footerComposite.getSize().y);
            }
        });
    }

    public static void main(String[] args) {
        try {
            final MainWindow window = new MainWindow(null, null);
            window.open(true);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}