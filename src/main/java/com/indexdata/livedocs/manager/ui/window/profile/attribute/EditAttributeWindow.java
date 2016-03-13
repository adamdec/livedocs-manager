package com.indexdata.livedocs.manager.ui.window.profile.attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.ValidationUtils;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * This class will add amend attribute in a given profile in database.
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class EditAttributeWindow extends BaseAttributeWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(EditAttributeWindow.class);

    private final Attribute attribute;

    public EditAttributeWindow(final Composite profileAttributeComposite, final Profile profile,
            final Attribute attribute) {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL), profile,
                profileAttributeComposite);
        this.shell.setSize(450, 130);
        this.shell.setText(messageSourceAdapter.getProperty("edit.attribute.window.title", attribute.getName()));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
        this.attribute = attribute;
    }

    protected void createContents() {
        final Label attributeNameLabel = new Label(shell, SWT.NONE);
        attributeNameLabel.setBounds(10, 10, 107, 21);
        attributeNameLabel.setText(I18NResources.getProperty("edit.attribute.window.edit.attribute.label"));
        UIControlUtils.applyDefaultBackground(attributeNameLabel);
        UIControlUtils.applyDefaultFont(attributeNameLabel);

        final Text attributeNameText = new Text(shell, SWT.BORDER | SWT.CENTER);
        attributeNameText.setText(attribute.getName());
        attributeNameText.setBounds(10, 37, 424, 21);
        UIControlUtils.applyDefaultBackground(attributeNameText);
        UIControlUtils.applyDefaultFont(attributeNameText);

        final SquareButton closeButton = SquareButtonFactory.getWhiteButton(shell);
        closeButton.setBounds(354, 64, 80, 32);
        closeButton.setText(I18NResources.getProperty("close.button.label"));
        UIControlUtils.addCloseAction(shell, closeButton);

        final SquareButton saveAttributeButton = SquareButtonFactory.getWhiteButton(shell);
        saveAttributeButton.setBounds(10, 64, 150, 32);
        saveAttributeButton.setText(I18NResources.getProperty("edit.attribute.window.save.attribute.button"));
        saveAttributeButton.setImage(LiveDocsResourceManager.getImage(Resources.SAVE_BUTTON.getValue()));
        saveAttributeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!ValidationUtils.validateAttributeTextBox(shell, attributeNameText)) {
                    return;
                }

                attributeService.changeAttributeNameByAttributeNameAndProfileId(attributeNameText.getText(),
                        attribute.getId(), getProfile().getId());
                LOGGER.debug("Edited attribute [{}] with id [{}] in profile [{}]", attributeNameText.getText(),
                        attribute.getId(), getProfile().getName());

                refreshProfileDataAttributesUI();
                closeButton.notifyListeners(SWT.Selection, new Event());
            }
        });

        getTabList().add(0, attributeNameText);
        getTabList().add(1, saveAttributeButton);
        getTabList().add(2, closeButton);
        this.shell.setTabList(getTabList().toArray(new Control[0]));
    }
}
