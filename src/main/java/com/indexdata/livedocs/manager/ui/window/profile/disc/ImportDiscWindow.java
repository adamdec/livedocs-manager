package com.indexdata.livedocs.manager.ui.window.profile.disc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.AesBase64Utils;
import com.indexdata.livedocs.manager.core.utils.DiscFileUtils;
import com.indexdata.livedocs.manager.core.utils.DomainUtils;
import com.indexdata.livedocs.manager.core.utils.FileSizeFormatter;
import com.indexdata.livedocs.manager.core.utils.JaxbUtils;
import com.indexdata.livedocs.manager.core.utils.KeyProvider;
import com.indexdata.livedocs.manager.repository.domain.Field;
import com.indexdata.livedocs.manager.repository.utils.filter.QueryFilter.FilterClassTypeEnum;
import com.indexdata.livedocs.manager.service.DiscService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.model.jaxb.Indexed;
import com.indexdata.livedocs.manager.ui.common.AbstractWindow;
import com.indexdata.livedocs.manager.ui.common.components.FileOpenDialog;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.components.progress.InfiniteProgressPanel;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ImportDiscWindow extends AbstractWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(ImportDiscWindow.class);

    public static final int MAPPING_LIMIT = 10;

    @Autowired
    private JaxbUtils jaxbUtils;

    @Autowired
    private DiscService discService;

    private final Profile profile;
    private final ScrolledComposite profileDetailsDiscsComposite;
    private final AtomicBoolean interruptionFlag = new AtomicBoolean(false);
    private final AtomicBoolean isDiscImported = new AtomicBoolean(false);
    private final AtomicBoolean discTitleEnabled = new AtomicBoolean(false);
    private final AtomicBoolean batchNumberEnabled = new AtomicBoolean(false);
    private final List<Control> tabList = new ArrayList<Control>(4);

    private Composite attributesMappingComposite;
    private ScrolledComposite attributesMappingScrolledComposite;
    private Text discTitleText, batchNumberText, documentsNumberText, selectSourceText;
    private ProgressBar importProgressBar;
    private Label importProgressLabel, requiredDiskSpaceLabel, availableDiscSpaceLabel;
    private SquareButton closeAndCancelButton, importDiscButton, browseFileButton;
    private List<CCombo> profileAttributesComboList;
    private File discDataFile;
    private Indexed indexedDisc;
    private long availableHddSpace;

    public ImportDiscWindow(final Shell detailsProfileWindow, final ScrolledComposite profileDetailsDiscsComposite,
            final Profile profile) {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL));
        this.profile = profile;
        this.profileDetailsDiscsComposite = profileDetailsDiscsComposite;
        this.shell.setSize(780, 460);
        this.shell.setText(messageSourceAdapter.getProperty("import.disc.window.title", profile.getName()));
        UIControlUtils.centerWindowPosition(shell);
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.applyDefaultFont(shell);
    }

    protected void createContents() {
        final Composite selectSourceComposite = new Composite(shell, SWT.NONE);
        selectSourceComposite.setBounds(10, 10, 754, 39);
        UIControlUtils.applyDefaultBackground(selectSourceComposite);

        final Label selectSourceLabel = new Label(selectSourceComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(selectSourceLabel);
        UIControlUtils.applyDefaultFont(selectSourceLabel);
        selectSourceLabel.setBounds(10, 10, 162, 21);
        selectSourceLabel.setText(I18NResources.getProperty("import.disc.window.select.source"));

        this.selectSourceText = new Text(selectSourceComposite, SWT.BORDER);
        this.selectSourceText.setBounds(178, 10, 475, 21);
        this.selectSourceText.setText(LiveDocsResourceManager.getCurrentApplicationDirectory());
        UIControlUtils.applyDefaultBackground(selectSourceText);
        UIControlUtils.applyDefaultFont(selectSourceText);

        this.browseFileButton = SquareButtonFactory.getWhiteButton(selectSourceComposite);
        this.browseFileButton.setBounds(658, 6, 90, 32);
        this.browseFileButton.setText(I18NResources.getProperty("browse.button.label"));
        this.browseFileButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                final FileOpenDialog fileOpenDialog = new FileOpenDialog(shell);
                final String openFile = fileOpenDialog.openLdpFiles();
                if (openFile == null) {
                    UIControlUtils.createErrorMessageBoxWithTitle(shell, "No file was selected.");
                    return;
                }

                try {
                    discDataFile = new File(openFile);
                    if (discDataFile.isFile()) {
                        final String encryptedXmlContent = FileUtils.readFileToString(discDataFile);
                        String decryptedXmlContent = "";
                        try {
                            decryptedXmlContent = new AesBase64Utils(KeyProvider.ENCRYPTION_KEY, 128)
                                    .decrypt(encryptedXmlContent);
                        } catch (Exception e) {
                            LOGGER.error("Could not decode XML file {}", openFile, e);
                            UIControlUtils.createErrorMessageBox(shell, messageSourceAdapter.getProperty(
                                    "import.disc.window.import.decode.file", openFile, e.getMessage()));
                            return;
                        }

                        indexedDisc = jaxbUtils.unmarshall(decryptedXmlContent);
                    }
                    else {
                        LOGGER.warn("Selected XML file is not a file {}", discDataFile);
                        UIControlUtils.createWarningMessageBox(shell,
                                messageSourceAdapter.getProperty("import.disc.window.import.not.a.file", discDataFile));
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not unmarshall XML file {}", openFile, e);
                    UIControlUtils.createErrorMessageBox(
                            shell,
                            messageSourceAdapter.getProperty("import.disc.window.import.unmarshall.file", openFile,
                                    e.getMessage()));
                    return;
                }

                // Setup UI
                importProgressBar.setEnabled(true);
                importProgressBar.setVisible(true);
                importProgressBar.setSelection(0);
                importProgressBar.setMinimum(0);
                importProgressLabel.setText("");

                final InfiniteProgressPanel progressPanel = UIControlUtils.createProgressPanel(
                        I18NResources.getProperty("process.index.data.progress"), shell);
                progressPanel.start();

                final Thread performer = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Display.getDefault().syncExec(
                                () -> {
                                    try {
                                        populateIndexedDiscData();
                                    } catch (Exception e) {
                                        LOGGER.error("Could not populate index data={}", openFile, e);
                                        UIControlUtils.createErrorMessageBox(shell, messageSourceAdapter.getProperty(
                                                "import.disc.window.import.data.population.error", openFile,
                                                e.getMessage()));
                                        return;
                                    }
                                    if (!selectSourceText.isDisposed()) {
                                        selectSourceText.setText(openFile);
                                    }
                                    progressPanel.getCanvas().dispose();
                                });
                        progressPanel.stop();
                    }
                }, "InfiniteProgressPanel");
                performer.start();
            }
        });

        this.importDiscButton = SquareButtonFactory.getWhiteButton(shell);
        this.importDiscButton.setBounds(20, 381, 130, 32);
        this.importDiscButton.setImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
        this.importDiscButton.setText(I18NResources.getProperty("import.disc.window.import.button"));
        this.importDiscButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (isDiscImported.get()) {
                    if ("".equals(discTitleText.getText())) {
                        UIControlUtils.createWarningMessageBox(shell, messageSourceAdapter.getProperty(
                                "import.disc.window.import.disc.empty", batchNumberText.getText()));
                        return;
                    }

                    if (discService.checkIdDiscExists(batchNumberText.getText())) {
                        UIControlUtils.createWarningMessageBox(shell, messageSourceAdapter.getProperty(
                                "import.disc.window.import.disc.exists", batchNumberText.getText()));
                        return;
                    }

                    final Map<String, Field> profileAttribbuteMappping = createDiscToProfileAttributeMapping();
                    if (profileAttribbuteMappping.size() > MAPPING_LIMIT) {
                        UIControlUtils.createWarningMessageBox(shell, messageSourceAdapter.getProperty(
                                "import.disc.window.import.disc.limit.warning", MAPPING_LIMIT));
                        return;
                    }

                    // Setup UI
                    interruptionFlag.set(false);
                    closeAndCancelButton.setText(I18NResources.getProperty("cancel.button.label"));
                    batchNumberText.setEnabled(false);
                    discTitleText.setEnabled(false);
                    importDiscButton.setEnabled(false);
                    browseFileButton.setEnabled(false);
                    documentsNumberText.setEnabled(false);
                    selectSourceText.setEnabled(false);
                    requiredDiskSpaceLabel.setEnabled(false);
                    availableDiscSpaceLabel.setEnabled(false);

                    for (CCombo combo : profileAttributesComboList) {
                        combo.setEnabled(false);
                    }

                    // Run disc import
                    Executors.newSingleThreadExecutor().execute(
                            new ImportDiscThread(new ImportDiscData(profile.getId(), batchNumberText.getText(),
                                    discTitleText.getText(), interruptionFlag, discDataFile, importProgressBar,
                                    importProgressLabel, indexedDisc, shell, profileDetailsDiscsComposite,
                                    closeAndCancelButton), profileAttribbuteMappping));
                }
                else {
                    UIControlUtils.createWarningMessageBox(UIControlsRepository.getInstance().getMainWindowShell(),
                            I18NResources.getProperty("import.disc.window.import.disc.warning"));
                }
            }
        });

        // Progress
        final Composite progressComposite = new Composite(shell, SWT.NONE);
        progressComposite.setBounds(156, 365, 502, 60);
        UIControlUtils.applyDefaultBackground(progressComposite);

        this.importProgressLabel = new Label(progressComposite, SWT.NONE);
        this.importProgressLabel.setBounds(10, 10, 442, 18);
        UIControlUtils.applyDefaultBackground(importProgressLabel);
        UIControlUtils.applyDefaultFont(importProgressLabel);

        this.importProgressBar = new ProgressBar(progressComposite, SWT.SMOOTH);
        this.importProgressBar.setEnabled(false);
        this.importProgressBar.setVisible(false);
        this.importProgressBar.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
        this.importProgressBar.setBounds(10, 31, 492, 23);

        this.closeAndCancelButton = SquareButtonFactory.getWhiteButton(shell);
        this.closeAndCancelButton.setBounds(674, 381, 80, 32);
        this.closeAndCancelButton.setText(I18NResources.getProperty("close.button.label"));
        this.closeAndCancelButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (I18NResources.getProperty("close.button.label").equals(closeAndCancelButton.getText())) {
                    shell.close();
                }
                else if (I18NResources.getProperty("cancel.button.label").equals(closeAndCancelButton.getText())) {
                    interruptionFlag.set(true);
                }
            }
        });

        // Main composite (attribute mapping)

        // Disc details
        final int discTitleWidth = 450;
        final Composite mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setBounds(10, 55, 754, 320);
        UIControlUtils.applyDefaultBackground(mainComposite);

        final ScrolledComposite discDetailsScrolledComposite = new ScrolledComposite(mainComposite, SWT.NONE);
        discDetailsScrolledComposite.setBounds(10, 10, 734, 45);
        discDetailsScrolledComposite.setExpandHorizontal(true);
        discDetailsScrolledComposite.setExpandVertical(true);

        final Composite discDetailsComposite = new Composite(discDetailsScrolledComposite, SWT.NONE);
        discDetailsComposite.setLayout(new GridLayout(3, false));
        UIControlUtils.applyDefaultBackground(discDetailsComposite);

        final Label discTitleLabel = new Label(discDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(discTitleLabel);
        UIControlUtils.applyDefaultBoldedFont(discTitleLabel);
        discTitleLabel.setText(I18NResources.getProperty("import.disc.window.disc.title.label"));
        final GridData discTitleLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        discTitleLabelData.widthHint = discTitleWidth;
        discTitleLabel.setLayoutData(discTitleLabelData);

        final Label batchNumberLabel = new Label(discDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(batchNumberLabel);
        UIControlUtils.applyDefaultBoldedFont(batchNumberLabel);
        batchNumberLabel.setText(I18NResources.getProperty("import.disc.window.batch.number.label"));
        batchNumberLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label noDocumentsLabel = new Label(discDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(noDocumentsLabel);
        UIControlUtils.applyDefaultBoldedFont(noDocumentsLabel);
        noDocumentsLabel.setText(I18NResources.getProperty("import.disc.window.no.documents.label"));
        noDocumentsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        this.discTitleText = new Text(discDetailsComposite, SWT.CENTER);
        this.discTitleText.setEditable(true);
        final GridData discTitleTextData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        discTitleTextData.widthHint = discTitleWidth;
        this.discTitleText.setLayoutData(discTitleTextData);
        UIControlUtils.applyDefaultBackground(discTitleText);
        UIControlUtils.applyDefaultFont(discTitleText);
        this.discTitleText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (!discTitleEnabled.get()) {
                    e.doit = false;
                }
            }
        });

        this.batchNumberText = new Text(discDetailsComposite, SWT.CENTER);
        this.batchNumberText.setEditable(true);
        this.batchNumberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UIControlUtils.applyDefaultBackground(batchNumberText);
        UIControlUtils.applyDefaultFont(batchNumberText);
        this.batchNumberText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (!batchNumberEnabled.get()) {
                    e.doit = false;
                }
            }
        });

        this.documentsNumberText = new Text(discDetailsComposite, SWT.CENTER);
        this.documentsNumberText.setEditable(false);
        this.documentsNumberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UIControlUtils.applyDefaultBackground(documentsNumberText);
        UIControlUtils.applyDefaultFont(documentsNumberText);

        discDetailsScrolledComposite.setContent(discDetailsComposite);
        discDetailsScrolledComposite.setMinSize(discDetailsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Disc mapping

        final ScrolledComposite discMappingScrolledComposite = new ScrolledComposite(mainComposite, SWT.NONE);
        discMappingScrolledComposite.setBounds(10, 61, 734, 30);
        discMappingScrolledComposite.setExpandHorizontal(true);
        discMappingScrolledComposite.setExpandVertical(true);

        final Composite discMappingComposite = new Composite(discMappingScrolledComposite, SWT.NONE);
        discMappingComposite.setLayout(new GridLayout(3, false));
        UIControlUtils.applyDefaultBackground(discMappingComposite);

        final Label attributesMappingTableLabel = new Label(discMappingComposite, SWT.NONE);
        attributesMappingTableLabel.setBounds(10, 61, 120, 26);
        attributesMappingTableLabel.setText(I18NResources.getProperty("import.disc.window.attr.mapping.label"));
        UIControlUtils.applyDefaultBackground(attributesMappingTableLabel);
        UIControlUtils.applyDefaultFont(attributesMappingTableLabel);

        final GridData attributesMappingTableLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        attributesMappingTableLabelData.widthHint = 120;
        attributesMappingTableLabel.setLayoutData(attributesMappingTableLabelData);

        this.availableDiscSpaceLabel = new Label(discMappingComposite, SWT.NONE);
        this.availableDiscSpaceLabel.setText(messageSourceAdapter.getProperty(
                "import.disc.window.available.disc.space.label", FileSizeFormatter.formatSize(0L)));
        this.availableDiscSpaceLabel.setBounds(140, 61, 140, 26);
        UIControlUtils.applyDefaultBackground(availableDiscSpaceLabel);
        UIControlUtils.applyDefaultFont(availableDiscSpaceLabel);

        final GridData availableDiscSpaceLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        availableDiscSpaceLabelData.widthHint = 140;
        availableDiscSpaceLabel.setLayoutData(availableDiscSpaceLabelData);

        this.requiredDiskSpaceLabel = new Label(discMappingComposite, SWT.NONE);
        this.requiredDiskSpaceLabel.setText(messageSourceAdapter.getProperty(
                "import.disc.window.required.disc.space.label", FileSizeFormatter.formatSize(0L)));
        this.requiredDiskSpaceLabel.setBounds(290, 61, 140, 26);
        UIControlUtils.applyDefaultBackground(requiredDiskSpaceLabel);
        UIControlUtils.applyDefaultFont(requiredDiskSpaceLabel);

        final GridData requiredDiskSpaceLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        requiredDiskSpaceLabelData.widthHint = 140;
        requiredDiskSpaceLabel.setLayoutData(requiredDiskSpaceLabelData);

        discMappingScrolledComposite.setContent(discMappingComposite);
        discMappingScrolledComposite.setMinSize(discMappingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        this.attributesMappingScrolledComposite = new ScrolledComposite(mainComposite, SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);
        this.attributesMappingScrolledComposite.setBounds(10, 93, 734, 217);
        this.attributesMappingScrolledComposite.setExpandHorizontal(true);
        this.attributesMappingScrolledComposite.setExpandVertical(true);
        UIControlUtils.applyDefaultBackground(attributesMappingScrolledComposite);

        this.attributesMappingComposite = new Composite(attributesMappingScrolledComposite, SWT.NONE);
        attributesMappingComposite.setLayout(new GridLayout(3, false));
        UIControlUtils.applyDefaultBackground(attributesMappingComposite);

        this.tabList.add(0, selectSourceComposite);
        this.tabList.add(1, mainComposite);
        this.tabList.add(2, importDiscButton);
        this.tabList.add(3, closeAndCancelButton);

        this.shell.setTabList(tabList.toArray(new Control[0]));
    }

    private Map<String, Field> createDiscToProfileAttributeMapping() {
        final Map<String, Field> discToProfileAttrMap = new HashMap<String, Field>(MAPPING_LIMIT);
        for (CCombo profileAttributeCombo : profileAttributesComboList) {
            final String profileAttributeComboValue = profileAttributeCombo.getText();
            if (I18NResources.getProperty("import.disc.window.do.not.import").equals(profileAttributeComboValue)) {
                LOGGER.warn("Disc field [{}] not mapped", profileAttributeCombo.getData());
                continue;
            }
            final Field field = new Field(profileAttributeComboValue, profileAttributeComboValue,
                    FilterClassTypeEnum.STRING);
            discToProfileAttrMap.put((String) profileAttributeCombo.getData(), field);
            LOGGER.debug("Disc field [{}] mapped to combo value [{}]", profileAttributeCombo.getData(), field);
        }
        LOGGER.debug("Created disc to profile mapping of size [{}] and values:\n{}", discToProfileAttrMap.size(),
                discToProfileAttrMap);
        return discToProfileAttrMap;
    }

    private void populateIndexedDiscData() throws Exception {
        UIControlUtils.disposeChildren(attributesMappingComposite);
        this.profileAttributesComboList = new ArrayList<CCombo>(indexedDisc.getHeader().getField().size());
        for (String field : indexedDisc.getHeader().getField()) {
            final Label discAttribute = new Label(attributesMappingComposite, SWT.NONE);
            discAttribute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            discAttribute.setText(field);
            UIControlUtils.applyDefaultBackground(discAttribute);
            UIControlUtils.applyDefaultFont(discAttribute);

            final Label label = new Label(attributesMappingComposite, SWT.NONE);
            label.setText(I18NResources.getProperty("import.disc.window.mapping"));
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            UIControlUtils.applyDefaultBackground(label);
            UIControlUtils.applyDefaultBoldedFont(label);

            final CCombo profileAttributesCombo = new CCombo(attributesMappingComposite, SWT.NONE);
            profileAttributesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            profileAttributesCombo.setEditable(false);
            profileAttributesCombo.setItems(DomainUtils.convertAttributes(profile.getAttributes()));
            profileAttributesCombo.setText(I18NResources.getProperty("import.disc.window.do.not.import"));
            profileAttributesCombo.setData(discAttribute.getText());
            UIControlUtils.applyDefaultBackground(profileAttributesCombo);
            UIControlUtils.applyDefaultFont(profileAttributesCombo);
            UIControlUtils.matchAttributesMapping(discAttribute.getText(), profileAttributesCombo);
            profileAttributesCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    final CCombo selectedCOmbo = (CCombo) e.getSource();
                    if (profileAttributesComboList.isEmpty()) {
                        return;
                    }

                    for (CCombo combo : profileAttributesComboList) {
                        if (!selectedCOmbo.equals(combo) && selectedCOmbo.getText().equals(combo.getText())) {
                            combo.setText(I18NResources.getProperty("import.disc.window.do.not.import"));
                        }
                    }
                }
            });

            profileAttributesComboList.add(profileAttributesCombo);
        }
        attributesMappingScrolledComposite.setContent(attributesMappingComposite);
        attributesMappingScrolledComposite.setMinSize(attributesMappingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        long totalDiskFileSpace = DiscFileUtils.calculateDiscSize(new File(FilenameUtils.getFullPath(discDataFile
                .getPath())));
        int doNotExist = 0;
        for (com.indexdata.livedocs.manager.service.model.jaxb.Indexed.Data.File file : indexedDisc.getData().getFile()) {
            if (!new File(FilenameUtils.getFullPath(discDataFile.getPath()), file.getPath()).exists()) {
                LOGGER.warn("File [{}] could not be loaded as it does not exist in selected directory={}",
                        file.getPath(), FilenameUtils.getFullPath(discDataFile.getPath()));
                doNotExist++;
            }
        }
        importProgressBar.setMaximum(indexedDisc.getData().getFile().size() - doNotExist);

        try {
            availableHddSpace = DiscFileUtils.getFreeSpaceOnMappedDrive();
            LOGGER.debug("Calculated free disk space on mapped drive={}", availableHddSpace);
        } catch (Exception e) {
            final StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Could not get available space on remote mapped drive ["
                    + LiveDocsResourceManager.getSambaServer() + "] for login="
                    + LiveDocsResourceManager.getSambaLogin() + " due to error=" + e.getMessage());
            if (e.getMessage().contains("The network name")) {
                errorMessage.append(" Please check if " + ManagerConfiguration.getAppMainRemoteDirectory()
                        + " directory was created on remote machine.");
            } else {
                errorMessage.append(" Please check if " + LiveDocsResourceManager.getSambaLogin()
                        + " user has access to " + ManagerConfiguration.getAppMainRemoteDirectory()
                        + " directory on remote machine.");
            }

            LOGGER.error(errorMessage.toString());
            UIControlUtils.createErrorMessageBox(shell, errorMessage.toString());
            shell.dispose();
            return;
        }

        if (availableHddSpace < totalDiskFileSpace) {
            availableDiscSpaceLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
        }
        availableDiscSpaceLabel.setText(messageSourceAdapter.getProperty(
                "import.disc.window.available.disc.space.label", FileSizeFormatter.formatSize(availableHddSpace)));

        requiredDiskSpaceLabel.setText(messageSourceAdapter.getProperty("import.disc.window.required.disc.space.label",
                FileSizeFormatter.formatSize(totalDiskFileSpace)));
        discTitleText.setText(indexedDisc.getTitle());
        batchNumberText.setText(indexedDisc.getBatchnumber());
        documentsNumberText.setText(String.valueOf(indexedDisc.getData().getFile().size()));
        discTitleEnabled.set(true);
        batchNumberEnabled.set(true);
        isDiscImported.set(true);
    }
}