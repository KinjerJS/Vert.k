package fr.kinjer.vertxutils.module.request.value;

public @interface ParamValue {

    int intValue() default 0;

    long longValue() default 0L;

    float floatValue() default 0.0F;

    double doubleValue() default 0.0D;

    boolean booleanValue() default false;

    String stringValue() default "";

    Type typeValue();

    enum Type {
        STRING,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        NONE
        ;
    }

}
