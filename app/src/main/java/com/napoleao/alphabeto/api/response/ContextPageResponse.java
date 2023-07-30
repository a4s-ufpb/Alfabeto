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

    public List<Tema> getContent() {
        return content;
    }
}
