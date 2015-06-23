//
// Kit's Java Utils.
//

package com.github.ewbankkit.log4j2;

import org.apache.logging.log4j.core.appender.rolling.AbstractRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverDescription;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.Deflater;

/**
 * Custom RolloverStrategy for Log4j2 to delete old files.
 */
@Plugin(name = "C1DeleteMaxAgeFilesStrategy", category = "Core", printObject = true)
public final class C1DeleteMaxAgeFilesStrategy extends AbstractRolloverStrategy {
    private static final int DEFAULT_MAX_AGE = 14;

    private final RolloverStrategy delegate;
    private final int              maxAge;

    private C1DeleteMaxAgeFilesStrategy(RolloverStrategy delegate, int maxAge) {
        this.delegate = delegate;
        this.maxAge = maxAge;
    }

    /**
     * Prepare for a rollover.
     * This method is called prior to closing the active log file, performs any necessary preliminary actions and describes actions needed after close of current log file.
     */
    @Override
    public RolloverDescription rollover(RollingFileManager manager) throws SecurityException {
        purgeMaxAgeFiles(new File(manager.getFileName()));
        return delegate.rollover(manager);
    }

    @PluginFactory
    @SuppressWarnings("UnusedDeclaration")
    public static C1DeleteMaxAgeFilesStrategy createStrategy(
        @PluginAttribute(value = "maxAge", defaultInt = DEFAULT_MAX_AGE)
        final int maxAge,
        @PluginConfiguration
        final Configuration config
    ) {
        if (maxAge <= 0) {
            LOGGER.warn("Invalid maxAge attribute: {}", maxAge);
            return null;
        }

        // Delegate to the default rollover strategy.
        RolloverStrategy delegate = DefaultRolloverStrategy.createStrategy(
            null,
            null,
            null,
            String.valueOf(Deflater.DEFAULT_COMPRESSION),
            config
        );
        return new C1DeleteMaxAgeFilesStrategy(delegate, maxAge);
    }

    /**
     * Purge files older than defined maxAge. If file older than current date - maxAge delete them or else keep it.
     */
    private void purgeMaxAgeFiles(File currentLogFile) {
        File parentFile = currentLogFile.getParentFile();
        if (parentFile.exists()) {
            final String absolutePath = currentLogFile.getAbsolutePath();
            final int dotIndex = absolutePath.lastIndexOf('.');
            final String prefix = (dotIndex == -1) ? absolutePath : absolutePath.substring(0, dotIndex);

            // Get files that match the prefix.
            File[] matchingFiles = parentFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (!f.isFile()) {
                        return false;
                    }
                    String ap = f.getAbsolutePath();
                    // Ignore the current log file.
                    if (ap.equals(absolutePath)) {
                        return false;
                    }
                    return ap.startsWith(prefix);
                }
            });

            LocalDate cutOffDate = LocalDate.now().minusDays(maxAge);
            for (File matchingFile : matchingFiles) {
                try {
                    BasicFileAttributes bfa = Files.readAttributes(matchingFile.toPath(), BasicFileAttributes.class);
                    LocalDate creationDate = new LocalDate(bfa.creationTime().toMillis());
                    if (creationDate.isBefore(cutOffDate)) {
                        LOGGER.info("Deleting {}", matchingFile);
                        matchingFile.delete();
                    }
                    else {
                        LOGGER.info("Skipping {}", matchingFile);
                    }
                }
                catch (IOException ex) {
                    LOGGER.error("Unable to delete old log file at rollover", ex);
                }
            }
        }
    }
}
