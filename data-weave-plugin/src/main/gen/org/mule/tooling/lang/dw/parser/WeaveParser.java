// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.mule.tooling.lang.dw.parser.psi.WeaveTypes.*;
import static org.mule.tooling.lang.dw.parser.WeaveParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class WeaveParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ATTRIBUTE, DYNAMIC_ATTRIBUTE, SIMPLE_ATTRIBUTE),
    create_token_set_(ANNOTATION_DIRECTIVE, DIRECTIVE, FUNCTION_DIRECTIVE, IMPORT_DIRECTIVE,
      INPUT_DIRECTIVE, NAMESPACE_DIRECTIVE, OUTPUT_DIRECTIVE, TYPE_DIRECTIVE,
      VARIABLE_DIRECTIVE, VERSION_DIRECTIVE),
    create_token_set_(ARRAY_DECONSTRUCT_PATTERN, DEFAULT_PATTERN, EMPTY_ARRAY_PATTERN, EMPTY_OBJECT_PATTERN,
      EXPRESSION_PATTERN, LITERAL_PATTERN, NAMED_LITERAL_PATTERN, NAMED_REGEX_PATTERN,
      NAMED_TYPE_PATTERN, OBJECT_DECONSTRUCT_PATTERN, PATTERN, REGEX_PATTERN,
      TYPE_PATTERN),
    create_token_set_(ATTRIBUTES_TYPE, CLOSE_OBJECT_TYPE, CLOSE_ORDERED_OBJECT_TYPE, INTERSECTION_TYPE,
      KEY_TYPE, KEY_VALUE_PAIR_TYPE, LAMBDA_TYPE, LITERAL_TYPE,
      NAME_TYPE, OBJECT_TYPE, ORDERED_OBJECT_TYPE, REFERENCE_TYPE,
      TYPE, UNION_TYPE),
    create_token_set_(ADDITION_SUBTRACTION_EXPRESSION, AND_EXPRESSION, ANY_DATE_LITERAL, ARRAY_EXPRESSION,
      AS_EXPRESSION, BINARY_EXPRESSION, BOOLEAN_LITERAL, BRACKET_SELECTOR_EXPRESSION,
      CONDITIONAL_EXPRESSION, CUSTOM_INTERPOLATOR_EXPRESSION, DEFAULT_VALUE_EXPRESSION, DOT_SELECTOR_EXPRESSION,
      DO_EXPRESSION, ENCLOSED_EXPRESSION, EQUALITY_EXPRESSION, EXPRESSION,
      FUNCTION_CALL_EXPRESSION, GREATER_THAN_EXPRESSION, IS_EXPRESSION, LAMBDA_LITERAL,
      LEFT_SHIFT_EXPRESSION, LITERAL_EXPRESSION, MATCH_EXPRESSION, MULTIPLICATION_DIVISION_EXPRESSION,
      NOT_EXPRESSION, NULL_LITERAL, NUMBER_LITERAL, OBJECT_DECONSTRUCT_EXPRESSION,
      OBJECT_EXPRESSION, OR_EXPRESSION, PATTERN_MATCHER_EXPRESSION, REGEX_LITERAL,
      RIGHT_SHIFT_EXPRESSION, STRING_LITERAL, UNARY_MINUS_EXPRESSION, UNDEFINED_LITERAL,
      UPDATE_EXPRESSION, USING_EXPRESSION, VARIABLE_REFERENCE_EXPRESSION),
  };

  /* ********************************************************** */
  // AT_SPACE
  public static boolean AllAttributeSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllAttributeSelector")) return false;
    if (!nextTokenIs(b, AT_SPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT_SPACE);
    exit_section_(b, m, ALL_ATTRIBUTE_SELECTOR, r);
    return r;
  }

  /* ********************************************************** */
  // CARET_SPACE
  public static boolean AllSchemaSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AllSchemaSelector")) return false;
    if (!nextTokenIs(b, CARET_SPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CARET_SPACE);
    exit_section_(b, m, ALL_SCHEMA_SELECTOR, r);
    return r;
  }

  /* ********************************************************** */
  // '@'FqnIdentifier AnnotationArguments?
  public static boolean Annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Annotation")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ANNOTATION, null);
    r = consumeToken(b, AT);
    r = r && FqnIdentifier(b, l + 1);
    p = r; // pin = 2
    r = r && Annotation_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // AnnotationArguments?
  private static boolean Annotation_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Annotation_2")) return false;
    AnnotationArguments(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Identifier '=' Expression
  public static boolean AnnotationArgument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArgument")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANNOTATION_ARGUMENT, "<annotation argument>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' (AnnotationArgument ( ',' AnnotationArgument )* )? (',')? ')'
  public static boolean AnnotationArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArguments")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && AnnotationArguments_1(b, l + 1);
    r = r && AnnotationArguments_2(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, m, ANNOTATION_ARGUMENTS, r);
    return r;
  }

  // (AnnotationArgument ( ',' AnnotationArgument )* )?
  private static boolean AnnotationArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArguments_1")) return false;
    AnnotationArguments_1_0(b, l + 1);
    return true;
  }

  // AnnotationArgument ( ',' AnnotationArgument )*
  private static boolean AnnotationArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AnnotationArgument(b, l + 1);
    r = r && AnnotationArguments_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' AnnotationArgument )*
  private static boolean AnnotationArguments_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArguments_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AnnotationArguments_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AnnotationArguments_1_0_1", c)) break;
    }
    return true;
  }

  // ',' AnnotationArgument
  private static boolean AnnotationArguments_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArguments_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && AnnotationArgument(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',')?
  private static boolean AnnotationArguments_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationArguments_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // Identifier '('( AnnotationParameter ( ',' AnnotationParameter )*  )?')'
  public static boolean AnnotationDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ANNOTATION_DEFINITION, "<annotation definition>");
    r = Identifier(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, L_PARREN));
    r = p && report_error_(b, AnnotationDefinition_2(b, l + 1)) && r;
    r = p && consumeToken(b, R_PARREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( AnnotationParameter ( ',' AnnotationParameter )*  )?
  private static boolean AnnotationDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDefinition_2")) return false;
    AnnotationDefinition_2_0(b, l + 1);
    return true;
  }

  // AnnotationParameter ( ',' AnnotationParameter )*
  private static boolean AnnotationDefinition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDefinition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AnnotationParameter(b, l + 1);
    r = r && AnnotationDefinition_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' AnnotationParameter )*
  private static boolean AnnotationDefinition_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDefinition_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AnnotationDefinition_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AnnotationDefinition_2_0_1", c)) break;
    }
    return true;
  }

  // ',' AnnotationParameter
  private static boolean AnnotationDefinition_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDefinition_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && AnnotationParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Annotation* 'annotation' AnnotationDefinition
  public static boolean AnnotationDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDirective")) return false;
    if (!nextTokenIs(b, "<annotation directive>", ANNOTATION_DIRECTIVE_KEYWORD, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ANNOTATION_DIRECTIVE, "<annotation directive>");
    r = AnnotationDirective_0(b, l + 1);
    r = r && consumeToken(b, ANNOTATION_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && AnnotationDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean AnnotationDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AnnotationDirective_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ((Identifier) ':' TypeLiteral) ('=' Expression)?
  public static boolean AnnotationParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationParameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANNOTATION_PARAMETER, "<annotation parameter>");
    r = AnnotationParameter_0(b, l + 1);
    r = r && AnnotationParameter_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (Identifier) ':' TypeLiteral
  private static boolean AnnotationParameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationParameter_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AnnotationParameter_0_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && TypeLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (Identifier)
  private static boolean AnnotationParameter_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationParameter_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('=' Expression)?
  private static boolean AnnotationParameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationParameter_1")) return false;
    AnnotationParameter_1_0(b, l + 1);
    return true;
  }

  // '=' Expression
  private static boolean AnnotationParameter_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnnotationParameter_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // RULE_ANY_DATE
  public static boolean AnyDateLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnyDateLiteral")) return false;
    if (!nextTokenIs(b, RULE_ANY_DATE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RULE_ANY_DATE);
    exit_section_(b, m, ANY_DATE_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // '[' DeconstructVariableDeclaration '~' DeconstructVariableDeclaration ']' '->' Expression
  public static boolean ArrayDeconstructPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayDeconstructPattern")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_DECONSTRUCT_PATTERN, null);
    r = consumeToken(b, L_BRACKET);
    r = r && DeconstructVariableDeclaration(b, l + 1);
    r = r && consumeToken(b, TILDE);
    p = r; // pin = 3
    r = r && report_error_(b, DeconstructVariableDeclaration(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, R_BRACKET, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Expression (IF SimpleExpression)?
  static boolean ArrayElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayElement")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, -1);
    r = r && ArrayElement_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (IF SimpleExpression)?
  private static boolean ArrayElement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayElement_1")) return false;
    ArrayElement_1_0(b, l + 1);
    return true;
  }

  // IF SimpleExpression
  private static boolean ArrayElement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayElement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && Expression(b, l + 1, 4);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SimpleAttribute
  //            | DynamicAttribute
  public static boolean Attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ATTRIBUTE, "<attribute>");
    r = SimpleAttribute(b, l + 1);
    if (!r) r = DynamicAttribute(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // multiAttributeSelector | multiAttributeSelectorOld
  public static boolean AttributeMultiValueSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeMultiValueSelector")) return false;
    if (!nextTokenIs(b, "<attribute multi value selector>", AT, MULTIPLY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_MULTI_VALUE_SELECTOR, "<attribute multi value selector>");
    r = multiAttributeSelector(b, l + 1);
    if (!r) r = multiAttributeSelectorOld(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '@'fieldSelector?
  public static boolean AttributeSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_SELECTOR, null);
    r = consumeToken(b, AT);
    p = r; // pin = 1
    r = r && AttributeSelector_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // fieldSelector?
  private static boolean AttributeSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector_1")) return false;
    fieldSelector(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '@(' ( Attribute ( ',' Attribute )* )? ')'
  public static boolean Attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTES, "<attributes>");
    r = consumeToken(b, "@(");
    p = r; // pin = 1
    r = r && report_error_(b, Attributes_1(b, l + 1));
    r = p && consumeToken(b, R_PARREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( Attribute ( ',' Attribute )* )?
  private static boolean Attributes_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1")) return false;
    Attributes_1_0(b, l + 1);
    return true;
  }

  // Attribute ( ',' Attribute )*
  private static boolean Attributes_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Attribute(b, l + 1);
    r = r && Attributes_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' Attribute )*
  private static boolean Attributes_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Attributes_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Attributes_1_0_1", c)) break;
    }
    return true;
  }

  // ',' Attribute
  private static boolean Attributes_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Attribute(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // "@""(" NameType ":" Type (',' NameType ":" Type)* ")"
  public static boolean AttributesType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributesType")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTES_TYPE, null);
    r = consumeTokens(b, 1, AT, L_PARREN);
    p = r; // pin = 1
    r = r && report_error_(b, NameType(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, Type(b, l + 1)) && r;
    r = p && report_error_(b, AttributesType_5(b, l + 1)) && r;
    r = p && consumeToken(b, R_PARREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (',' NameType ":" Type)*
  private static boolean AttributesType_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributesType_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AttributesType_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AttributesType_5", c)) break;
    }
    return true;
  }

  // ',' NameType ":" Type
  private static boolean AttributesType_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributesType_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && NameType(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (LambdaType | CloseOrderedObjectType | OrderedObjectType | CloseObjectType | ObjectType  | ReferenceType | LiteralType | ('(' Type ')')) (Schema)?
  static boolean BasicTypeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = BasicTypeExpression_0(b, l + 1);
    r = r && BasicTypeExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LambdaType | CloseOrderedObjectType | OrderedObjectType | CloseObjectType | ObjectType  | ReferenceType | LiteralType | ('(' Type ')')
  private static boolean BasicTypeExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = LambdaType(b, l + 1);
    if (!r) r = CloseOrderedObjectType(b, l + 1);
    if (!r) r = OrderedObjectType(b, l + 1);
    if (!r) r = CloseObjectType(b, l + 1);
    if (!r) r = ObjectType(b, l + 1);
    if (!r) r = ReferenceType(b, l + 1);
    if (!r) r = LiteralType(b, l + 1);
    if (!r) r = BasicTypeExpression_0_7(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' Type ')'
  private static boolean BasicTypeExpression_0_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression_0_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (Schema)?
  private static boolean BasicTypeExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression_1")) return false;
    BasicTypeExpression_1_0(b, l + 1);
    return true;
  }

  // (Schema)
  private static boolean BasicTypeExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Schema(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CustomLoader? ContainerModuleIdentifier Identifier
  public static boolean BinaryFunctionIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BinaryFunctionIdentifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_FUNCTION_IDENTIFIER, "<Identifier>");
    r = BinaryFunctionIdentifier_0(b, l + 1);
    r = r && ContainerModuleIdentifier(b, l + 1);
    r = r && Identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CustomLoader?
  private static boolean BinaryFunctionIdentifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BinaryFunctionIdentifier_0")) return false;
    CustomLoader(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Expression
  public static boolean Body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BODY, "<body>");
    r = Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TRUE_LITERAL
  //                         | FALSE_LITERAL
  public static boolean BooleanLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BooleanLiteral")) return false;
    if (!nextTokenIs(b, "<boolean literal>", FALSE_LITERAL, TRUE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLEAN_LITERAL, "<boolean literal>");
    r = consumeToken(b, TRUE_LITERAL);
    if (!r) r = consumeToken(b, FALSE_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{|' KeyValuePairsType '|}' (Schema)?
  public static boolean CloseObjectType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType")) return false;
    if (!nextTokenIs(b, OPEN_CLOSE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLOSE_OBJECT_TYPE, null);
    r = consumeToken(b, OPEN_CLOSE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, KeyValuePairsType(b, l + 1));
    r = p && report_error_(b, consumeToken(b, CLOSE_CLOSE_KEYWORD)) && r;
    r = p && CloseObjectType_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (Schema)?
  private static boolean CloseObjectType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_3")) return false;
    CloseObjectType_3_0(b, l + 1);
    return true;
  }

  // (Schema)
  private static boolean CloseObjectType_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Schema(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{-|' ((KeyValuePairType)? (',' KeyValuePairType)*)?  '|-}' (Schema)?
  public static boolean CloseOrderedObjectType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType")) return false;
    if (!nextTokenIs(b, OPEN_CLOSE_ORDERED_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLOSE_ORDERED_OBJECT_TYPE, null);
    r = consumeToken(b, OPEN_CLOSE_ORDERED_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, CloseOrderedObjectType_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, CLOSE_CLOSE_ORDERED_KEYWORD)) && r;
    r = p && CloseOrderedObjectType_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((KeyValuePairType)? (',' KeyValuePairType)*)?
  private static boolean CloseOrderedObjectType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_1")) return false;
    CloseOrderedObjectType_1_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)? (',' KeyValuePairType)*
  private static boolean CloseOrderedObjectType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = CloseOrderedObjectType_1_0_0(b, l + 1);
    r = r && CloseOrderedObjectType_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (KeyValuePairType)?
  private static boolean CloseOrderedObjectType_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_1_0_0")) return false;
    CloseOrderedObjectType_1_0_0_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)
  private static boolean CloseOrderedObjectType_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' KeyValuePairType)*
  private static boolean CloseOrderedObjectType_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!CloseOrderedObjectType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "CloseOrderedObjectType_1_0_1", c)) break;
    }
    return true;
  }

  // ',' KeyValuePairType
  private static boolean CloseOrderedObjectType_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (Schema)?
  private static boolean CloseOrderedObjectType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_3")) return false;
    CloseOrderedObjectType_3_0(b, l + 1);
    return true;
  }

  // (Schema)
  private static boolean CloseOrderedObjectType_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseOrderedObjectType_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Schema(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // "(" (Identifier | StringLiteral) ':' Expression ")" IF "(" Expression ")"
  public static boolean ConditionalSchemaKV(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalSchemaKV")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONDITIONAL_SCHEMA_KV, null);
    r = consumeToken(b, L_PARREN);
    r = r && ConditionalSchemaKV_1(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 3
    r = r && report_error_(b, Expression(b, l + 1, -1));
    r = p && report_error_(b, consumeTokens(b, -1, R_PARREN, IF, L_PARREN)) && r;
    r = p && report_error_(b, Expression(b, l + 1, -1)) && r;
    r = p && consumeToken(b, R_PARREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Identifier | StringLiteral
  private static boolean ConditionalSchemaKV_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalSchemaKV_1")) return false;
    boolean r;
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (Identifier '::')*
  public static boolean ContainerModuleIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ContainerModuleIdentifier")) return false;
    Marker m = enter_section_(b, l, _NONE_, CONTAINER_MODULE_IDENTIFIER, "<container module identifier>");
    while (true) {
      int c = current_position_(b);
      if (!ContainerModuleIdentifier_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ContainerModuleIdentifier", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // Identifier '::'
  private static boolean ContainerModuleIdentifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ContainerModuleIdentifier_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, PACKAGE_SEPARATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BACKTIKED_QUOTED_STRING
  public static boolean CustomInterpolationString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomInterpolationString")) return false;
    if (!nextTokenIs(b, BACKTIKED_QUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BACKTIKED_QUOTED_STRING);
    exit_section_(b, m, CUSTOM_INTERPOLATION_STRING, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier"!"
  public static boolean CustomLoader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomLoader")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_LOADER, "<custom loader>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, ESCLAMATION);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MIME_TYPE_KEYWORD
  public static boolean DataFormat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DataFormat")) return false;
    if (!nextTokenIs(b, MIME_TYPE_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MIME_TYPE_KEYWORD);
    exit_section_(b, m, DATA_FORMAT, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier '#'
  public static boolean DeclaredNamespace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DeclaredNamespace")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECLARED_NAMESPACE, "<declared namespace>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, HASH);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Identifier
  public static boolean DeconstructVariableDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DeconstructVariableDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECONSTRUCT_VARIABLE_DECLARATION, "<deconstruct variable declaration>");
    r = Identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ELSE (Identifier)? '->' Expression
  public static boolean DefaultPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DefaultPattern")) return false;
    if (!nextTokenIs(b, ELSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DEFAULT_PATTERN, null);
    r = consumeToken(b, ELSE);
    p = r; // pin = 1
    r = r && report_error_(b, DefaultPattern_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (Identifier)?
  private static boolean DefaultPattern_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DefaultPattern_1")) return false;
    DefaultPattern_1_0(b, l + 1);
    return true;
  }

  // (Identifier)
  private static boolean DefaultPattern_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DefaultPattern_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // VersionDirective
  //            | NamespaceDirective
  //            | VariableDirective
  //            | AnnotationDirective
  //            | OutputDirective
  //            | InputDirective
  //            | TypeDirective
  //            | ImportDirective
  //            | FunctionDirective
  public static boolean Directive(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Directive")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, DIRECTIVE, "<directive>");
    r = VersionDirective(b, l + 1);
    if (!r) r = NamespaceDirective(b, l + 1);
    if (!r) r = VariableDirective(b, l + 1);
    if (!r) r = AnnotationDirective(b, l + 1);
    if (!r) r = OutputDirective(b, l + 1);
    if (!r) r = InputDirective(b, l + 1);
    if (!r) r = TypeDirective(b, l + 1);
    if (!r) r = ImportDirective(b, l + 1);
    if (!r) r = FunctionDirective(b, l + 1);
    exit_section_(b, l, m, r, false, WeaveParser::HeaderRecover);
    return r;
  }

  /* ********************************************************** */
  // Annotation* (VariableDirective
  //            | TypeDirective
  //            | ImportDirective
  //            | NamespaceDirective
  //            | FunctionDirective)
  static boolean DoDirectives(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoDirectives")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = DoDirectives_0(b, l + 1);
    r = r && DoDirectives_1(b, l + 1);
    exit_section_(b, l, m, r, false, WeaveParser::HeaderRecover);
    return r;
  }

  // Annotation*
  private static boolean DoDirectives_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoDirectives_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "DoDirectives_0", c)) break;
    }
    return true;
  }

  // VariableDirective
  //            | TypeDirective
  //            | ImportDirective
  //            | NamespaceDirective
  //            | FunctionDirective
  private static boolean DoDirectives_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoDirectives_1")) return false;
    boolean r;
    r = VariableDirective(b, l + 1);
    if (!r) r = TypeDirective(b, l + 1);
    if (!r) r = ImportDirective(b, l + 1);
    if (!r) r = NamespaceDirective(b, l + 1);
    if (!r) r = FunctionDirective(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // Header ('---'  Body) ? | Body
  public static boolean Document(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Document")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DOCUMENT, "<document>");
    r = Document_0(b, l + 1);
    if (!r) r = Body(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Header ('---'  Body) ?
  private static boolean Document_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Document_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Header(b, l + 1);
    r = r && Document_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('---'  Body) ?
  private static boolean Document_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Document_0_1")) return false;
    Document_0_1_0(b, l + 1);
    return true;
  }

  // '---'  Body
  private static boolean Document_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Document_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOCUMENT_SEPARATOR);
    r = r && Body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' Expression ')' ((':' Expression) | (IF Expression))?
  public static boolean DynamicAttribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicAttribute")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeToken(b, R_PARREN);
    r = r && DynamicAttribute_3(b, l + 1);
    exit_section_(b, m, DYNAMIC_ATTRIBUTE, r);
    return r;
  }

  // ((':' Expression) | (IF Expression))?
  private static boolean DynamicAttribute_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicAttribute_3")) return false;
    DynamicAttribute_3_0(b, l + 1);
    return true;
  }

  // (':' Expression) | (IF Expression)
  private static boolean DynamicAttribute_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicAttribute_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DynamicAttribute_3_0_0(b, l + 1);
    if (!r) r = DynamicAttribute_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ':' Expression
  private static boolean DynamicAttribute_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicAttribute_3_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IF Expression
  private static boolean DynamicAttribute_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicAttribute_3_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' Expression ')' (dynamicKV | conditionalKV)?
  public static boolean DynamicKeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicKeyValuePair")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeToken(b, R_PARREN);
    r = r && DynamicKeyValuePair_3(b, l + 1);
    exit_section_(b, m, DYNAMIC_KEY_VALUE_PAIR, r);
    return r;
  }

  // (dynamicKV | conditionalKV)?
  private static boolean DynamicKeyValuePair_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicKeyValuePair_3")) return false;
    DynamicKeyValuePair_3_0(b, l + 1);
    return true;
  }

  // dynamicKV | conditionalKV
  private static boolean DynamicKeyValuePair_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicKeyValuePair_3_0")) return false;
    boolean r;
    r = dynamicKV(b, l + 1);
    if (!r) r = conditionalKV(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '?'
  public static boolean DynamicReturn(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicReturn")) return false;
    if (!nextTokenIs(b, QUESTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUESTION);
    exit_section_(b, m, DYNAMIC_RETURN, r);
    return r;
  }

  /* ********************************************************** */
  // (Attributes)? ':' Expression
  public static boolean DynamicSingleKeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicSingleKeyValuePair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DYNAMIC_SINGLE_KEY_VALUE_PAIR, "<dynamic single key value pair>");
    r = DynamicSingleKeyValuePair_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (Attributes)?
  private static boolean DynamicSingleKeyValuePair_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicSingleKeyValuePair_0")) return false;
    DynamicSingleKeyValuePair_0_0(b, l + 1);
    return true;
  }

  // (Attributes)
  private static boolean DynamicSingleKeyValuePair_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicSingleKeyValuePair_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Attributes(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '['']'  '->' Expression
  public static boolean EmptyArrayPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EmptyArrayPattern")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, L_BRACKET, R_BRACKET, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, EMPTY_ARRAY_PATTERN, r);
    return r;
  }

  /* ********************************************************** */
  // '{''}'  '->' Expression
  public static boolean EmptyObjectPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EmptyObjectPattern")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, L_CURLY, R_CURLY, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, EMPTY_OBJECT_PATTERN, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier IF (EnclosedExpression | SimpleExpression) '->' Expression
  public static boolean ExpressionPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionPattern")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_PATTERN, "<expression pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, IF);
    r = r && ExpressionPattern_2(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 4
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // EnclosedExpression | SimpleExpression
  private static boolean ExpressionPattern_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionPattern_2")) return false;
    boolean r;
    r = EnclosedExpression(b, l + 1);
    if (!r) r = Expression(b, l + 1, 4);
    return r;
  }

  /* ********************************************************** */
  // CustomLoader? ContainerModuleIdentifier Identifier
  public static boolean FqnIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FqnIdentifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FQN_IDENTIFIER, "<Identifier>");
    r = FqnIdentifier_0(b, l + 1);
    r = r && ContainerModuleIdentifier(b, l + 1);
    r = r && Identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CustomLoader?
  private static boolean FqnIdentifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FqnIdentifier_0")) return false;
    CustomLoader(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' ( Expression ( ',' Expression )* )? (',')? ')'
  public static boolean FunctionCallArguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallArguments")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_CALL_ARGUMENTS, null);
    r = consumeToken(b, L_PARREN);
    p = r; // pin = 1
    r = r && report_error_(b, FunctionCallArguments_1(b, l + 1));
    r = p && report_error_(b, FunctionCallArguments_2(b, l + 1)) && r;
    r = p && consumeToken(b, R_PARREN) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( Expression ( ',' Expression )* )?
  private static boolean FunctionCallArguments_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallArguments_1")) return false;
    FunctionCallArguments_1_0(b, l + 1);
    return true;
  }

  // Expression ( ',' Expression )*
  private static boolean FunctionCallArguments_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallArguments_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, -1);
    r = r && FunctionCallArguments_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' Expression )*
  private static boolean FunctionCallArguments_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallArguments_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!FunctionCallArguments_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionCallArguments_1_0_1", c)) break;
    }
    return true;
  }

  // ',' Expression
  private static boolean FunctionCallArguments_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallArguments_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',')?
  private static boolean FunctionCallArguments_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallArguments_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // '<' (Type ( ',' Type )*)? '>'
  public static boolean FunctionCallTypeParameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallTypeParameters")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESS);
    r = r && FunctionCallTypeParameters_1(b, l + 1);
    r = r && consumeToken(b, GREATER);
    exit_section_(b, m, FUNCTION_CALL_TYPE_PARAMETERS, r);
    return r;
  }

  // (Type ( ',' Type )*)?
  private static boolean FunctionCallTypeParameters_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallTypeParameters_1")) return false;
    FunctionCallTypeParameters_1_0(b, l + 1);
    return true;
  }

  // Type ( ',' Type )*
  private static boolean FunctionCallTypeParameters_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallTypeParameters_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Type(b, l + 1);
    r = r && FunctionCallTypeParameters_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' Type )*
  private static boolean FunctionCallTypeParameters_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallTypeParameters_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!FunctionCallTypeParameters_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionCallTypeParameters_1_0_1", c)) break;
    }
    return true;
  }

  // ',' Type
  private static boolean FunctionCallTypeParameters_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallTypeParameters_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier TypeParameterDeclaration? L_PARREN ( FunctionParameter ( ',' FunctionParameter )* )? (",")? R_PARREN ( ":" (Type | DynamicReturn)? "=" | "=")? Expression
  public static boolean FunctionDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_DEFINITION, "<function definition>");
    r = Identifier(b, l + 1);
    r = r && FunctionDefinition_1(b, l + 1);
    r = r && consumeToken(b, L_PARREN);
    p = r; // pin = 3
    r = r && report_error_(b, FunctionDefinition_3(b, l + 1));
    r = p && report_error_(b, FunctionDefinition_4(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, R_PARREN)) && r;
    r = p && report_error_(b, FunctionDefinition_6(b, l + 1)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // TypeParameterDeclaration?
  private static boolean FunctionDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_1")) return false;
    TypeParameterDeclaration(b, l + 1);
    return true;
  }

  // ( FunctionParameter ( ',' FunctionParameter )* )?
  private static boolean FunctionDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_3")) return false;
    FunctionDefinition_3_0(b, l + 1);
    return true;
  }

  // FunctionParameter ( ',' FunctionParameter )*
  private static boolean FunctionDefinition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FunctionParameter(b, l + 1);
    r = r && FunctionDefinition_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' FunctionParameter )*
  private static boolean FunctionDefinition_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!FunctionDefinition_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionDefinition_3_0_1", c)) break;
    }
    return true;
  }

  // ',' FunctionParameter
  private static boolean FunctionDefinition_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && FunctionParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (",")?
  private static boolean FunctionDefinition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_4")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // ( ":" (Type | DynamicReturn)? "=" | "=")?
  private static boolean FunctionDefinition_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_6")) return false;
    FunctionDefinition_6_0(b, l + 1);
    return true;
  }

  // ":" (Type | DynamicReturn)? "=" | "="
  private static boolean FunctionDefinition_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FunctionDefinition_6_0_0(b, l + 1);
    if (!r) r = consumeToken(b, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // ":" (Type | DynamicReturn)? "="
  private static boolean FunctionDefinition_6_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_6_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && FunctionDefinition_6_0_0_1(b, l + 1);
    r = r && consumeToken(b, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // (Type | DynamicReturn)?
  private static boolean FunctionDefinition_6_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_6_0_0_1")) return false;
    FunctionDefinition_6_0_0_1_0(b, l + 1);
    return true;
  }

  // Type | DynamicReturn
  private static boolean FunctionDefinition_6_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_6_0_0_1_0")) return false;
    boolean r;
    r = Type(b, l + 1);
    if (!r) r = DynamicReturn(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // Annotation*  'fun' FunctionDefinition
  public static boolean FunctionDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDirective")) return false;
    if (!nextTokenIs(b, "<function directive>", AT, FUNCTION_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_DIRECTIVE, "<function directive>");
    r = FunctionDirective_0(b, l + 1);
    r = r && consumeToken(b, FUNCTION_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && FunctionDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean FunctionDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionDirective_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // VariableNameTypeDefinition ('=' Expression)?
  public static boolean FunctionParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionParameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_PARAMETER, "<function parameter>");
    r = VariableNameTypeDefinition(b, l + 1);
    r = r && FunctionParameter_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('=' Expression)?
  private static boolean FunctionParameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionParameter_1")) return false;
    FunctionParameter_1_0(b, l + 1);
    return true;
  }

  // '=' Expression
  private static boolean FunctionParameter_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionParameter_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (Directive)+
  public static boolean Header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Header")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEADER, "<header>");
    r = Header_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!Header_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Header", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (Directive)
  private static boolean Header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Directive(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // !('---'|OUTPUT_DIRECTIVE_KEYWORD|'type'|'fun'|'ns'|'var'|'%dw'|'input'|IMPORT_DIRECTIVE_KEYWORD | '@' | 'annotation')
  static boolean HeaderRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "HeaderRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !HeaderRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '---'|OUTPUT_DIRECTIVE_KEYWORD|'type'|'fun'|'ns'|'var'|'%dw'|'input'|IMPORT_DIRECTIVE_KEYWORD | '@' | 'annotation'
  private static boolean HeaderRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "HeaderRecover_0")) return false;
    boolean r;
    r = consumeToken(b, DOCUMENT_SEPARATOR);
    if (!r) r = consumeToken(b, OUTPUT_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, TYPE_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, FUNCTION_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, NAMESPACE_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, VAR_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, VERSION_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, INPUT_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, IMPORT_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, AT);
    if (!r) r = consumeToken(b, ANNOTATION_DIRECTIVE_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // DOLLAR_VARIABLE | ID | MATCH_KEYWORD | MATCHES_KEYWORD | FROM_KEYWORD | NOT_KEYWORD | UPDATE_KEYWORD | AT_KEYWORD | METADATA_INJECTOR
  public static boolean Identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Identifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER, "<Identifier>");
    r = consumeToken(b, DOLLAR_VARIABLE);
    if (!r) r = consumeToken(b, ID);
    if (!r) r = consumeToken(b, MATCH_KEYWORD);
    if (!r) r = consumeToken(b, MATCHES_KEYWORD);
    if (!r) r = consumeToken(b, FROM_KEYWORD);
    if (!r) r = consumeToken(b, NOT_KEYWORD);
    if (!r) r = consumeToken(b, UPDATE_KEYWORD);
    if (!r) r = consumeToken(b, AT_KEYWORD);
    if (!r) r = consumeToken(b, METADATA_INJECTOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Annotation* IMPORT_DIRECTIVE_KEYWORD (((ImportedElement (',' ImportedElement)*) | '*') 'from')? FqnIdentifier ('as' Identifier)?
  public static boolean ImportDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective")) return false;
    if (!nextTokenIs(b, "<import directive>", AT, IMPORT_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_DIRECTIVE, "<import directive>");
    r = ImportDirective_0(b, l + 1);
    r = r && consumeToken(b, IMPORT_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && report_error_(b, ImportDirective_2(b, l + 1));
    r = p && report_error_(b, FqnIdentifier(b, l + 1)) && r;
    r = p && ImportDirective_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean ImportDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ImportDirective_0", c)) break;
    }
    return true;
  }

  // (((ImportedElement (',' ImportedElement)*) | '*') 'from')?
  private static boolean ImportDirective_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_2")) return false;
    ImportDirective_2_0(b, l + 1);
    return true;
  }

  // ((ImportedElement (',' ImportedElement)*) | '*') 'from'
  private static boolean ImportDirective_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportDirective_2_0_0(b, l + 1);
    r = r && consumeToken(b, FROM_KEYWORD);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ImportedElement (',' ImportedElement)*) | '*'
  private static boolean ImportDirective_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportDirective_2_0_0_0(b, l + 1);
    if (!r) r = consumeToken(b, MULTIPLY);
    exit_section_(b, m, null, r);
    return r;
  }

  // ImportedElement (',' ImportedElement)*
  private static boolean ImportDirective_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportedElement(b, l + 1);
    r = r && ImportDirective_2_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' ImportedElement)*
  private static boolean ImportDirective_2_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_2_0_0_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ImportDirective_2_0_0_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ImportDirective_2_0_0_0_1", c)) break;
    }
    return true;
  }

  // ',' ImportedElement
  private static boolean ImportDirective_2_0_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_2_0_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ImportedElement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('as' Identifier)?
  private static boolean ImportDirective_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_4")) return false;
    ImportDirective_4_0(b, l + 1);
    return true;
  }

  // 'as' Identifier
  private static boolean ImportDirective_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AS);
    r = r && Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier ('as' ImportedElementAlias)?
  public static boolean ImportedElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORTED_ELEMENT, "<imported element>");
    r = Identifier(b, l + 1);
    r = r && ImportedElement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('as' ImportedElementAlias)?
  private static boolean ImportedElement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElement_1")) return false;
    ImportedElement_1_0(b, l + 1);
    return true;
  }

  // 'as' ImportedElementAlias
  private static boolean ImportedElement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AS);
    r = r && ImportedElementAlias(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier
  public static boolean ImportedElementAlias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElementAlias")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORTED_ELEMENT_ALIAS, "<imported element alias>");
    r = Identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DataFormat | Identifier
  public static boolean InputDataFormat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDataFormat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INPUT_DATA_FORMAT, "<input data format>");
    r = DataFormat(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Annotation* 'input' VariableNameTypeDefinition InputDataFormat? Options?
  public static boolean InputDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDirective")) return false;
    if (!nextTokenIs(b, "<input directive>", AT, INPUT_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INPUT_DIRECTIVE, "<input directive>");
    r = InputDirective_0(b, l + 1);
    r = r && consumeToken(b, INPUT_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && report_error_(b, VariableNameTypeDefinition(b, l + 1));
    r = p && report_error_(b, InputDirective_3(b, l + 1)) && r;
    r = p && InputDirective_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean InputDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "InputDirective_0", c)) break;
    }
    return true;
  }

  // InputDataFormat?
  private static boolean InputDirective_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDirective_3")) return false;
    InputDataFormat(b, l + 1);
    return true;
  }

  // Options?
  private static boolean InputDirective_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDirective_4")) return false;
    Options(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // BasicTypeExpression (IntersectionTypeExpression)*
  public static boolean IntersectionType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IntersectionType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, INTERSECTION_TYPE, "<intersection type>");
    r = BasicTypeExpression(b, l + 1);
    r = r && IntersectionType_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (IntersectionTypeExpression)*
  private static boolean IntersectionType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IntersectionType_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!IntersectionType_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "IntersectionType_1", c)) break;
    }
    return true;
  }

  // (IntersectionTypeExpression)
  private static boolean IntersectionType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IntersectionType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = IntersectionTypeExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '&' BasicTypeExpression
  static boolean IntersectionTypeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IntersectionTypeExpression")) return false;
    if (!nextTokenIs(b, AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    r = r && BasicTypeExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // QualifiedName Attributes?
  public static boolean Key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY, "<key>");
    r = QualifiedName(b, l + 1);
    r = r && Key_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Attributes?
  private static boolean Key_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Key_1")) return false;
    Attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // NameType AttributesType?
  public static boolean KeyType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, KEY_TYPE, "<key type>");
    r = NameType(b, l + 1);
    r = r && KeyType_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AttributesType?
  private static boolean KeyType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyType_1")) return false;
    AttributesType(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Key ':' Expression
  public static boolean KeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePair")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, KEY_VALUE_PAIR, "<key value pair>");
    r = Key(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 2
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KeyType ('*')? ('?')? ":" Type
  public static boolean KeyValuePairType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY_VALUE_PAIR_TYPE, "<key value pair type>");
    r = KeyType(b, l + 1);
    r = r && KeyValuePairType_1(b, l + 1);
    r = r && KeyValuePairType_2(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('*')?
  private static boolean KeyValuePairType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairType_1")) return false;
    consumeToken(b, MULTIPLY);
    return true;
  }

  // ('?')?
  private static boolean KeyValuePairType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairType_2")) return false;
    consumeToken(b, QUESTION);
    return true;
  }

  /* ********************************************************** */
  // ((KeyValuePairType)? (',' KeyValuePairType)*)? (',')?
  static boolean KeyValuePairsType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairsType_0(b, l + 1);
    r = r && KeyValuePairsType_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((KeyValuePairType)? (',' KeyValuePairType)*)?
  private static boolean KeyValuePairsType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_0")) return false;
    KeyValuePairsType_0_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)? (',' KeyValuePairType)*
  private static boolean KeyValuePairsType_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairsType_0_0_0(b, l + 1);
    r = r && KeyValuePairsType_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (KeyValuePairType)?
  private static boolean KeyValuePairsType_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_0_0_0")) return false;
    KeyValuePairsType_0_0_0_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)
  private static boolean KeyValuePairsType_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_0_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' KeyValuePairType)*
  private static boolean KeyValuePairsType_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_0_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!KeyValuePairsType_0_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "KeyValuePairsType_0_0_1", c)) break;
    }
    return true;
  }

  // ',' KeyValuePairType
  private static boolean KeyValuePairsType_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',')?
  private static boolean KeyValuePairsType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePairsType_1")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // '(' (LambdaTypeParameter (',' LambdaTypeParameter)*)? ')' '->' Type
  public static boolean LambdaType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaType")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LAMBDA_TYPE, null);
    r = consumeToken(b, L_PARREN);
    r = r && LambdaType_1(b, l + 1);
    r = r && consumeTokens(b, 2, R_PARREN, ARROW_TOKEN);
    p = r; // pin = 4
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (LambdaTypeParameter (',' LambdaTypeParameter)*)?
  private static boolean LambdaType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaType_1")) return false;
    LambdaType_1_0(b, l + 1);
    return true;
  }

  // LambdaTypeParameter (',' LambdaTypeParameter)*
  private static boolean LambdaType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = LambdaTypeParameter(b, l + 1);
    r = r && LambdaType_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' LambdaTypeParameter)*
  private static boolean LambdaType_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaType_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!LambdaType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LambdaType_1_0_1", c)) break;
    }
    return true;
  }

  // ',' LambdaTypeParameter
  private static boolean LambdaType_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaType_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && LambdaTypeParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NamedLambdaTypeParameter | Type
  public static boolean LambdaTypeParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaTypeParameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LAMBDA_TYPE_PARAMETER, "<lambda type parameter>");
    r = NamedLambdaTypeParameter(b, l + 1);
    if (!r) r = Type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (LiteralExpression | FqnIdentifier ) '->' Expression
  public static boolean LiteralPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralPattern")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_PATTERN, "<literal pattern>");
    r = LiteralPattern_0(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 2
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // LiteralExpression | FqnIdentifier
  private static boolean LiteralPattern_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralPattern_0")) return false;
    boolean r;
    r = LiteralExpression(b, l + 1);
    if (!r) r = FqnIdentifier(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NumberLiteral | StringLiteral | BooleanLiteral
  public static boolean LiteralType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_TYPE, "<literal type>");
    r = NumberLiteral(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    if (!r) r = BooleanLiteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '*'fieldSelector
  public static boolean MultiValueSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiValueSelector")) return false;
    if (!nextTokenIs(b, MULTIPLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MULTI_VALUE_SELECTOR, null);
    r = consumeToken(b, MULTIPLY);
    p = r; // pin = 1
    r = r && fieldSelector(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' objectEntryRule? (',' objectEntryRule)* (',')? '}'
  static boolean MultipleKeyValuePairObj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultipleKeyValuePairObj")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, L_CURLY);
    p = r; // pin = 1
    r = r && report_error_(b, MultipleKeyValuePairObj_1(b, l + 1));
    r = p && report_error_(b, MultipleKeyValuePairObj_2(b, l + 1)) && r;
    r = p && report_error_(b, MultipleKeyValuePairObj_3(b, l + 1)) && r;
    r = p && consumeToken(b, R_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // objectEntryRule?
  private static boolean MultipleKeyValuePairObj_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultipleKeyValuePairObj_1")) return false;
    objectEntryRule(b, l + 1);
    return true;
  }

  // (',' objectEntryRule)*
  private static boolean MultipleKeyValuePairObj_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultipleKeyValuePairObj_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!MultipleKeyValuePairObj_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MultipleKeyValuePairObj_2", c)) break;
    }
    return true;
  }

  // ',' objectEntryRule
  private static boolean MultipleKeyValuePairObj_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultipleKeyValuePairObj_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && objectEntryRule(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',')?
  private static boolean MultipleKeyValuePairObj_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultipleKeyValuePairObj_3")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // ((Identifier"#")?(Identifier | StringLiteral) ('?')?) | '_' | '('TypeParameter')'
  public static boolean NameType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_TYPE, "<name type>");
    r = NameType_0(b, l + 1);
    if (!r) r = consumeToken(b, UNDERSCORE);
    if (!r) r = NameType_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (Identifier"#")?(Identifier | StringLiteral) ('?')?
  private static boolean NameType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = NameType_0_0(b, l + 1);
    r = r && NameType_0_1(b, l + 1);
    r = r && NameType_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (Identifier"#")?
  private static boolean NameType_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType_0_0")) return false;
    NameType_0_0_0(b, l + 1);
    return true;
  }

  // Identifier"#"
  private static boolean NameType_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, HASH);
    exit_section_(b, m, null, r);
    return r;
  }

  // Identifier | StringLiteral
  private static boolean NameType_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType_0_1")) return false;
    boolean r;
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    return r;
  }

  // ('?')?
  private static boolean NameType_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType_0_2")) return false;
    consumeToken(b, QUESTION);
    return true;
  }

  // '('TypeParameter')'
  private static boolean NameType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameType_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && TypeParameter(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier ('?')? ':' Type
  static boolean NamedLambdaTypeParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedLambdaTypeParameter")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = Identifier(b, l + 1);
    r = r && NamedLambdaTypeParameter_1(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 3
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ('?')?
  private static boolean NamedLambdaTypeParameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedLambdaTypeParameter_1")) return false;
    consumeToken(b, QUESTION);
    return true;
  }

  /* ********************************************************** */
  // Identifier ':' LiteralExpression '->' Expression
  public static boolean NamedLiteralPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedLiteralPattern")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMED_LITERAL_PATTERN, "<named literal pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 2
    r = r && report_error_(b, LiteralExpression(b, l + 1));
    r = p && report_error_(b, consumeToken(b, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Identifier 'matches' RegexLiteral '->' Expression
  public static boolean NamedRegexPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedRegexPattern")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMED_REGEX_PATTERN, "<named regex pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, MATCHES_KEYWORD);
    r = r && RegexLiteral(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 4
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Identifier "is" TypeLiteral '->' Expression
  public static boolean NamedTypePattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedTypePattern")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMED_TYPE_PATTERN, "<named type pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, IS);
    p = r; // pin = 2
    r = r && report_error_(b, TypeLiteral(b, l + 1));
    r = p && report_error_(b, consumeToken(b, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Identifier NAMESPACE_URI
  public static boolean NamespaceDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_DEFINITION, "<namespace definition>");
    r = Identifier(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, NAMESPACE_URI);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Annotation* 'ns' NamespaceDefinition
  public static boolean NamespaceDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDirective")) return false;
    if (!nextTokenIs(b, "<namespace directive>", AT, NAMESPACE_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_DIRECTIVE, "<namespace directive>");
    r = NamespaceDirective_0(b, l + 1);
    r = r && consumeToken(b, NAMESPACE_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && NamespaceDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean NamespaceDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "NamespaceDirective_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '#'
  public static boolean NamespaceSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceSelector")) return false;
    if (!nextTokenIs(b, HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HASH);
    exit_section_(b, m, NAMESPACE_SELECTOR, r);
    return r;
  }

  /* ********************************************************** */
  // NULL_LITERAL_KEYWORD
  public static boolean NullLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NullLiteral")) return false;
    if (!nextTokenIs(b, NULL_LITERAL_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NULL_LITERAL_KEYWORD);
    exit_section_(b, m, NULL_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // ('+' | '-')? (DOUBLE_LITERAL | INTEGER_LITERAL)
  public static boolean NumberLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NumberLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMBER_LITERAL, "<number literal>");
    r = NumberLiteral_0(b, l + 1);
    r = r && NumberLiteral_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('+' | '-')?
  private static boolean NumberLiteral_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NumberLiteral_0")) return false;
    NumberLiteral_0_0(b, l + 1);
    return true;
  }

  // '+' | '-'
  private static boolean NumberLiteral_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NumberLiteral_0_0")) return false;
    boolean r;
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    return r;
  }

  // DOUBLE_LITERAL | INTEGER_LITERAL
  private static boolean NumberLiteral_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NumberLiteral_1")) return false;
    boolean r;
    r = consumeToken(b, DOUBLE_LITERAL);
    if (!r) r = consumeToken(b, INTEGER_LITERAL);
    return r;
  }

  /* ********************************************************** */
  // '{' DeconstructVariableDeclaration ':' DeconstructVariableDeclaration '~' DeconstructVariableDeclaration '}' '->' Expression
  public static boolean ObjectDeconstructPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectDeconstructPattern")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_DECONSTRUCT_PATTERN, null);
    r = consumeToken(b, L_CURLY);
    r = r && DeconstructVariableDeclaration(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && DeconstructVariableDeclaration(b, l + 1);
    r = r && consumeToken(b, TILDE);
    p = r; // pin = 5
    r = r && report_error_(b, DeconstructVariableDeclaration(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, R_CURLY, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '&'fieldSelector?
  public static boolean ObjectSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector")) return false;
    if (!nextTokenIs(b, AND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_SELECTOR, null);
    r = consumeToken(b, AND);
    p = r; // pin = 1
    r = r && ObjectSelector_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // fieldSelector?
  private static boolean ObjectSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector_1")) return false;
    fieldSelector(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '{' KeyValuePairsType '}' (Schema)?
  public static boolean ObjectType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_TYPE, null);
    r = consumeToken(b, L_CURLY);
    p = r; // pin = 1
    r = r && report_error_(b, KeyValuePairsType(b, l + 1));
    r = p && report_error_(b, consumeToken(b, R_CURLY)) && r;
    r = p && ObjectType_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (Schema)?
  private static boolean ObjectType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_3")) return false;
    ObjectType_3_0(b, l + 1);
    return true;
  }

  // (Schema)
  private static boolean ObjectType_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Schema(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier '=' LiteralExpression
  public static boolean OptionElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OptionElement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OPTION_ELEMENT, "<option element>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, EQ);
    p = r; // pin = 2
    r = r && LiteralExpression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // OptionElement ( ',' OptionElement )*
  public static boolean Options(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Options")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPTIONS, "<options>");
    r = OptionElement(b, l + 1);
    r = r && Options_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( ',' OptionElement )*
  private static boolean Options_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Options_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Options_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Options_1", c)) break;
    }
    return true;
  }

  // ',' OptionElement
  private static boolean Options_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Options_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && OptionElement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{-' ((KeyValuePairType)? (',' KeyValuePairType)*)?  '-}' (Schema)?
  public static boolean OrderedObjectType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType")) return false;
    if (!nextTokenIs(b, OPEN_ORDERED_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ORDERED_OBJECT_TYPE, null);
    r = consumeToken(b, OPEN_ORDERED_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, OrderedObjectType_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, CLOSE_ORDERED_KEYWORD)) && r;
    r = p && OrderedObjectType_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((KeyValuePairType)? (',' KeyValuePairType)*)?
  private static boolean OrderedObjectType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_1")) return false;
    OrderedObjectType_1_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)? (',' KeyValuePairType)*
  private static boolean OrderedObjectType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = OrderedObjectType_1_0_0(b, l + 1);
    r = r && OrderedObjectType_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (KeyValuePairType)?
  private static boolean OrderedObjectType_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_1_0_0")) return false;
    OrderedObjectType_1_0_0_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)
  private static boolean OrderedObjectType_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' KeyValuePairType)*
  private static boolean OrderedObjectType_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!OrderedObjectType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "OrderedObjectType_1_0_1", c)) break;
    }
    return true;
  }

  // ',' KeyValuePairType
  private static boolean OrderedObjectType_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (Schema)?
  private static boolean OrderedObjectType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_3")) return false;
    OrderedObjectType_3_0(b, l + 1);
    return true;
  }

  // (Schema)
  private static boolean OrderedObjectType_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OrderedObjectType_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Schema(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Annotation* OUTPUT_DIRECTIVE_KEYWORD (":" Type)? ((DataFormat ('with' Identifier)?) | Identifier) Options?
  public static boolean OutputDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective")) return false;
    if (!nextTokenIs(b, "<output directive>", AT, OUTPUT_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OUTPUT_DIRECTIVE, "<output directive>");
    r = OutputDirective_0(b, l + 1);
    r = r && consumeToken(b, OUTPUT_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && report_error_(b, OutputDirective_2(b, l + 1));
    r = p && report_error_(b, OutputDirective_3(b, l + 1)) && r;
    r = p && OutputDirective_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean OutputDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "OutputDirective_0", c)) break;
    }
    return true;
  }

  // (":" Type)?
  private static boolean OutputDirective_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_2")) return false;
    OutputDirective_2_0(b, l + 1);
    return true;
  }

  // ":" Type
  private static boolean OutputDirective_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DataFormat ('with' Identifier)?) | Identifier
  private static boolean OutputDirective_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = OutputDirective_3_0(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DataFormat ('with' Identifier)?
  private static boolean OutputDirective_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DataFormat(b, l + 1);
    r = r && OutputDirective_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('with' Identifier)?
  private static boolean OutputDirective_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_3_0_1")) return false;
    OutputDirective_3_0_1_0(b, l + 1);
    return true;
  }

  // 'with' Identifier
  private static boolean OutputDirective_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "with");
    r = r && Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Options?
  private static boolean OutputDirective_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_4")) return false;
    Options(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // RegexPattern
  //           | NamedRegexPattern
  //           | EmptyArrayPattern
  //           | EmptyObjectPattern
  //           | ObjectDeconstructPattern
  //           | ArrayDeconstructPattern
  //           | TypePattern
  //           | NamedTypePattern
  //           | LiteralPattern
  //           | NamedLiteralPattern
  //           | ExpressionPattern
  public static boolean Pattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Pattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, PATTERN, "<pattern>");
    r = RegexPattern(b, l + 1);
    if (!r) r = NamedRegexPattern(b, l + 1);
    if (!r) r = EmptyArrayPattern(b, l + 1);
    if (!r) r = EmptyObjectPattern(b, l + 1);
    if (!r) r = ObjectDeconstructPattern(b, l + 1);
    if (!r) r = ArrayDeconstructPattern(b, l + 1);
    if (!r) r = TypePattern(b, l + 1);
    if (!r) r = NamedTypePattern(b, l + 1);
    if (!r) r = LiteralPattern(b, l + 1);
    if (!r) r = NamedLiteralPattern(b, l + 1);
    if (!r) r = ExpressionPattern(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' ('case' Pattern)+ (DefaultPattern)? '}'
  public static boolean PatternMatcherExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PatternMatcherExpression")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PATTERN_MATCHER_EXPRESSION, null);
    r = consumeToken(b, L_CURLY);
    r = r && PatternMatcherExpression_1(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, PatternMatcherExpression_2(b, l + 1));
    r = p && consumeToken(b, R_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ('case' Pattern)+
  private static boolean PatternMatcherExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PatternMatcherExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = PatternMatcherExpression_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!PatternMatcherExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "PatternMatcherExpression_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // 'case' Pattern
  private static boolean PatternMatcherExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PatternMatcherExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE_KEYWORD);
    r = r && Pattern(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DefaultPattern)?
  private static boolean PatternMatcherExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PatternMatcherExpression_2")) return false;
    PatternMatcherExpression_2_0(b, l + 1);
    return true;
  }

  // (DefaultPattern)
  private static boolean PatternMatcherExpression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PatternMatcherExpression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DefaultPattern(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DeclaredNamespace? (Identifier | StringLiteral)
  public static boolean QualifiedName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "QualifiedName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, QUALIFIED_NAME, "<Key Name>");
    r = QualifiedName_0(b, l + 1);
    r = r && QualifiedName_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DeclaredNamespace?
  private static boolean QualifiedName_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "QualifiedName_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // Identifier | StringLiteral
  private static boolean QualifiedName_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "QualifiedName_1")) return false;
    boolean r;
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // FqnIdentifier ('<' Type (',' Type)* '>')? ('.' (StringLiteral|Identifier))*
  public static boolean ReferenceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REFERENCE_TYPE, "<reference type>");
    r = FqnIdentifier(b, l + 1);
    r = r && ReferenceType_1(b, l + 1);
    p = r; // pin = 2
    r = r && ReferenceType_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ('<' Type (',' Type)* '>')?
  private static boolean ReferenceType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_1")) return false;
    ReferenceType_1_0(b, l + 1);
    return true;
  }

  // '<' Type (',' Type)* '>'
  private static boolean ReferenceType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESS);
    r = r && Type(b, l + 1);
    r = r && ReferenceType_1_0_2(b, l + 1);
    r = r && consumeToken(b, GREATER);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' Type)*
  private static boolean ReferenceType_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_1_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ReferenceType_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ReferenceType_1_0_2", c)) break;
    }
    return true;
  }

  // ',' Type
  private static boolean ReferenceType_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.' (StringLiteral|Identifier))*
  private static boolean ReferenceType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ReferenceType_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ReferenceType_2", c)) break;
    }
    return true;
  }

  // '.' (StringLiteral|Identifier)
  private static boolean ReferenceType_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && ReferenceType_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // StringLiteral|Identifier
  private static boolean ReferenceType_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_2_0_1")) return false;
    boolean r;
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // RULE_ANY_REGEX
  public static boolean RegexLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RegexLiteral")) return false;
    if (!nextTokenIs(b, RULE_ANY_REGEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RULE_ANY_REGEX);
    exit_section_(b, m, REGEX_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // 'matches' RegexLiteral '->' Expression
  public static boolean RegexPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RegexPattern")) return false;
    if (!nextTokenIs(b, MATCHES_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REGEX_PATTERN, null);
    r = consumeToken(b, MATCHES_KEYWORD);
    r = r && RegexLiteral(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 3
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' ( SchemaElement ( ',' SchemaElement )* )? (",")? '}'
  public static boolean Schema(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SCHEMA, null);
    r = consumeToken(b, L_CURLY);
    p = r; // pin = 1
    r = r && report_error_(b, Schema_1(b, l + 1));
    r = p && report_error_(b, Schema_2(b, l + 1)) && r;
    r = p && consumeToken(b, R_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( SchemaElement ( ',' SchemaElement )* )?
  private static boolean Schema_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema_1")) return false;
    Schema_1_0(b, l + 1);
    return true;
  }

  // SchemaElement ( ',' SchemaElement )*
  private static boolean Schema_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SchemaElement(b, l + 1);
    r = r && Schema_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' SchemaElement )*
  private static boolean Schema_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Schema_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Schema_1_0_1", c)) break;
    }
    return true;
  }

  // ',' SchemaElement
  private static boolean Schema_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && SchemaElement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (",")?
  private static boolean Schema_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // SchemaKV | ConditionalSchemaKV
  public static boolean SchemaElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCHEMA_ELEMENT, "<schema element>");
    r = SchemaKV(b, l + 1);
    if (!r) r = ConditionalSchemaKV(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (Identifier | StringLiteral) ':' Expression
  public static boolean SchemaKV(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaKV")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SCHEMA_KV, "<schema kv>");
    r = SchemaKV_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 2
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Identifier | StringLiteral
  private static boolean SchemaKV_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaKV_0")) return false;
    boolean r;
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '^'fieldSelector?
  public static boolean SchemaSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector")) return false;
    if (!nextTokenIs(b, XOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SCHEMA_SELECTOR, null);
    r = consumeToken(b, XOR);
    p = r; // pin = 1
    r = r && SchemaSelector_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // fieldSelector?
  private static boolean SchemaSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector_1")) return false;
    fieldSelector(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ValueSelector |
  //          AllAttributeSelector |
  //          AttributeSelector |
  //          NamespaceSelector |
  //          AllSchemaSelector |
  //          SchemaSelector |
  //          ObjectSelector |
  //          AttributeMultiValueSelector|
  //          MultiValueSelector
  public static boolean Selector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Selector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECTOR, "<selector>");
    r = ValueSelector(b, l + 1);
    if (!r) r = AllAttributeSelector(b, l + 1);
    if (!r) r = AttributeSelector(b, l + 1);
    if (!r) r = NamespaceSelector(b, l + 1);
    if (!r) r = AllSchemaSelector(b, l + 1);
    if (!r) r = SchemaSelector(b, l + 1);
    if (!r) r = ObjectSelector(b, l + 1);
    if (!r) r = AttributeMultiValueSelector(b, l + 1);
    if (!r) r = MultiValueSelector(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // QualifiedName ':' Expression
  public static boolean SimpleAttribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleAttribute")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_ATTRIBUTE, "<simple attribute>");
    r = QualifiedName(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KeyValuePair | '(' KeyValuePair ')' (conditionalKV)
  static boolean SingleKeyValuePairObj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SingleKeyValuePairObj")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePair(b, l + 1);
    if (!r) r = SingleKeyValuePairObj_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' KeyValuePair ')' (conditionalKV)
  private static boolean SingleKeyValuePairObj_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SingleKeyValuePairObj_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && KeyValuePair(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    r = r && SingleKeyValuePairObj_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (conditionalKV)
  private static boolean SingleKeyValuePairObj_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SingleKeyValuePairObj_1_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = conditionalKV(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DOUBLE_QUOTED_STRING
  //                       | BACKTIKED_QUOTED_STRING
  //                       | SINGLE_QUOTED_STRING
  public static boolean StringLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StringLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_LITERAL, "<string literal>");
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, BACKTIKED_QUOTED_STRING);
    if (!r) r = consumeToken(b, SINGLE_QUOTED_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // UnionType
  public static boolean Type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, TYPE, "<type>");
    r = UnionType(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Identifier TypeParameterDeclaration? '=' (TypeLiteral | UndefinedLiteral)
  public static boolean TypeDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPE_DEFINITION, "<type definition>");
    r = Identifier(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, TypeDefinition_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, EQ)) && r;
    r = p && TypeDefinition_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // TypeParameterDeclaration?
  private static boolean TypeDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDefinition_1")) return false;
    TypeParameterDeclaration(b, l + 1);
    return true;
  }

  // TypeLiteral | UndefinedLiteral
  private static boolean TypeDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDefinition_3")) return false;
    boolean r;
    r = TypeLiteral(b, l + 1);
    if (!r) r = UndefinedLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // Annotation* 'type' TypeDefinition
  public static boolean TypeDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDirective")) return false;
    if (!nextTokenIs(b, "<type directive>", AT, TYPE_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPE_DIRECTIVE, "<type directive>");
    r = TypeDirective_0(b, l + 1);
    r = r && consumeToken(b, TYPE_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && TypeDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean TypeDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeDirective_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Type
  static boolean TypeLiteral(PsiBuilder b, int l) {
    return Type(b, l + 1);
  }

  /* ********************************************************** */
  // Identifier ( '<:' Type)?
  public static boolean TypeParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_PARAMETER, "<type parameter>");
    r = Identifier(b, l + 1);
    r = r && TypeParameter_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( '<:' Type)?
  private static boolean TypeParameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameter_1")) return false;
    TypeParameter_1_0(b, l + 1);
    return true;
  }

  // '<:' Type
  private static boolean TypeParameter_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameter_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SUB_TYPE);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<' (TypeParameter (',' TypeParameter)*)? '>'
  static boolean TypeParameterDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameterDeclaration")) return false;
    if (!nextTokenIs(b, LESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESS);
    r = r && TypeParameterDeclaration_1(b, l + 1);
    r = r && consumeToken(b, GREATER);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TypeParameter (',' TypeParameter)*)?
  private static boolean TypeParameterDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameterDeclaration_1")) return false;
    TypeParameterDeclaration_1_0(b, l + 1);
    return true;
  }

  // TypeParameter (',' TypeParameter)*
  private static boolean TypeParameterDeclaration_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameterDeclaration_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeParameter(b, l + 1);
    r = r && TypeParameterDeclaration_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' TypeParameter)*
  private static boolean TypeParameterDeclaration_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameterDeclaration_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!TypeParameterDeclaration_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeParameterDeclaration_1_0_1", c)) break;
    }
    return true;
  }

  // ',' TypeParameter
  private static boolean TypeParameterDeclaration_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeParameterDeclaration_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && TypeParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'is' Type '->' Expression
  public static boolean TypePattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypePattern")) return false;
    if (!nextTokenIs(b, IS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPE_PATTERN, null);
    r = consumeToken(b, IS);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 3
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // IntersectionType UnionTypeExpression*
  public static boolean UnionType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UnionType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, UNION_TYPE, "<union type>");
    r = IntersectionType(b, l + 1);
    r = r && UnionType_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // UnionTypeExpression*
  private static boolean UnionType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UnionType_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!UnionTypeExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "UnionType_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '|' IntersectionType
  static boolean UnionTypeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UnionTypeExpression")) return false;
    if (!nextTokenIs(b, OR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OR);
    r = r && IntersectionType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ((Identifier | '(' Identifier ',' Identifier ')') 'at')? UpdateSelector ('!')? (IF EnclosedExpression)? '->' Expression
  public static boolean UpdateCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_CASE, "<update case>");
    r = UpdateCase_0(b, l + 1);
    r = r && UpdateSelector(b, l + 1);
    r = r && UpdateCase_2(b, l + 1);
    r = r && UpdateCase_3(b, l + 1);
    p = r; // pin = 4
    r = r && report_error_(b, consumeToken(b, ARROW_TOKEN));
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((Identifier | '(' Identifier ',' Identifier ')') 'at')?
  private static boolean UpdateCase_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_0")) return false;
    UpdateCase_0_0(b, l + 1);
    return true;
  }

  // (Identifier | '(' Identifier ',' Identifier ')') 'at'
  private static boolean UpdateCase_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = UpdateCase_0_0_0(b, l + 1);
    r = r && consumeToken(b, AT_KEYWORD);
    exit_section_(b, m, null, r);
    return r;
  }

  // Identifier | '(' Identifier ',' Identifier ')'
  private static boolean UpdateCase_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    if (!r) r = UpdateCase_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' Identifier ',' Identifier ')'
  private static boolean UpdateCase_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_0_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && Identifier(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && Identifier(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('!')?
  private static boolean UpdateCase_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_2")) return false;
    consumeToken(b, ESCLAMATION);
    return true;
  }

  // (IF EnclosedExpression)?
  private static boolean UpdateCase_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_3")) return false;
    UpdateCase_3_0(b, l + 1);
    return true;
  }

  // IF EnclosedExpression
  private static boolean UpdateCase_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCase_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && EnclosedExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' ('case' UpdateCase)+ '}'
  public static boolean UpdateCases(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCases")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, UPDATE_CASES, null);
    r = consumeToken(b, L_CURLY);
    r = r && UpdateCases_1(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ('case' UpdateCase)+
  private static boolean UpdateCases_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCases_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = UpdateCases_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!UpdateCases_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "UpdateCases_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // 'case' UpdateCase
  private static boolean UpdateCases_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateCases_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE_KEYWORD);
    r = r && UpdateCase(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (('.' (ValueSelector | MultiValueSelector | AttributeSelector)) | '[' Expression ']') (UpdateSelector)*
  static boolean UpdateSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = UpdateSelector_0(b, l + 1);
    r = r && UpdateSelector_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.' (ValueSelector | MultiValueSelector | AttributeSelector)) | '[' Expression ']'
  private static boolean UpdateSelector_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = UpdateSelector_0_0(b, l + 1);
    if (!r) r = UpdateSelector_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '.' (ValueSelector | MultiValueSelector | AttributeSelector)
  private static boolean UpdateSelector_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ".");
    r = r && UpdateSelector_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ValueSelector | MultiValueSelector | AttributeSelector
  private static boolean UpdateSelector_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector_0_0_1")) return false;
    boolean r;
    r = ValueSelector(b, l + 1);
    if (!r) r = MultiValueSelector(b, l + 1);
    if (!r) r = AttributeSelector(b, l + 1);
    return r;
  }

  // '[' Expression ']'
  private static boolean UpdateSelector_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // (UpdateSelector)*
  private static boolean UpdateSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!UpdateSelector_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "UpdateSelector_1", c)) break;
    }
    return true;
  }

  // (UpdateSelector)
  private static boolean UpdateSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateSelector_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = UpdateSelector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // fieldSelector
  public static boolean ValueSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueSelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_SELECTOR, "<value selector>");
    r = fieldSelector(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VariableNameTypeDefinition '='  Expression
  public static boolean VariableDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DEFINITION, "<variable definition>");
    r = VariableNameTypeDefinition(b, l + 1);
    r = r && consumeToken(b, EQ);
    p = r; // pin = 2
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Annotation* 'var' VariableDefinition
  public static boolean VariableDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableDirective")) return false;
    if (!nextTokenIs(b, "<variable directive>", AT, VAR_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DIRECTIVE, "<variable directive>");
    r = VariableDirective_0(b, l + 1);
    r = r && consumeToken(b, VAR_DIRECTIVE_KEYWORD);
    p = r; // pin = 2
    r = r && VariableDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean VariableDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "VariableDirective_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Annotation* Identifier (":" Type?)?
  static boolean VariableNameTypeDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableNameTypeDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VariableNameTypeDefinition_0(b, l + 1);
    r = r && Identifier(b, l + 1);
    r = r && VariableNameTypeDefinition_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Annotation*
  private static boolean VariableNameTypeDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableNameTypeDefinition_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "VariableNameTypeDefinition_0", c)) break;
    }
    return true;
  }

  // (":" Type?)?
  private static boolean VariableNameTypeDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableNameTypeDefinition_2")) return false;
    VariableNameTypeDefinition_2_0(b, l + 1);
    return true;
  }

  // ":" Type?
  private static boolean VariableNameTypeDefinition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableNameTypeDefinition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && VariableNameTypeDefinition_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Type?
  private static boolean VariableNameTypeDefinition_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableNameTypeDefinition_2_0_1")) return false;
    Type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Annotation* '%dw'  DOUBLE_LITERAL
  public static boolean VersionDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VersionDirective")) return false;
    if (!nextTokenIs(b, "<version directive>", AT, VERSION_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VERSION_DIRECTIVE, "<version directive>");
    r = VersionDirective_0(b, l + 1);
    r = r && consumeTokens(b, 1, VERSION_DIRECTIVE_KEYWORD, DOUBLE_LITERAL);
    p = r; // pin = 2
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Annotation*
  private static boolean VersionDirective_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VersionDirective_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Annotation(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "VersionDirective_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // IF Expression
  static boolean conditionalKV(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conditionalKV")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (Attributes)? ':' Expression
  static boolean dynamicKV(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamicKV")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = dynamicKV_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 2
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (Attributes)?
  private static boolean dynamicKV_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamicKV_0")) return false;
    dynamicKV_0_0(b, l + 1);
    return true;
  }

  // (Attributes)
  private static boolean dynamicKV_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamicKV_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Attributes(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DeclaredNamespace? (StringLiteral|Identifier)
  static boolean fieldSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldSelector")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fieldSelector_0(b, l + 1);
    r = r && fieldSelector_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DeclaredNamespace?
  private static boolean fieldSelector_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldSelector_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // StringLiteral|Identifier
  private static boolean fieldSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fieldSelector_1")) return false;
    boolean r;
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '*''@'fieldSelector
  static boolean multiAttributeSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiAttributeSelector")) return false;
    if (!nextTokenIs(b, MULTIPLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 2, MULTIPLY, AT);
    p = r; // pin = 2
    r = r && fieldSelector(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '@' '*' fieldSelector
  static boolean multiAttributeSelectorOld(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiAttributeSelectorOld")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 2, AT, MULTIPLY);
    p = r; // pin = 2
    r = r && fieldSelector(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KeyValuePair | DynamicKeyValuePair
  static boolean objectEntryRule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectEntryRule")) return false;
    boolean r;
    r = KeyValuePair(b, l + 1);
    if (!r) r = DynamicKeyValuePair(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // Document
  static boolean root(PsiBuilder b, int l) {
    return Document(b, l + 1);
  }

  /* ********************************************************** */
  // Expression root: Expression
  // Operator priority table:
  // 0: POSTFIX(MatchExpression)
  // 1: POSTFIX(UpdateExpression)
  // 2: ATOM(CustomInterpolatorExpression)
  // 3: BINARY(DefaultValueExpression)
  // 4: BINARY(BinaryExpression)
  // 5: BINARY(OrExpression) BINARY(AndExpression) BINARY(EqualityExpression) POSTFIX(FunctionCallExpression)
  //    BINARY(GreaterThanExpression) BINARY(AdditionSubtractionExpression) BINARY(RightShiftExpression) BINARY(LeftShiftExpression)
  //    BINARY(MultiplicationDivisionExpression) POSTFIX(AsExpression) POSTFIX(IsExpression) POSTFIX(DotSelectorExpression)
  //    POSTFIX(BracketSelectorExpression) ATOM(UndefinedLiteral) ATOM(UnaryMinusExpression) ATOM(NotExpression)
  //    ATOM(ConditionalExpression) ATOM(UsingExpression) ATOM(DoExpression) ATOM(LambdaLiteral)
  //    ATOM(ObjectDeconstructExpression) ATOM(ObjectExpression) ATOM(ArrayExpression) ATOM(VariableReferenceExpression)
  //    ATOM(LiteralExpression) PREFIX(EnclosedExpression)
  public static boolean Expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "Expression")) return false;
    addVariant(b, "<expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = CustomInterpolatorExpression(b, l + 1);
    if (!r) r = UndefinedLiteral(b, l + 1);
    if (!r) r = UnaryMinusExpression(b, l + 1);
    if (!r) r = NotExpression(b, l + 1);
    if (!r) r = ConditionalExpression(b, l + 1);
    if (!r) r = UsingExpression(b, l + 1);
    if (!r) r = DoExpression(b, l + 1);
    if (!r) r = LambdaLiteral(b, l + 1);
    if (!r) r = ObjectDeconstructExpression(b, l + 1);
    if (!r) r = ObjectExpression(b, l + 1);
    if (!r) r = ArrayExpression(b, l + 1);
    if (!r) r = VariableReferenceExpression(b, l + 1);
    if (!r) r = LiteralExpression(b, l + 1);
    if (!r) r = EnclosedExpression(b, l + 1);
    p = r;
    r = r && Expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean Expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "Expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && MatchExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, MATCH_EXPRESSION, r, true, null);
      }
      else if (g < 1 && UpdateExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, UPDATE_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, DEFAULT)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, DEFAULT_VALUE_EXPRESSION, r, true, null);
      }
      else if (g < 4 && BinaryFunctionIdentifier(b, l + 1)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, BINARY_EXPRESSION, r, true, null);
      }
      else if (g < 5 && consumeTokenSmart(b, OR_KEYWORD)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, OR_EXPRESSION, r, true, null);
      }
      else if (g < 5 && consumeTokenSmart(b, AND_KEYWORD)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, AND_EXPRESSION, r, true, null);
      }
      else if (g < 5 && EqualityExpression_0(b, l + 1)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, EQUALITY_EXPRESSION, r, true, null);
      }
      else if (g < 5 && FunctionCallExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, FUNCTION_CALL_EXPRESSION, r, true, null);
      }
      else if (g < 5 && GreaterThanExpression_0(b, l + 1)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, GREATER_THAN_EXPRESSION, r, true, null);
      }
      else if (g < 5 && AdditionSubtractionExpression_0(b, l + 1)) {
        r = Expression(b, l, 5);
        exit_section_(b, l, m, ADDITION_SUBTRACTION_EXPRESSION, r, true, null);
      }
      else if (g < 5 && parseTokensSmart(b, 0, GREATER, GREATER)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, RIGHT_SHIFT_EXPRESSION, r, true, null);
      }
      else if (g < 5 && parseTokensSmart(b, 0, LESS, LESS)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, LEFT_SHIFT_EXPRESSION, r, true, null);
      }
      else if (g < 5 && MultiplicationDivisionExpression_0(b, l + 1)) {
        r = Expression(b, l, 5);
        exit_section_(b, l, m, MULTIPLICATION_DIVISION_EXPRESSION, r, true, null);
      }
      else if (g < 5 && AsExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, AS_EXPRESSION, r, true, null);
      }
      else if (g < 5 && IsExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, IS_EXPRESSION, r, true, null);
      }
      else if (g < 5 && DotSelectorExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, DOT_SELECTOR_EXPRESSION, r, true, null);
      }
      else if (g < 5 && BracketSelectorExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, BRACKET_SELECTOR_EXPRESSION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // 'match' (PatternMatcherExpression )
  private static boolean MatchExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MatchExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, MATCH_KEYWORD);
    r = r && MatchExpression_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PatternMatcherExpression )
  private static boolean MatchExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MatchExpression_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = PatternMatcherExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'update' (UpdateCases)
  private static boolean UpdateExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, UPDATE_KEYWORD);
    r = r && UpdateExpression_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (UpdateCases)
  private static boolean UpdateExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UpdateExpression_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = UpdateCases(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Identifier CustomInterpolationString
  public static boolean CustomInterpolatorExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomInterpolatorExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_INTERPOLATOR_EXPRESSION, "<custom interpolator expression>");
    r = Identifier(b, l + 1);
    r = r && CustomInterpolationString(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '==' | '!=' | '~='
  private static boolean EqualityExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EqualityExpression_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, EQUAL);
    if (!r) r = consumeTokenSmart(b, NOT_EQUAL);
    if (!r) r = consumeTokenSmart(b, SIMILAR);
    return r;
  }

  // FunctionCallTypeParameters? FunctionCallArguments
  private static boolean FunctionCallExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FunctionCallExpression_0_0(b, l + 1);
    r = r && FunctionCallArguments(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FunctionCallTypeParameters?
  private static boolean FunctionCallExpression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0_0")) return false;
    FunctionCallTypeParameters(b, l + 1);
    return true;
  }

  // '>' !('>') | '>=' |  '<' !('<') | '<='
  private static boolean GreaterThanExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GreaterThanExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GreaterThanExpression_0_0(b, l + 1);
    if (!r) r = consumeTokenSmart(b, GREATER_EQUAL);
    if (!r) r = GreaterThanExpression_0_2(b, l + 1);
    if (!r) r = consumeTokenSmart(b, LESS_EQUAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // '>' !('>')
  private static boolean GreaterThanExpression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GreaterThanExpression_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, GREATER);
    r = r && GreaterThanExpression_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !('>')
  private static boolean GreaterThanExpression_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GreaterThanExpression_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeTokenSmart(b, GREATER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '<' !('<')
  private static boolean GreaterThanExpression_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GreaterThanExpression_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LESS);
    r = r && GreaterThanExpression_0_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !('<')
  private static boolean GreaterThanExpression_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GreaterThanExpression_0_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeTokenSmart(b, LESS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '+' | '-'
  private static boolean AdditionSubtractionExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AdditionSubtractionExpression_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, PLUS);
    if (!r) r = consumeTokenSmart(b, MINUS);
    return r;
  }

  // '*' | '/'
  private static boolean MultiplicationDivisionExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiplicationDivisionExpression_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, MULTIPLY);
    if (!r) r = consumeTokenSmart(b, DIVISION);
    return r;
  }

  // 'as' TypeLiteral
  private static boolean AsExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AsExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, AS);
    r = r && TypeLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IS TypeLiteral
  private static boolean IsExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IsExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, IS);
    r = r && TypeLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( '..' | '.' ) Selector? ('!'| '?')?
  private static boolean DotSelectorExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DotSelectorExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DotSelectorExpression_0_0(b, l + 1);
    r = r && DotSelectorExpression_0_1(b, l + 1);
    r = r && DotSelectorExpression_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '..' | '.'
  private static boolean DotSelectorExpression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DotSelectorExpression_0_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, "..");
    if (!r) r = consumeTokenSmart(b, ".");
    return r;
  }

  // Selector?
  private static boolean DotSelectorExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DotSelectorExpression_0_1")) return false;
    Selector(b, l + 1);
    return true;
  }

  // ('!'| '?')?
  private static boolean DotSelectorExpression_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DotSelectorExpression_0_2")) return false;
    DotSelectorExpression_0_2_0(b, l + 1);
    return true;
  }

  // '!'| '?'
  private static boolean DotSelectorExpression_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DotSelectorExpression_0_2_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, ESCLAMATION);
    if (!r) r = consumeTokenSmart(b, QUESTION);
    return r;
  }

  // '[' (('?' |'@' | '&' | '^' |'*')? ( '?' | '*' | '@')? DeclaredNamespace?  (Expression)) ']' ('!'| '?')?
  private static boolean BracketSelectorExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, L_BRACKET);
    r = r && BracketSelectorExpression_0_1(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    r = r && BracketSelectorExpression_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('?' |'@' | '&' | '^' |'*')? ( '?' | '*' | '@')? DeclaredNamespace?  (Expression)
  private static boolean BracketSelectorExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = BracketSelectorExpression_0_1_0(b, l + 1);
    r = r && BracketSelectorExpression_0_1_1(b, l + 1);
    r = r && BracketSelectorExpression_0_1_2(b, l + 1);
    r = r && BracketSelectorExpression_0_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('?' |'@' | '&' | '^' |'*')?
  private static boolean BracketSelectorExpression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_0")) return false;
    BracketSelectorExpression_0_1_0_0(b, l + 1);
    return true;
  }

  // '?' |'@' | '&' | '^' |'*'
  private static boolean BracketSelectorExpression_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_0_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, QUESTION);
    if (!r) r = consumeTokenSmart(b, AT);
    if (!r) r = consumeTokenSmart(b, AND);
    if (!r) r = consumeTokenSmart(b, XOR);
    if (!r) r = consumeTokenSmart(b, MULTIPLY);
    return r;
  }

  // ( '?' | '*' | '@')?
  private static boolean BracketSelectorExpression_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_1")) return false;
    BracketSelectorExpression_0_1_1_0(b, l + 1);
    return true;
  }

  // '?' | '*' | '@'
  private static boolean BracketSelectorExpression_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_1_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, QUESTION);
    if (!r) r = consumeTokenSmart(b, MULTIPLY);
    if (!r) r = consumeTokenSmart(b, AT);
    return r;
  }

  // DeclaredNamespace?
  private static boolean BracketSelectorExpression_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_2")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // (Expression)
  private static boolean BracketSelectorExpression_0_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('!'| '?')?
  private static boolean BracketSelectorExpression_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_3")) return false;
    BracketSelectorExpression_0_3_0(b, l + 1);
    return true;
  }

  // '!'| '?'
  private static boolean BracketSelectorExpression_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_3_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, ESCLAMATION);
    if (!r) r = consumeTokenSmart(b, QUESTION);
    return r;
  }

  // '?''?''?'
  public static boolean UndefinedLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UndefinedLiteral")) return false;
    if (!nextTokenIsSmart(b, QUESTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, QUESTION, QUESTION, QUESTION);
    exit_section_(b, m, UNDEFINED_LITERAL, r);
    return r;
  }

  // '-' SimpleExpression
  public static boolean UnaryMinusExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UnaryMinusExpression")) return false;
    if (!nextTokenIsSmart(b, MINUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, UNARY_MINUS_EXPRESSION, null);
    r = consumeTokenSmart(b, MINUS);
    p = r; // pin = 1
    r = r && Expression(b, l + 1, 4);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ('!' | 'not') ValueExpression
  public static boolean NotExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NotExpression")) return false;
    if (!nextTokenIsSmart(b, ESCLAMATION, NOT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _COLLAPSE_, NOT_EXPRESSION, "<not expression>");
    r = NotExpression_0(b, l + 1);
    p = r; // pin = 1
    r = r && Expression(b, l + 1, 4);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '!' | 'not'
  private static boolean NotExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NotExpression_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, ESCLAMATION);
    if (!r) r = consumeTokenSmart(b, NOT_KEYWORD);
    return r;
  }

  // ( IF | UNLESS ) '(' Expression ')' Expression ELSE Expression
  public static boolean ConditionalExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalExpression")) return false;
    if (!nextTokenIsSmart(b, IF, UNLESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _COLLAPSE_, CONDITIONAL_EXPRESSION, "<conditional expression>");
    r = ConditionalExpression_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, L_PARREN));
    r = p && report_error_(b, Expression(b, l + 1, -1)) && r;
    r = p && report_error_(b, consumeToken(b, R_PARREN)) && r;
    r = p && report_error_(b, Expression(b, l + 1, -1)) && r;
    r = p && report_error_(b, consumeToken(b, ELSE)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // IF | UNLESS
  private static boolean ConditionalExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalExpression_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, IF);
    if (!r) r = consumeTokenSmart(b, UNLESS);
    return r;
  }

  // USING '(' VariableDefinition ( ',' VariableDefinition )*  (",")? ')' Expression
  public static boolean UsingExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingExpression")) return false;
    if (!nextTokenIsSmart(b, USING)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, USING_EXPRESSION, null);
    r = consumeTokensSmart(b, 1, USING, L_PARREN);
    p = r; // pin = 1
    r = r && report_error_(b, VariableDefinition(b, l + 1));
    r = p && report_error_(b, UsingExpression_3(b, l + 1)) && r;
    r = p && report_error_(b, UsingExpression_4(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, R_PARREN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( ',' VariableDefinition )*
  private static boolean UsingExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingExpression_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!UsingExpression_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "UsingExpression_3", c)) break;
    }
    return true;
  }

  // ',' VariableDefinition
  private static boolean UsingExpression_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingExpression_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COMMA);
    r = r && VariableDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (",")?
  private static boolean UsingExpression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingExpression_4")) return false;
    consumeTokenSmart(b, COMMA);
    return true;
  }

  // 'do' '{' (DoDirectives+ '---')?  Expression'}'
  public static boolean DoExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression")) return false;
    if (!nextTokenIsSmart(b, DO_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_EXPRESSION, null);
    r = consumeTokensSmart(b, 1, DO_KEYWORD, L_CURLY);
    p = r; // pin = 1
    r = r && report_error_(b, DoExpression_2(b, l + 1));
    r = p && report_error_(b, Expression(b, l + 1, -1)) && r;
    r = p && consumeToken(b, R_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (DoDirectives+ '---')?
  private static boolean DoExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression_2")) return false;
    DoExpression_2_0(b, l + 1);
    return true;
  }

  // DoDirectives+ '---'
  private static boolean DoExpression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DoExpression_2_0_0(b, l + 1);
    r = r && consumeToken(b, DOCUMENT_SEPARATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  // DoDirectives+
  private static boolean DoExpression_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DoDirectives(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!DoDirectives(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "DoExpression_2_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // TypeParameterDeclaration? '(' ( FunctionParameter ( ',' FunctionParameter )* )? (",")? ')' (':' Type?)?  '->' SimpleExpression
  public static boolean LambdaLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral")) return false;
    if (!nextTokenIsSmart(b, LESS, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LAMBDA_LITERAL, "<lambda literal>");
    r = LambdaLiteral_0(b, l + 1);
    r = r && consumeToken(b, L_PARREN);
    r = r && LambdaLiteral_2(b, l + 1);
    r = r && LambdaLiteral_3(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    r = r && LambdaLiteral_5(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 7
    r = r && Expression(b, l + 1, 4);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // TypeParameterDeclaration?
  private static boolean LambdaLiteral_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_0")) return false;
    TypeParameterDeclaration(b, l + 1);
    return true;
  }

  // ( FunctionParameter ( ',' FunctionParameter )* )?
  private static boolean LambdaLiteral_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_2")) return false;
    LambdaLiteral_2_0(b, l + 1);
    return true;
  }

  // FunctionParameter ( ',' FunctionParameter )*
  private static boolean LambdaLiteral_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FunctionParameter(b, l + 1);
    r = r && LambdaLiteral_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' FunctionParameter )*
  private static boolean LambdaLiteral_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!LambdaLiteral_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LambdaLiteral_2_0_1", c)) break;
    }
    return true;
  }

  // ',' FunctionParameter
  private static boolean LambdaLiteral_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COMMA);
    r = r && FunctionParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (",")?
  private static boolean LambdaLiteral_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_3")) return false;
    consumeTokenSmart(b, COMMA);
    return true;
  }

  // (':' Type?)?
  private static boolean LambdaLiteral_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_5")) return false;
    LambdaLiteral_5_0(b, l + 1);
    return true;
  }

  // ':' Type?
  private static boolean LambdaLiteral_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COLON);
    r = r && LambdaLiteral_5_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Type?
  private static boolean LambdaLiteral_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_5_0_1")) return false;
    Type(b, l + 1);
    return true;
  }

  // '{' (KeyValuePair | DynamicKeyValuePair) '~' Expression '}'
  public static boolean ObjectDeconstructExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectDeconstructExpression")) return false;
    if (!nextTokenIsSmart(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_DECONSTRUCT_EXPRESSION, null);
    r = consumeTokenSmart(b, L_CURLY);
    r = r && ObjectDeconstructExpression_1(b, l + 1);
    r = r && consumeToken(b, TILDE);
    p = r; // pin = 3
    r = r && report_error_(b, Expression(b, l + 1, -1));
    r = p && consumeToken(b, R_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // KeyValuePair | DynamicKeyValuePair
  private static boolean ObjectDeconstructExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectDeconstructExpression_1")) return false;
    boolean r;
    r = KeyValuePair(b, l + 1);
    if (!r) r = DynamicKeyValuePair(b, l + 1);
    return r;
  }

  // SingleKeyValuePairObj | MultipleKeyValuePairObj
  public static boolean ObjectExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_EXPRESSION, "<object expression>");
    r = SingleKeyValuePairObj(b, l + 1);
    if (!r) r = MultipleKeyValuePairObj(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '[' (ArrayElement (('~' Expression) | ( ',' ArrayElement )* (',')? )?)? ']'
  public static boolean ArrayExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression")) return false;
    if (!nextTokenIsSmart(b, L_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_EXPRESSION, null);
    r = consumeTokenSmart(b, L_BRACKET);
    p = r; // pin = 1
    r = r && report_error_(b, ArrayExpression_1(b, l + 1));
    r = p && consumeToken(b, R_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (ArrayElement (('~' Expression) | ( ',' ArrayElement )* (',')? )?)?
  private static boolean ArrayExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1")) return false;
    ArrayExpression_1_0(b, l + 1);
    return true;
  }

  // ArrayElement (('~' Expression) | ( ',' ArrayElement )* (',')? )?
  private static boolean ArrayExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayElement(b, l + 1);
    r = r && ArrayExpression_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (('~' Expression) | ( ',' ArrayElement )* (',')? )?
  private static boolean ArrayExpression_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1")) return false;
    ArrayExpression_1_0_1_0(b, l + 1);
    return true;
  }

  // ('~' Expression) | ( ',' ArrayElement )* (',')?
  private static boolean ArrayExpression_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayExpression_1_0_1_0_0(b, l + 1);
    if (!r) r = ArrayExpression_1_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '~' Expression
  private static boolean ArrayExpression_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, TILDE);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' ArrayElement )* (',')?
  private static boolean ArrayExpression_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayExpression_1_0_1_0_1_0(b, l + 1);
    r = r && ArrayExpression_1_0_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' ArrayElement )*
  private static boolean ArrayExpression_1_0_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1_0_1_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ArrayExpression_1_0_1_0_1_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ArrayExpression_1_0_1_0_1_0", c)) break;
    }
    return true;
  }

  // ',' ArrayElement
  private static boolean ArrayExpression_1_0_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COMMA);
    r = r && ArrayElement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',')?
  private static boolean ArrayExpression_1_0_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_0_1_0_1_1")) return false;
    consumeTokenSmart(b, COMMA);
    return true;
  }

  // FqnIdentifier
  public static boolean VariableReferenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableReferenceExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_REFERENCE_EXPRESSION, "<variable reference expression>");
    r = FqnIdentifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BooleanLiteral
  //            | NullLiteral
  //            | StringLiteral
  //            | NumberLiteral
  //            | AnyDateLiteral
  //            | RegexLiteral
  public static boolean LiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, LITERAL_EXPRESSION, "<literal expression>");
    r = BooleanLiteral(b, l + 1);
    if (!r) r = NullLiteral(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    if (!r) r = NumberLiteral(b, l + 1);
    if (!r) r = AnyDateLiteral(b, l + 1);
    if (!r) r = RegexLiteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  public static boolean EnclosedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnclosedExpression")) return false;
    if (!nextTokenIsSmart(b, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, L_PARREN);
    p = r;
    r = p && Expression(b, l, -1);
    r = p && report_error_(b, EnclosedExpression_1(b, l + 1)) && r;
    exit_section_(b, l, m, ENCLOSED_EXPRESSION, r, p, null);
    return r || p;
  }

  // ')' (DynamicSingleKeyValuePair)?
  private static boolean EnclosedExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnclosedExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, R_PARREN);
    r = r && EnclosedExpression_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DynamicSingleKeyValuePair)?
  private static boolean EnclosedExpression_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnclosedExpression_1_1")) return false;
    EnclosedExpression_1_1_0(b, l + 1);
    return true;
  }

  // (DynamicSingleKeyValuePair)
  private static boolean EnclosedExpression_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EnclosedExpression_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DynamicSingleKeyValuePair(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
