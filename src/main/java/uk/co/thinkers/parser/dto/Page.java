package uk.co.thinkers.parser.dto;

import java.io.Serializable;
import java.util.List;

/**
 * POJO class to expose Magnolia page structure.
 */
public class Page implements Serializable {

    private static final long serialVersionUID = 5945549092307492825L;
    private String title;
    private String link;
    private List<Page> pages;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}
