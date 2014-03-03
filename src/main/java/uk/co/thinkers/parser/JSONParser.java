package uk.co.thinkers.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.thinkers.client.RestClient;
import uk.co.thinkers.parser.dto.Page;
import uk.co.thinkers.parser.dto.Pages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramarm on 03/03/14.
 *
 * @author ramarm
 */
public class JSONParser implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONParser.class);
    private String[] args;
    private List<String> urls = new ArrayList<String>();
    private int stripLevel;
    private String stripPattern;

    public JSONParser(String[] args) {
        this.args = args;
    }

    /**
     * Program entry method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new JSONParser(args).run();
    }

    @Override
    public void run() {
        if (args == null) {
            LOGGER.error("No arguments given.");
            usage();
            return;
        }

        if (args.length < 2) {
            LOGGER.error("Wrong parameters");
            usage();
            return;
        }

        if (args.length >= 3) {
            try {
                stripLevel = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Invalid stripLevel: {}", args[2]);
                stripLevel = 0;
            }
        }

        StringBuilder sb = new StringBuilder("^/");
        for (int i = 0; i < stripLevel; i++) {
            sb.append("[^/]+/");
        }
        stripPattern = sb.toString();

        RestClient<Void, Pages> restClient = new RestClient<Void, Pages>();
        Pages pages = restClient.getForObject(args[0], Pages.class);
        processPages(pages.getPages());
        LOGGER.debug("Striplevel: {}, stripPattern: {}", Integer.valueOf(args[2]), stripPattern);

        /* List them to the specified file */
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(args[1])));
            for (String url : urls) {
                LOGGER.debug("URL -> {}", url);
                writer.println(url + " " + url);
            }
            writer.close();
        } catch (IOException ioe) {
            LOGGER.error("Could not open file for writing: {}", args[1]);
        }
    }

    private void usage() {
        LOGGER.info("Usage: java -jar {}.jar JSON-URL output-file-name [stripLevel]", getClass().getSimpleName());
    }

    private void processPages(List<Page> pages) {
        for (Page page : pages) {
            urls.add(page.getLink().replaceAll("\\.html$", "").replaceAll(stripPattern, ""));

            if (page.getPages() != null) {
                processPages(page.getPages());
            }
        }
    }

}