package com.indexdata.livedocs.manager.core.utils;

import java.io.IOException;

import com.izforge.izpack.util.AbstractUIProcessHandler;

/**
 * http://docs.codehaus.org/display/IZPACK/The+available+panels
 * https://code.google.com/p/wordpress-izpack-installer/
 * 
 * @author Adam Dec
 * @since 1.0.0
 */
public class IDProcessor {

    public void run(AbstractUIProcessHandler handler, String[] args) throws IOException {
        handler.logOutput("Checking live-docs manager key validity...", false);
        final String generatedKey = CustomIdCreator.generate();
        handler.logOutput("\nMachine Key:\n" + generatedKey.substring(0, 16).concat("..."), false);
        if (!KeyProvider.getClientKeys().contains(generatedKey)) {
            handler.logOutput("\nKey is invalid!", false);
            handler.emitErrorAndBlockNext("ERROR",
                    "Invalid key! Contact live-docs manager support (contact@livedocs.eu).");
            System.exit(-1);
        } else {
            handler.logOutput("\nKey is valid!", false);
        }
    }
}