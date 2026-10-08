package com.napoleao.alphabeto.api.response;

import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the response of the API when the request is made with the context parameter.
 */
public class ContextPageResponse {

    private List<Tema> content = new ArrayList<>();
    private boolean first;
    private boolean last;
    private int size;
    private int number;
    private int totalPages;
    private long totalElements;

    public List<Tema> getContent() {
        return content == null ? new ArrayList<Tema>() : content;
    }

    public boolean isLast() {
        return last;
    }

    public int getNumber() {
        return number;
    }

    public int getTotalPages() {
        return totalPages;
    }

    /** Total of themes (all pages) that match the request; 0 if the API did not send it. */
    public long getTotalElements() {
        return totalElements;
    }
}
