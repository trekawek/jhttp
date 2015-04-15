package eu.rekawek.jhttp;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import eu.rekawek.jhttp.server.HttpServer;

public final class Main {

    private static final int DEFAULT_PORT = 8888;

    private static final int DEFAULT_THREAD_POOL_SIZE = 10;

    private static final String DEFAULT_SERVER_ROOT = ".";

    private Main() {
    }

    /**
     * Entry point for the server application.
     */
    public static void main(String[] args) throws IOException, ParseException {
        final Options options = getOptions();
        final CommandLine cmdLine = new BasicParser().parse(options, args);

        if (cmdLine.hasOption('h')) {
            displayUsage(options);
            return;
        }

        final int port = getIntValue(cmdLine, 'p', DEFAULT_PORT);
        final int threadPoolSize = getIntValue(cmdLine, 't', DEFAULT_THREAD_POOL_SIZE);
        final String serverRoot = getValue(cmdLine, 'r', DEFAULT_SERVER_ROOT);

        final HttpServer server = new HttpServer(Paths.get(serverRoot), port, threadPoolSize);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
        server.start();
    }

    @SuppressWarnings("static-access")
    private static Options getOptions() throws ParseException {
        final Options options = new Options();
        options.addOption(OptionBuilder
                .withLongOpt("port")
                .withDescription(String.format("port to listen (default: %d)", DEFAULT_PORT))
                .hasArg()
                .withArgName("PORT")
                .withType(Number.class)
                .create('p'));
        options.addOption(OptionBuilder
                .withLongOpt("threads")
                .withDescription(String.format("thread pool size (default: %d)", DEFAULT_THREAD_POOL_SIZE))
                .hasArg()
                .withArgName("THREADS_NO")
                .withType(Number.class)
                .create('t'));
        options.addOption(OptionBuilder
                .withLongOpt("root")
                .withDescription(String.format("server root directory (default: '%s')", DEFAULT_SERVER_ROOT))
                .hasArg()
                .withArgName("DIR")
                .withType(Number.class)
                .create('r'));
        options.addOption(OptionBuilder
                .withLongOpt("help")
                .withDescription("display help")
                .create('h'));
        return options;
    }

    private static int getIntValue(CommandLine cmdLine, char option, int defaultValue) throws ParseException {
        if (cmdLine.hasOption(option)) {
            return ((Number) cmdLine.getParsedOptionValue(String.valueOf(option))).intValue();
        } else {
            return defaultValue;
        }
    }

    private static String getValue(CommandLine cmdLine, char option, String defaultValue) throws ParseException {
        if (cmdLine.hasOption(option)) {
            return cmdLine.getOptionValue(String.valueOf(option));
        } else {
            return defaultValue;
        }
    }

    private static void displayUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar jhttp*.jar", "Starts a simple HTTP server", options, null, true);
    }
}
