package org.example.FakeDataGeneratorClass;

public enum FieldType {
    FULL_NAME("\""),
    FIRST_NAME("\""),
    LAST_NAME("\""),
    USERNAME("\""),
    TITLE("\""),
    BLOOD_GROUP("\""),
    WORDS("\""),
    CHARACTERS("\""),
    CITY("\""),
    COUNTRY("\""),
    COUNTRY_CODE("\""),
    ZIP_CODE("\""),
    BOOK_AUTHOR("\""),
    BOOK_GENRE("\""),
    BOOK_PUBLISHER("\""),
    BOOK_TITLE("\""),
    CELL_PHONE("\""),
    AGE(""),
    ID(""),
    GENDER("\"");

    private final String i;

    FieldType(String i) {
        this.i = i;
    }

    public String getJsonPairs(FieldType fieldType,Object value) {
        return "\t \"%s\":%s%s%s".formatted(fieldType,i,value,i);
    }
    public String getSQLPairs(Object value){
        return "%s%s%s".formatted(i,value,i);
    }

}
