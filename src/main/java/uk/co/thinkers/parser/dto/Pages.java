package uk.co.thinkers.parser.dto;

import java.io.Serializable;
import java.util.List;

/**
 * POJO class to expose the Magnolia page structure.
 */
public class Pages implements Serializable {

    private static final long serialVersionUID = 4565321550820078204L;
    private List<Page> pages;

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}