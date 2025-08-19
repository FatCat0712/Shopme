package com.shopme.common.entity.section;

public enum SectionType {
    ALL_CATEGORIES {
        @Override
        public String defaultDescription() {
            return "All Categories";
        }
    }, SELECTED_PRODUCTS {
        @Override
        public String defaultDescription() {
            return "Product";
        }
    },
    SELECTED_CATEGORIES {
        @Override
        public String defaultDescription() {
            return "Category";
        }
    }, SELECTED_BRANDS {
        @Override
        public String defaultDescription() {
            return "Brand";
        }
    }, SELECTED_ARTICLES {
        @Override
        public String defaultDescription() {
            return "Article";
        }
    },
    TEXT {
        @Override
        public String defaultDescription() {
            return "Text";
        }
    };



    public abstract String defaultDescription();
}
