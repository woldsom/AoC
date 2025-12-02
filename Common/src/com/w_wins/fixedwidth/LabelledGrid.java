package com.w_wins.fixedwidth;

import java.util.List;
import java.util.Map;

public interface LabelledGrid<T> extends Grid<T> {
    public static <T> LabelledGrid<T> fromGridAndHeaders(Grid<T> original, Map<HeaderType,List<String>> headers) {
        return new DelegateLabelledGrid<>(original,headers);
    }

    public List<String> getHeader(HeaderType headerType);
}
