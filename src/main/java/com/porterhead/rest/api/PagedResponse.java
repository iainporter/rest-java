package com.porterhead.rest.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 28/02/2013
 */
@XmlRootElement
public class PagedResponse<T> {

    private int total;
    private int page;
    private long records;
    private List<T> rows;

    public PagedResponse() {}

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

}
