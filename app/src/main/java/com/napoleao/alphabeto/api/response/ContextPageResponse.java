package com.napoleao.alphabeto.api.response;

import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

public class ContextPageResponse {

    private List<Tema> content = new ArrayList<>();
    private boolean first;
    private boolean last;
    private int size;

    public List<Tema> getContent() {
        return content;
    }
}
