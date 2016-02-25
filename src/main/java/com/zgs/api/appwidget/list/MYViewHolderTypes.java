package com.zgs.api.appwidget.list;

/**
 * Created by simon on 15-12-1.
 */
public interface MYViewHolderTypes {
    int TYPE_HEAD = 0xFFFF;
    int TYPE_FOOT = TYPE_HEAD + 1;
    int TYPE_EMPTY = TYPE_FOOT + 1;
    int TYPE_CONTENT = TYPE_EMPTY + 1;
    int TYPE_SECTION_HEAD = TYPE_CONTENT + 1;
    int TYPE_SECTION_CONTENT = TYPE_SECTION_HEAD + 1;
    int TYPE_SECTION_FOOTER = TYPE_SECTION_CONTENT + 1;
}
