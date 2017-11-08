package com.sumscope.optimus.moneymarket.model.dto;

import java.util.List;

public class SearchKeywordResponseDto {

    private String type;

    private List<Result> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Result> getList() {
        return list;
    }

    public void setList(List<Result> list) {
        this.list = list;
    }

    public static class Result {

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
