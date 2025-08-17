package com.shopme.common.entity.menu;

public enum MenuType {
    HEADER {
        @Override
        public String defaultDescription() {
            return "Header Menu";
        }
    }, FOOTER {
        @Override
        public String defaultDescription() {
            return "Footer Menu";
        }
    };

    public abstract String defaultDescription();
}
