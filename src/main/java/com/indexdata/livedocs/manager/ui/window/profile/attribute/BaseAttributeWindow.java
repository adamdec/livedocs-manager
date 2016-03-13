package com.indexdata.livedocs.manager.ui.window.profile.attribute;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.service.AttributeService;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractWindow;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.TableFileQueryData;

public abstract class BaseAttributeWindow extends AccessAbstractWindow {

    @Autowired
    protected AttributeService attributeService;

    @Autowired
    protected ProfileService profileService;

    protected final Profile profile;
    protected final Composite profileAttributeComposite;
    private final List<Control> tabList = new ArrayList<Control>(3);

    public BaseAttributeWindow(final Shell shell, final Profile profile, final Composite profileAttributeComposite) {
        super(shell);
        this.profile = profile;
        this.profileAttributeComposite = profileAttributeComposite;
    }

    protected void refreshProfileDataAttributesUI() {
        UIControlUtils.refreshControl(profileAttributeComposite, AppEvents.REFRESH_ATTRIBUTES);
        UIControlsRepository.getInstance().sendRefreshProfilesEventToDocumentProfilesComposite();
        if (UIControlsRepository.getInstance().getCurrentProfileNameFromCombo().equals(profile.getName())) {
            UIControlsRepository.getInstance().sendRefreshAttributesEventToProfileAttributesGroupAdapter(
                    profile.getId());
            UIControlsRepository.getInstance().sendRefreshAttributesEventToScrolledCompositeDiscTable(
                    new TableFileQueryData(profile));
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public List<Control> getTabList() {
        return tabList;
    }
}
