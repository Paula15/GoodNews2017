package com.java.no16.protos;

/**
 * Category enum defining news category.
 */
public enum Category {

    /* Please keep categories in the order which specifies in News API. */
    ALL("全部"), TECHNOLOGY("科技"), EDUCATION("教育"),
    MILITARY("军事"), DOMESTICATION("国内"), SOCIETY("社会"),
    CULTURE("文化"), CAR("汽车"), INTERNATIONALITY("国际"),
    SPORTS("体育"), ECONIMICS("财经"), HEALTH("健康"), ENTERTAINMENT("娱乐");

    private String name;

    Category(String name) {
        this.name = name;
    }

    /** Gets the chinese name of the category. */
    public String getName() {
        return name;
    }
}
