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
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class RemoveAttributeWindow extends BaseAttributeWindow {

    private final Attribute attribute;

    public RemoveAttributeWindow(final Composite profileAttributeComposite, final Profile profile,
            final Attribute attribute) {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL), profile,
                profileAttributeComposite);
        this.shell.setSize(732, 150);
        this.shell.setText(messageSourceAdapter.getProperty("remove.attribute.window.title", attribute.getName()));
        UIControlUtils.centerWindowPosition(shell);
        UIControlUtils.applyDefaultBackground(shell);
        this.attribute = attribute;
    }

    protected void createContents() {
        final Composite labelComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(labelComposite);
        labelComposite.setBounds(10, 10, 708, 64);

        final Label warningLabel = new Label(labelComposite, SWT.WRAP | SWT.SHADOW_NONE | SWT.CENTER);
        warningLabel.setBounds(10, 10, 688, 44);
        warningLabel.setText(messageSourceAdapter.getProperty("remove.attribute.window.information",
                attribute.getName()));
        UIControlUtils.applyDefaultBackground(warningLabel);
        UIControlUtils.applyDefaultBoldedFont(warningLabel);

        final SquareButton removeAttributeButton = SquareButtonFactory.getWhiteButton(shell);
        removeAttributeButton.setBounds(10, 80, 170, 32);
        removeAttributeButton.setText(I18NResources.getProperty("remove.attribute.window.button"));
        removeAttributeButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));

        final SquareButton cancelButton = SquareButtonFactory.getWhiteButton(shell);
        cancelButton.setBounds(638, 80, 80, 32);
        cancelButton.setText(I18NResources.getProperty("cancel.button.label"));
        UIControlUtils.addCloseAction(shell, cancelButton);

        final Label passwordLabel = new Label(shell, SWT.NONE);
        passwordLabel.setBounds(190, 85, 190, 28);
        passwordLabel.setText(I18NResources.getProperty("retype.password.label"));
        UIControlUtils.applyDefaultBackground(passwordLabel);
        UIControlUtils.applyDefaultFont(passwordLabel);

        final Text passwordText = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        passwordText.setBounds(385, 80, 220, 28);
        UIControlUtils.applyDefaultBackground(passwordText);
        UIControlUtils.applyDefaultFont(passwordText);
        UIControlUtils.addWindowCloseOnEscOrNotifyButton(shell, removeAttributeButton, passwordText);

        removeAttributeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (passwordText.getText().isEmpty()) {
                    UIControlUtils.createErrorMessageBox(shell, I18NResources.getProperty("empty.password"));
                    return;
                }
                if (!userAuthenticationContext.checkPassword(passwordText.getText())) {
                    UIControlUtils.createErrorMessageBox(shell, I18NResources.getProperty("wrong.password"));
                    return;
                }

                if (!attributeService.removeAttributeByAttributeIdAndAttributeName(attribute.getId(),
                        attribute.getName(), getProfile().getId())) {
                    UIControlUtils.createErrorMessageBox(shell,
                            messageSourceAdapter.getProperty("remove.attribute.window.removal.error",
                                    attribute.getName()));
                }

                refreshProfileDataAttributesUI();
                cancelButton.notifyListeners(SWT.Selection, new Event());
            }
        });

        getTabList().add(0, passwordText);
        getTabList().add(1, removeAttributeButton);
        getTabList().add(2, cancelButton);
        this.shell.setTabList(getTabList().toArray(new Control[0]));
    }
}
