package com.indexdata.livedocs.manager.ui.window.profile.disc;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.Immutable;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.indexdata.livedocs.manager.service.model.jaxb.Indexed;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;

@Immutable
public class ImportDiscData {

    private final long profileId;
    private final String batchNumber;
    private final String discTitle;
    private final AtomicBoolean interruptionFlag;
    private final File discDataFile;
    private final ProgressBar importProgressBar;
    private final Label importProgressLabel;
    private final Indexed indexedDisc;
    private final Shell shell;
    private final ScrolledComposite profileDetailsDiscsComposite;
    private final SquareButton closeAndCancelButton;

    public ImportDiscData(long profileId, String batchNumber, String discTitle, AtomicBoolean interruptionFlag,
            File discDataFile, ProgressBar importProgressBar, Label importProgressLabel, Indexed indexedDisc,
            final Shell shell, final ScrolledComposite profileDetailsDiscsComposite,
            final SquareButton closeAndCancelButton) {
        super();
        this.profileId = profileId;
        this.batchNumber = batchNumber;
        this.discTitle = discTitle;
        this.interruptionFlag = interruptionFlag;
        this.discDataFile = discDataFile;
        this.importProgressBar = importProgressBar;
        this.importProgressLabel = importProgressLabel;
        this.indexedDisc = indexedDisc;
        this.shell = shell;
        this.profileDetailsDiscsComposite = profileDetailsDiscsComposite;
        this.closeAndCancelButton = closeAndCancelButton;
    }

    public long getProfileId() {
        return profileId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getDiscTitle() {
        return discTitle;
    }

    public AtomicBoolean getInterruptionFlag() {
        return interruptionFlag;
    }

    public File getDiscDataFile() {
        return discDataFile;
    }

    public ProgressBar getImportProgressBar() {
        return importProgressBar;
    }

    public Label getImportProgressLabel() {
        return importProgressLabel;
    }

    public Indexed getIndexedDisc() {
        return indexedDisc;
    }

    public Shell getShell() {
        return shell;
    }

    public ScrolledComposite getProfileDetailsDiscsComposite() {
        return profileDetailsDiscsComposite;
    }

    public SquareButton getCloseAndCancelButton() {
        return closeAndCancelButton;
    }
}