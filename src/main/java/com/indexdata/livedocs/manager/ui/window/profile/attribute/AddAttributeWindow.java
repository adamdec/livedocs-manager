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
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.ValidationUtils;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * This class will add new attribute in a given profile in database.
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class AddAttributeWindow extends BaseAttributeWindow {

    public AddAttributeWindow(final Composite profileAttributeComposite, final Profile profile) {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL), profile,
                profileAttributeComposite);
        this.shell.setSize(500, 130);
        this.shell.setText(messageSourceAdapter.getProperty("add.attribute.window.title", profile.getName()));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
    }

    protected void createContents() {
        final Label attributeNameLabel = new Label(shell, SWT.NONE);
        attributeNameLabel.setBounds(10, 10, 112, 21);
        attributeNameLabel.setText(I18NResources.getProperty("add.attribute.window.attribute.name.label"));
        UIControlUtils.applyDefaultBackground(attributeNameLabel);
        UIControlUtils.applyDefaultFont(attributeNameLabel);

        final Text attributeNameText = new Text(shell, SWT.BORDER | SWT.CENTER);
        attributeNameText.setText(I18NResources.getProperty("add.attribute.window.attribute.name.text"));
        attributeNameText.setBounds(10, 37, 474, 21);
        UIControlUtils.cleanTextOnFocus(attributeNameText);
        UIControlUtils.applyDefaultBackground(attributeNameText);
        UIControlUtils.applyDefaultFont(attributeNameText);
        UIControlUtils.addDefaultValueKeyListener(attributeNameText);

        final SquareButton closeButton = SquareButtonFactory.getWhiteButton(shell);
        closeButton.setBounds(404, 64, 80, 32);
        closeButton.setText(I18NResources.getProperty("close.button.label"));
        UIControlUtils.addCloseAction(shell, closeButton);

        final SquareButton createAttributeButton = SquareButtonFactory.getWhiteButton(shell);
        createAttributeButton.setBounds(10, 64, 160, 32);
        createAttributeButton.setText(I18NResources.getProperty("add.attribute.window.create.button.label"));
        createAttributeButton.setImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
        createAttributeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!ValidationUtils.validateAttributeTextBox(shell, attributeNameText)) {
                    return;
                }
                profileService.addAttribute(getProfile().getId(), attributeNameText.getText());
                refreshProfileDataAttributesUI();
                closeButton.notifyListeners(SWT.Selection, new Event());
            }
        });
        UIControlUtils.addWindowCloseOnEscOrNotifyButton(shell, createAttributeButton, attributeNameText);

        getTabList().add(0, attributeNameText);
        getTabList().add(1, createAttributeButton);
        getTabList().add(2, closeButton);
        this.shell.setTabList(getTabList().toArray(new Control[0]));
    }
}