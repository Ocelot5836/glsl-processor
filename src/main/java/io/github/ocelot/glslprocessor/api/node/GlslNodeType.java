package io.github.ocelot.glslprocessor.api.node;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public enum GlslNodeType {
    FOR_LOOP,
    WHILE_LOOP,
    CONTINUE,
    BREAK,
    DISCARD,
    RETURN,
    /**
     * If/Else Condition: {@code if(A) {B} else {C}}
     */
    IF_ELSE,
    SWITCH,
    CASE_LABEL,

    /**
     * AND: {@code &}
     */
    AND,
    /**
     * Inclusive OR: {@code |}
     */
    OR,
    /**
     * Exclusive OR: {@code ^}
     */
    XOR,
    /**
     * Logical AND: {@code &&}
     */
    LOGICAL_AND,
    /**
     * Logical OR: {@code ||}
     */
    LOGICAL_OR,
    /**
     * Logical Exclusive OR: {@code ^^}
     */
    LOGICAL_XOR,
    /**
     * Assignment: {@code A = B, A *= B}, etc
     */
    ASSIGN,
    /**
     * Operand: {@code A * B, A >> B, A + B}, etc
     */
    OPERATION,
    /**
     * Comparison: {@code A > B, A <= B}, etc
     */
    COMPARE,
    /**
     * Ternary operator: {@code A ? B : C}
     */
    CONDITION,
    /**
     * GLSL precision: {@code precision highp float; precision lowp int;} etc
     */
    PRECISION,
    /**
     * Unary Operator: {@code A++, -A, !A}, etc
     */
    UNARY,

    /**
     * Function Declaration: {@code float myCustomFunction(float input) { return input }}
     */
    FUNCTION,
    /**
     * Invoke Function: {@code myCustomFunction(24), length(vec2(1.0, 1.0))}, etc
     */
    INVOKE_FUNCTION,
    /**
     * Type Constructor: {@code int(42), vec4(1.0, 1.0, 1.0, 0.0)}, etc
     */
    PRIMITIVE_CONSTRUCTOR,

    /**
     * Boolean, double, int, and float constants: {@code float a = 4, uint test = 42}, etc
     */
    CONSTANT,

    /**
     * Variable Declaration: {@code float a, b, c, d}; etc
     */
    VARIABLE_DECLARATION,
    /**
     * Array Get: {@code floatArray[0]}, etc
     */
    GET_ARRAY,
    /**
     * Field Get: {@code variable.test}, etc
     */
    GET_FIELD,
    /**
     * New Field: {@code float a = 42, vec4 test;} etc
     */
    NEW_FIELD,
    /**
     * New struct: {@code struct A {...}}, etc
     */
    STRUCT_DECLARATION,
    /**
     * Variable Reference (can be get, set, unary, etc based on what node is used): {@code variableName}, etc
     */
    VARIABLE
}
