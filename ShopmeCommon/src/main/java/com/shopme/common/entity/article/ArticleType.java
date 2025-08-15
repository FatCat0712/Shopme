package com.shopme.common.entity.article;

public enum ArticleType {
    MENU {
        @Override
        public String defaultDescription() {
            return "Menu-Bound Article";
        }
    }, FREE {
        @Override
        public String defaultDescription() {
            return "Free Article";
        }
    };

    public abstract String defaultDescription();
}
