package com.indexdata.livedocs.manager.ui.window.main.elements.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;
import com.indexdata.livedocs.manager.ui.window.profile.ManageProfileWindow;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
@Deprecated
public final class ManageProfilesButton {

    private final Shell mainWindow;
    private final Composite actionsComposite;

    public ManageProfilesButton(final Shell mainWindow, final Composite actionsComposite) {
        this.mainWindow = mainWindow;
        this.actionsComposite = actionsComposite;
    }

    public SquareButton createButton() {
        final SquareButton button = SquareButtonFactory.getWhiteButton(actionsComposite, 5, 14);
        button.setImage(LiveDocsResourceManager.getImage(Resources.MANAGE_PROFILES_20_20.getValue()));
        button.setBounds(459, 10, 140, 32);
        button.setText(I18NResources.getProperty("main.window.manage.profiles.button"));
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                new ManageProfileWindow().open();
            }
        });

        mainWindow.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event e) {
                final Rectangle rect = mainWindow.getClientArea();

                // Horizontal scaling

                button.setLocation(rect.width - MainWindow.RIGHT_SIDE_PADDING
                        - (actionsComposite.getLocation().x + button.getSize().x),
                        button.getLocation().y);
            }
        });
        return button;
    }
}