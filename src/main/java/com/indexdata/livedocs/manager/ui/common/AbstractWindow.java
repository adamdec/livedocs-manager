package com.indexdata.livedocs.manager.ui.common;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;

import com.indexdata.livedocs.manager.core.resources.MessageSourceAdapter;
import com.indexdata.livedocs.manager.service.DiscService;
import com.indexdata.livedocs.manager.service.ProfileService;

public abstract class AbstractWindow {

    @Autowired
    protected MessageSourceAdapter messageSourceAdapter;

    @Autowired
    protected ProfileService profileService;

    @Autowired
    protected DiscService discService;

    protected final Shell shell;

    public AbstractWindow(final Shell shell) {
        this.shell = shell;
    }

    protected abstract void createContents();

    public Shell getShell() {
        return shell;
    }

    public void open() {
        this.open(false);
    }

    public void open(boolean isStandalone) {
        createContents();
        shell.open();
        shell.layout();

        if (isStandalone) {
            final Display display = Display.getDefault();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        }
    }
}
