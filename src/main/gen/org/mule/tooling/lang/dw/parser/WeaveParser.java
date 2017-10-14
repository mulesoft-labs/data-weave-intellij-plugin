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
    if (t == ANY_DATE_LITERAL) {
      r = AnyDateLiteral(b, 0);
    }
    else if (t == ANY_REGEX_LITERAL) {
      r = AnyRegexLiteral(b, 0);
    }
    else if (t == ARRAY_DECONSTRUCT_PATTERN) {
      r = ArrayDeconstructPattern(b, 0);
    }
    else if (t == ATTRIBUTE) {
      r = Attribute(b, 0);
    }
    else if (t == ATTRIBUTE_ELEMENT) {
      r = AttributeElement(b, 0);
    }
    else if (t == ATTRIBUTE_SELECTOR) {
      r = AttributeSelector(b, 0);
    }
    else if (t == ATTRIBUTES) {
      r = Attributes(b, 0);
    }
    else if (t == ATTRIBUTES_TYPE) {
      r = AttributesType(b, 0);
    }
    else if (t == BODY) {
      r = Body(b, 0);
    }
    else if (t == BOOLEAN_LITERAL) {
      r = BooleanLiteral(b, 0);
    }
    else if (t == CLOSE_OBJECT_TYPE) {
      r = CloseObjectType(b, 0);
    }
    else if (t == CLOSE_ORDERED_OBJECT_TYPE) {
      r = CloseOrderedObjectType(b, 0);
    }
    else if (t == CONDITIONAL_ATTRIBUTE) {
      r = ConditionalAttribute(b, 0);
    }
    else if (t == CONDITIONAL_KEY_VALUE_PAIR) {
      r = ConditionalKeyValuePair(b, 0);
    }
    else if (t == CUSTOM_LOADER) {
      r = CustomLoader(b, 0);
    }
    else if (t == DATA_FORMAT) {
      r = DataFormat(b, 0);
    }
    else if (t == DECLARED_NAMESPACE) {
      r = DeclaredNamespace(b, 0);
    }
    else if (t == DEFAULT_PATTERN) {
      r = DefaultPattern(b, 0);
    }
    else if (t == DIRECTIVE) {
      r = Directive(b, 0);
    }
    else if (t == DOCUMENT) {
      r = Document(b, 0);
    }
    else if (t == DYNAMIC_KEY_VALUE_PAIR) {
      r = DynamicKeyValuePair(b, 0);
    }
    else if (t == EMPTY_ARRAY_PATTERN) {
      r = EmptyArrayPattern(b, 0);
    }
    else if (t == EMPTY_OBJECT_PATTERN) {
      r = EmptyObjectPattern(b, 0);
    }
    else if (t == EXPRESSION) {
      r = Expression(b, 0, -1);
    }
    else if (t == EXPRESSION_PATTERN) {
      r = ExpressionPattern(b, 0);
    }
    else if (t == FQN_IDENTIFIER) {
      r = FqnIdentifier(b, 0);
    }
    else if (t == FUNCTION_DEFINITION) {
      r = FunctionDefinition(b, 0);
    }
    else if (t == FUNCTION_DIRECTIVE) {
      r = FunctionDirective(b, 0);
    }
    else if (t == FUNCTION_PARAMETER) {
      r = FunctionParameter(b, 0);
    }
    else if (t == HEADER) {
      r = Header(b, 0);
    }
    else if (t == IDENTIFIER) {
      r = Identifier(b, 0);
    }
    else if (t == IDENTIFIER_PACKAGE) {
      r = IdentifierPackage(b, 0);
    }
    else if (t == IMPORT_DIRECTIVE) {
      r = ImportDirective(b, 0);
    }
    else if (t == IMPORTED_ELEMENT) {
      r = ImportedElement(b, 0);
    }
    else if (t == INPUT_DIRECTIVE) {
      r = InputDirective(b, 0);
    }
    else if (t == INTERSECTION_TYPE) {
      r = IntersectionType(b, 0);
    }
    else if (t == KEY) {
      r = Key(b, 0);
    }
    else if (t == KEY_EXPRESSION) {
      r = KeyExpression(b, 0);
    }
    else if (t == KEY_TYPE) {
      r = KeyType(b, 0);
    }
    else if (t == KEY_VALUE_PAIR) {
      r = KeyValuePair(b, 0);
    }
    else if (t == KEY_VALUE_PAIR_TYPE) {
      r = KeyValuePairType(b, 0);
    }
    else if (t == LAMBDA_TYPE) {
      r = LambdaType(b, 0);
    }
    else if (t == LAMBDA_TYPE_PARAMETER) {
      r = LambdaTypeParameter(b, 0);
    }
    else if (t == LITERAL_PATTERN) {
      r = LiteralPattern(b, 0);
    }
    else if (t == MODULE_REFERENCE) {
      r = ModuleReference(b, 0);
    }
    else if (t == MULTI_VALUE_SELECTOR) {
      r = MultiValueSelector(b, 0);
    }
    else if (t == MULTIPLE_KEY_VALUE_PAIR_OBJ) {
      r = MultipleKeyValuePairObj(b, 0);
    }
    else if (t == NAME_TYPE) {
      r = NameType(b, 0);
    }
    else if (t == NAMED_LITERAL_PATTERN) {
      r = NamedLiteralPattern(b, 0);
    }
    else if (t == NAMED_REGEX_PATTERN) {
      r = NamedRegexPattern(b, 0);
    }
    else if (t == NAMED_TYPE_PATTERN) {
      r = NamedTypePattern(b, 0);
    }
    else if (t == NAMESPACE_DIRECTIVE) {
      r = NamespaceDirective(b, 0);
    }
    else if (t == NAMESPACE_SELECTOR) {
      r = NamespaceSelector(b, 0);
    }
    else if (t == NULL_LITERAL) {
      r = NullLiteral(b, 0);
    }
    else if (t == NUMBER_LITERAL) {
      r = NumberLiteral(b, 0);
    }
    else if (t == OBJECT_DECONSTRUCT_PATTERN) {
      r = ObjectDeconstructPattern(b, 0);
    }
    else if (t == OBJECT_SELECTOR) {
      r = ObjectSelector(b, 0);
    }
    else if (t == OBJECT_TYPE) {
      r = ObjectType(b, 0);
    }
    else if (t == OPTION_ELEMENT) {
      r = OptionElement(b, 0);
    }
    else if (t == OPTIONS) {
      r = Options(b, 0);
    }
    else if (t == ORDERED_OBJECT_TYPE) {
      r = OrderedObjectType(b, 0);
    }
    else if (t == OUTPUT_DIRECTIVE) {
      r = OutputDirective(b, 0);
    }
    else if (t == PATTERN) {
      r = Pattern(b, 0);
    }
    else if (t == PATTERN_MATCHER_EXPRESSION) {
      r = PatternMatcherExpression(b, 0);
    }
    else if (t == REFERENCE_TYPE) {
      r = ReferenceType(b, 0);
    }
    else if (t == REGEX_PATTERN) {
      r = RegexPattern(b, 0);
    }
    else if (t == SCHEMA) {
      r = Schema(b, 0);
    }
    else if (t == SCHEMA_ELEMENT) {
      r = SchemaElement(b, 0);
    }
    else if (t == SCHEMA_SELECTOR) {
      r = SchemaSelector(b, 0);
    }
    else if (t == SELECTOR) {
      r = Selector(b, 0);
    }
    else if (t == SIMPLE_KEY_VALUE_PAIR) {
      r = SimpleKeyValuePair(b, 0);
    }
    else if (t == SINGLE_KEY_VALUE_PAIR_OBJ) {
      r = SingleKeyValuePairObj(b, 0);
    }
    else if (t == STRING_LITERAL) {
      r = StringLiteral(b, 0);
    }
    else if (t == TYPE) {
      r = Type(b, 0);
    }
    else if (t == TYPE_DIRECTIVE) {
      r = TypeDirective(b, 0);
    }
    else if (t == TYPE_PARAMETER) {
      r = TypeParameter(b, 0);
    }
    else if (t == TYPE_PATTERN) {
      r = TypePattern(b, 0);
    }
    else if (t == UNION_TYPE) {
      r = UnionType(b, 0);
    }
    else if (t == VALUE_SELECTOR) {
      r = ValueSelector(b, 0);
    }
    else if (t == VARIABLE_DEFINITION) {
      r = VariableDefinition(b, 0);
    }
    else if (t == VARIABLE_DIRECTIVE) {
      r = VariableDirective(b, 0);
    }
    else if (t == VERSION_DIRECTIVE) {
      r = VersionDirective(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(DIRECTIVE, FUNCTION_DIRECTIVE, IMPORT_DIRECTIVE, INPUT_DIRECTIVE,
      NAMESPACE_DIRECTIVE, OUTPUT_DIRECTIVE, TYPE_DIRECTIVE, VARIABLE_DIRECTIVE,
      VERSION_DIRECTIVE),
    create_token_set_(ARRAY_DECONSTRUCT_PATTERN, DEFAULT_PATTERN, EMPTY_ARRAY_PATTERN, EMPTY_OBJECT_PATTERN,
      EXPRESSION_PATTERN, LITERAL_PATTERN, NAMED_LITERAL_PATTERN, NAMED_REGEX_PATTERN,
      NAMED_TYPE_PATTERN, OBJECT_DECONSTRUCT_PATTERN, PATTERN, REGEX_PATTERN,
      TYPE_PATTERN),
    create_token_set_(ATTRIBUTES_TYPE, CLOSE_OBJECT_TYPE, CLOSE_ORDERED_OBJECT_TYPE, INTERSECTION_TYPE,
      KEY_TYPE, KEY_VALUE_PAIR_TYPE, LAMBDA_TYPE, NAME_TYPE,
      OBJECT_TYPE, ORDERED_OBJECT_TYPE, REFERENCE_TYPE, TYPE,
      UNION_TYPE),
    create_token_set_(ADDITION_SUBTRACTION_EXPRESSION, AND_EXPRESSION, ANY_DATE_LITERAL, ANY_REGEX_LITERAL,
      ARRAY_EXPRESSION, AS_EXPRESSION, BINARY_EXPRESSION, BOOLEAN_LITERAL,
      BRACKET_SELECTOR_EXPRESSION, CONDITIONAL_EXPRESSION, CUSTOM_INTERPOLATOR_EXPRESSION, DEFAULT_VALUE_EXPRESSION,
      DOT_SELECTOR_EXPRESSION, DO_EXPRESSION, ENCLOSED_EXPRESSION, EQUALITY_EXPRESSION,
      EXPRESSION, FUNCTION_CALL_EXPRESSION, GREATER_THAN_EXPRESSION, IS_EXPRESSION,
      KEY_EXPRESSION, LAMBDA_LITERAL, LEFT_SHIFT_EXPRESSION, LITERAL_EXPRESSION,
      MATCH_EXPRESSION, MULTIPLICATION_DIVISION_EXPRESSION, NOT_EXPRESSION, NULL_LITERAL,
      NUMBER_LITERAL, OBJECT_DECONSTRUCT_EXPRESSION, OBJECT_EXPRESSION, OR_EXPRESSION,
      PATTERN_MATCHER_EXPRESSION, RIGHT_SHIFT_EXPRESSION, STRING_LITERAL, UNARY_MINUS_EXPRESSION,
      UNDEFINED_LITERAL, USING_EXPRESSION, VARIABLE_REFERENCE_EXPRESSION),
  };

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
  // RULE_ANY_REGEX
  public static boolean AnyRegexLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnyRegexLiteral")) return false;
    if (!nextTokenIs(b, RULE_ANY_REGEX)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RULE_ANY_REGEX);
    exit_section_(b, m, ANY_REGEX_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // '[' Identifier '~' Identifier ']' '->' Expression
  public static boolean ArrayDeconstructPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayDeconstructPattern")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_DECONSTRUCT_PATTERN, null);
    r = consumeToken(b, L_BRACKET);
    r = r && Identifier(b, l + 1);
    r = r && consumeToken(b, TILDE);
    p = r; // pin = 3
    r = r && report_error_(b, Identifier(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, R_BRACKET, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ConditionalArrayElement | Expression
  static boolean ArrayElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayElement")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ConditionalArrayElement(b, l + 1);
    if (!r) r = Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KeyExpression ':' Expression
  public static boolean Attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE, "<attribute>");
    r = KeyExpression(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ConditionalAttribute
  //            | Attribute
  //            | EnclosedExpression
  public static boolean AttributeElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_ELEMENT, "<attribute element>");
    r = ConditionalAttribute(b, l + 1);
    if (!r) r = Attribute(b, l + 1);
    if (!r) r = EnclosedExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '@'(DeclaredNamespace? (StringLiteral|Identifier))?
  public static boolean AttributeSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT);
    r = r && AttributeSelector_1(b, l + 1);
    exit_section_(b, m, ATTRIBUTE_SELECTOR, r);
    return r;
  }

  // (DeclaredNamespace? (StringLiteral|Identifier))?
  private static boolean AttributeSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector_1")) return false;
    AttributeSelector_1_0(b, l + 1);
    return true;
  }

  // DeclaredNamespace? (StringLiteral|Identifier)
  private static boolean AttributeSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AttributeSelector_1_0_0(b, l + 1);
    r = r && AttributeSelector_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DeclaredNamespace?
  private static boolean AttributeSelector_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector_1_0_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // StringLiteral|Identifier
  private static boolean AttributeSelector_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeSelector_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '@(' ( AttributeElement ( ',' AttributeElement )* )? ')'
  public static boolean Attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTES, "<attributes>");
    r = consumeToken(b, "@(");
    r = r && Attributes_1(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ( AttributeElement ( ',' AttributeElement )* )?
  private static boolean Attributes_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1")) return false;
    Attributes_1_0(b, l + 1);
    return true;
  }

  // AttributeElement ( ',' AttributeElement )*
  private static boolean Attributes_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AttributeElement(b, l + 1);
    r = r && Attributes_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' AttributeElement )*
  private static boolean Attributes_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!Attributes_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Attributes_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' AttributeElement
  private static boolean Attributes_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attributes_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && AttributeElement(b, l + 1);
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
    int c = current_position_(b);
    while (true) {
      if (!AttributesType_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AttributesType_5", c)) break;
      c = current_position_(b);
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
  // LambdaType | CloseOrderedObjectType | OrderedObjectType | CloseObjectType | ObjectType  | ReferenceType | ('(' Type ')')
  static boolean BasicTypeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = LambdaType(b, l + 1);
    if (!r) r = CloseOrderedObjectType(b, l + 1);
    if (!r) r = OrderedObjectType(b, l + 1);
    if (!r) r = CloseObjectType(b, l + 1);
    if (!r) r = ObjectType(b, l + 1);
    if (!r) r = ReferenceType(b, l + 1);
    if (!r) r = BasicTypeExpression_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' Type ')'
  private static boolean BasicTypeExpression_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BasicTypeExpression_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, m, null, r);
    return r;
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
  // '{|' ((KeyValuePairType)? (',' KeyValuePairType)*)? '|}' (Schema)?
  public static boolean CloseObjectType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType")) return false;
    if (!nextTokenIs(b, OPEN_CLOSE_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPEN_CLOSE_KEYWORD);
    r = r && CloseObjectType_1(b, l + 1);
    r = r && consumeToken(b, CLOSE_CLOSE_KEYWORD);
    r = r && CloseObjectType_3(b, l + 1);
    exit_section_(b, m, CLOSE_OBJECT_TYPE, r);
    return r;
  }

  // ((KeyValuePairType)? (',' KeyValuePairType)*)?
  private static boolean CloseObjectType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_1")) return false;
    CloseObjectType_1_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)? (',' KeyValuePairType)*
  private static boolean CloseObjectType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = CloseObjectType_1_0_0(b, l + 1);
    r = r && CloseObjectType_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (KeyValuePairType)?
  private static boolean CloseObjectType_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_1_0_0")) return false;
    CloseObjectType_1_0_0_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)
  private static boolean CloseObjectType_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' KeyValuePairType)*
  private static boolean CloseObjectType_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!CloseObjectType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "CloseObjectType_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' KeyValuePairType
  private static boolean CloseObjectType_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CloseObjectType_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPEN_CLOSE_ORDERED_KEYWORD);
    r = r && CloseOrderedObjectType_1(b, l + 1);
    r = r && consumeToken(b, CLOSE_CLOSE_ORDERED_KEYWORD);
    r = r && CloseOrderedObjectType_3(b, l + 1);
    exit_section_(b, m, CLOSE_ORDERED_OBJECT_TYPE, r);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!CloseOrderedObjectType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "CloseOrderedObjectType_1_0_1", c)) break;
      c = current_position_(b);
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
  // '(' Expression ')' IF SimpleExpression
  static boolean ConditionalArrayElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalArrayElement")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, L_PARREN);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeTokens(b, 2, R_PARREN, IF);
    p = r; // pin = 4
    r = r && Expression(b, l + 1, 3);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '(' KeyExpression ':' Expression ')' IF Expression
  public static boolean ConditionalAttribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalAttribute")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONDITIONAL_ATTRIBUTE, null);
    r = consumeToken(b, L_PARREN);
    r = r && KeyExpression(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeTokens(b, 2, R_PARREN, IF);
    p = r; // pin = 6
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '(' Key ':' Expression ')' IF SimpleExpression
  public static boolean ConditionalKeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalKeyValuePair")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && Key(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeTokens(b, 0, R_PARREN, IF);
    r = r && Expression(b, l + 1, 3);
    exit_section_(b, m, CONDITIONAL_KEY_VALUE_PAIR, r);
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
  // ELSE '->' Expression
  public static boolean DefaultPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DefaultPattern")) return false;
    if (!nextTokenIs(b, ELSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DEFAULT_PATTERN, null);
    r = consumeTokens(b, 1, ELSE, ARROW_TOKEN);
    p = r; // pin = 1
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // VersionDirective
  //            | NamespaceDirective
  //            | VariableDirective
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
    if (!r) r = OutputDirective(b, l + 1);
    if (!r) r = InputDirective(b, l + 1);
    if (!r) r = TypeDirective(b, l + 1);
    if (!r) r = ImportDirective(b, l + 1);
    if (!r) r = FunctionDirective(b, l + 1);
    exit_section_(b, l, m, r, false, HeaderRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // VariableDirective
  //            | TypeDirective
  //            | ImportDirective
  //            | FunctionDirective
  static boolean DoDirectives(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoDirectives")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = VariableDirective(b, l + 1);
    if (!r) r = TypeDirective(b, l + 1);
    if (!r) r = ImportDirective(b, l + 1);
    if (!r) r = FunctionDirective(b, l + 1);
    exit_section_(b, l, m, r, false, HeaderRecover_parser_);
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
  // EnclosedExpression
  public static boolean DynamicKeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicKeyValuePair")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = EnclosedExpression(b, l + 1);
    exit_section_(b, m, DYNAMIC_KEY_VALUE_PAIR, r);
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
  // Identifier IF SimpleExpression '->' Expression
  public static boolean ExpressionPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionPattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_PATTERN, "<expression pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, IF);
    r = r && Expression(b, l + 1, 3);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CustomLoader? IdentifierPackage Identifier
  public static boolean FqnIdentifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FqnIdentifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FQN_IDENTIFIER, "<fqn identifier>");
    r = FqnIdentifier_0(b, l + 1);
    r = r && IdentifierPackage(b, l + 1);
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
  // Identifier TypeParameterDeclaration? L_PARREN ( FunctionParameter ( ',' FunctionParameter )* )? R_PARREN ( ":" Type "=" | "=")? Expression
  public static boolean FunctionDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_DEFINITION, "<function definition>");
    r = Identifier(b, l + 1);
    r = r && FunctionDefinition_1(b, l + 1);
    r = r && consumeToken(b, L_PARREN);
    p = r; // pin = 3
    r = r && report_error_(b, FunctionDefinition_3(b, l + 1));
    r = p && report_error_(b, consumeToken(b, R_PARREN)) && r;
    r = p && report_error_(b, FunctionDefinition_5(b, l + 1)) && r;
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
    int c = current_position_(b);
    while (true) {
      if (!FunctionDefinition_3_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionDefinition_3_0_1", c)) break;
      c = current_position_(b);
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

  // ( ":" Type "=" | "=")?
  private static boolean FunctionDefinition_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_5")) return false;
    FunctionDefinition_5_0(b, l + 1);
    return true;
  }

  // ":" Type "=" | "="
  private static boolean FunctionDefinition_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FunctionDefinition_5_0_0(b, l + 1);
    if (!r) r = consumeToken(b, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // ":" Type "="
  private static boolean FunctionDefinition_5_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDefinition_5_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'fun' FunctionDefinition
  public static boolean FunctionDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDirective")) return false;
    if (!nextTokenIs(b, FUNCTION_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_DIRECTIVE, null);
    r = consumeToken(b, FUNCTION_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && FunctionDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // VARIABLE_DECLARATION ('=' Expression)?
  public static boolean FunctionParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionParameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION_PARAMETER, "<function parameter>");
    r = VARIABLE_DECLARATION(b, l + 1);
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
  // Directive+
  public static boolean Header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Header")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEADER, "<header>");
    r = Directive(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!Directive(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Header", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // !('---'|OUTPUT_DIRECTIVE_KEYWORD|'type'|'fun'|'ns'|'var'|'%dw'|'input'|IMPORT_DIRECTIVE_KEYWORD)
  static boolean HeaderRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "HeaderRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !HeaderRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '---'|OUTPUT_DIRECTIVE_KEYWORD|'type'|'fun'|'ns'|'var'|'%dw'|'input'|IMPORT_DIRECTIVE_KEYWORD
  private static boolean HeaderRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "HeaderRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOCUMENT_SEPARATOR);
    if (!r) r = consumeToken(b, OUTPUT_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, TYPE_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, FUNCTION_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, NAMESPACE_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, VAR_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, VERSION_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, INPUT_DIRECTIVE_KEYWORD);
    if (!r) r = consumeToken(b, IMPORT_DIRECTIVE_KEYWORD);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DOLLAR_VARIABLE | ID | 'match' |'matches' | 'from'
  public static boolean Identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Identifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER, "<identifier>");
    r = consumeToken(b, DOLLAR_VARIABLE);
    if (!r) r = consumeToken(b, ID);
    if (!r) r = consumeToken(b, MATCH_KEYWORD);
    if (!r) r = consumeToken(b, MATCHES_KEYWORD);
    if (!r) r = consumeToken(b, FROM_KEYWORD);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (Identifier '::')*
  public static boolean IdentifierPackage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IdentifierPackage")) return false;
    Marker m = enter_section_(b, l, _NONE_, IDENTIFIER_PACKAGE, "<identifier package>");
    int c = current_position_(b);
    while (true) {
      if (!IdentifierPackage_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "IdentifierPackage", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // Identifier '::'
  private static boolean IdentifierPackage_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IdentifierPackage_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, PACKAGE_SEPARATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IMPORT_DIRECTIVE_KEYWORD (((ImportedElement (',' ImportedElement)*) | '*') 'from')? ModuleReference ('as' Identifier)?
  public static boolean ImportDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective")) return false;
    if (!nextTokenIs(b, IMPORT_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_DIRECTIVE, null);
    r = consumeToken(b, IMPORT_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, ImportDirective_1(b, l + 1));
    r = p && report_error_(b, ModuleReference(b, l + 1)) && r;
    r = p && ImportDirective_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (((ImportedElement (',' ImportedElement)*) | '*') 'from')?
  private static boolean ImportDirective_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_1")) return false;
    ImportDirective_1_0(b, l + 1);
    return true;
  }

  // ((ImportedElement (',' ImportedElement)*) | '*') 'from'
  private static boolean ImportDirective_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportDirective_1_0_0(b, l + 1);
    r = r && consumeToken(b, FROM_KEYWORD);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ImportedElement (',' ImportedElement)*) | '*'
  private static boolean ImportDirective_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportDirective_1_0_0_0(b, l + 1);
    if (!r) r = consumeToken(b, MULTIPLY);
    exit_section_(b, m, null, r);
    return r;
  }

  // ImportedElement (',' ImportedElement)*
  private static boolean ImportDirective_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportedElement(b, l + 1);
    r = r && ImportDirective_1_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' ImportedElement)*
  private static boolean ImportDirective_1_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_1_0_0_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ImportDirective_1_0_0_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ImportDirective_1_0_0_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' ImportedElement
  private static boolean ImportDirective_1_0_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_1_0_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ImportedElement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('as' Identifier)?
  private static boolean ImportDirective_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_3")) return false;
    ImportDirective_3_0(b, l + 1);
    return true;
  }

  // 'as' Identifier
  private static boolean ImportDirective_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDirective_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AS);
    r = r && Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Identifier ('as' Identifier)?
  public static boolean ImportedElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORTED_ELEMENT, "<imported element>");
    r = Identifier(b, l + 1);
    r = r && ImportedElement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('as' Identifier)?
  private static boolean ImportedElement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElement_1")) return false;
    ImportedElement_1_0(b, l + 1);
    return true;
  }

  // 'as' Identifier
  private static boolean ImportedElement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportedElement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AS);
    r = r && Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'input' VARIABLE_DECLARATION DataFormat Options?
  public static boolean InputDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDirective")) return false;
    if (!nextTokenIs(b, INPUT_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INPUT_DIRECTIVE, null);
    r = consumeToken(b, INPUT_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, VARIABLE_DECLARATION(b, l + 1));
    r = p && report_error_(b, DataFormat(b, l + 1)) && r;
    r = p && InputDirective_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // Options?
  private static boolean InputDirective_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InputDirective_3")) return false;
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
    int c = current_position_(b);
    while (true) {
      if (!IntersectionType_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "IntersectionType_1", c)) break;
      c = current_position_(b);
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
  // KeyExpression Attributes?
  public static boolean Key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY, "<key>");
    r = KeyExpression(b, l + 1);
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
  // DeclaredNamespace? (Identifier | StringLiteral| EnclosedExpression)
  public static boolean KeyExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, KEY_EXPRESSION, "<key expression>");
    r = KeyExpression_0(b, l + 1);
    r = r && KeyExpression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DeclaredNamespace?
  private static boolean KeyExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyExpression_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // Identifier | StringLiteral| EnclosedExpression
  private static boolean KeyExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    if (!r) r = EnclosedExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // ConditionalKeyValuePair | SimpleKeyValuePair
  public static boolean KeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "KeyValuePair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEY_VALUE_PAIR, "<key value pair>");
    r = ConditionalKeyValuePair(b, l + 1);
    if (!r) r = SimpleKeyValuePair(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // '(' (LambdaTypeParameter (',' LambdaTypeParameter)*)? ')' '->' Type
  public static boolean LambdaType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaType")) return false;
    if (!nextTokenIs(b, L_PARREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARREN);
    r = r && LambdaType_1(b, l + 1);
    r = r && consumeTokens(b, 0, R_PARREN, ARROW_TOKEN);
    r = r && Type(b, l + 1);
    exit_section_(b, m, LAMBDA_TYPE, r);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!LambdaType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LambdaType_1_0_1", c)) break;
      c = current_position_(b);
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
  // LiteralExpression '->' Expression
  public static boolean LiteralPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralPattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_PATTERN, "<literal pattern>");
    r = LiteralExpression(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CustomLoader? IdentifierPackage Identifier
  public static boolean ModuleReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ModuleReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODULE_REFERENCE, "<module reference>");
    r = ModuleReference_0(b, l + 1);
    r = r && IdentifierPackage(b, l + 1);
    r = r && Identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CustomLoader?
  private static boolean ModuleReference_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ModuleReference_0")) return false;
    CustomLoader(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '*'(DeclaredNamespace? (StringLiteral|Identifier))
  public static boolean MultiValueSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiValueSelector")) return false;
    if (!nextTokenIs(b, MULTIPLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MULTIPLY);
    r = r && MultiValueSelector_1(b, l + 1);
    exit_section_(b, m, MULTI_VALUE_SELECTOR, r);
    return r;
  }

  // DeclaredNamespace? (StringLiteral|Identifier)
  private static boolean MultiValueSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiValueSelector_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MultiValueSelector_1_0(b, l + 1);
    r = r && MultiValueSelector_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DeclaredNamespace?
  private static boolean MultiValueSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiValueSelector_1_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // StringLiteral|Identifier
  private static boolean MultiValueSelector_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiValueSelector_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' objectEntryRule? (',' objectEntryRule)* ','? '}'
  public static boolean MultipleKeyValuePairObj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultipleKeyValuePairObj")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MULTIPLE_KEY_VALUE_PAIR_OBJ, null);
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
    int c = current_position_(b);
    while (true) {
      if (!MultipleKeyValuePairObj_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MultipleKeyValuePairObj_2", c)) break;
      c = current_position_(b);
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

  // ','?
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
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
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
  // Identifier ':' Type
  static boolean NamedLambdaTypeParameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedLambdaTypeParameter")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 2
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Identifier ':' LiteralExpression '->' Expression
  public static boolean NamedLiteralPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedLiteralPattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMED_LITERAL_PATTERN, "<named literal pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && LiteralExpression(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Identifier 'matches' AnyRegexLiteral '->' Expression
  public static boolean NamedRegexPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedRegexPattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMED_REGEX_PATTERN, "<named regex pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, MATCHES_KEYWORD);
    r = r && AnyRegexLiteral(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Identifier "is" TypeLiteral '->' Expression
  public static boolean NamedTypePattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamedTypePattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMED_TYPE_PATTERN, "<named type pattern>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, IS);
    r = r && TypeLiteral(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ns' Identifier NAMESPACE_URI
  public static boolean NamespaceDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NamespaceDirective")) return false;
    if (!nextTokenIs(b, NAMESPACE_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_DIRECTIVE, null);
    r = consumeToken(b, NAMESPACE_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, Identifier(b, l + 1));
    r = p && consumeToken(b, NAMESPACE_URI) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // DOUBLE_LITERAL | INTEGER_LITERAL
  public static boolean NumberLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NumberLiteral")) return false;
    if (!nextTokenIs(b, "<number literal>", DOUBLE_LITERAL, INTEGER_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMBER_LITERAL, "<number literal>");
    r = consumeToken(b, DOUBLE_LITERAL);
    if (!r) r = consumeToken(b, INTEGER_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' Identifier ':' Identifier '~' Identifier '}' '->' Expression
  public static boolean ObjectDeconstructPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectDeconstructPattern")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_DECONSTRUCT_PATTERN, null);
    r = consumeToken(b, L_CURLY);
    r = r && Identifier(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Identifier(b, l + 1);
    r = r && consumeToken(b, TILDE);
    p = r; // pin = 5
    r = r && report_error_(b, Identifier(b, l + 1));
    r = p && report_error_(b, consumeTokens(b, -1, R_CURLY, ARROW_TOKEN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '&'(DeclaredNamespace? (StringLiteral|Identifier))?
  public static boolean ObjectSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector")) return false;
    if (!nextTokenIs(b, AND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    r = r && ObjectSelector_1(b, l + 1);
    exit_section_(b, m, OBJECT_SELECTOR, r);
    return r;
  }

  // (DeclaredNamespace? (StringLiteral|Identifier))?
  private static boolean ObjectSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector_1")) return false;
    ObjectSelector_1_0(b, l + 1);
    return true;
  }

  // DeclaredNamespace? (StringLiteral|Identifier)
  private static boolean ObjectSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ObjectSelector_1_0_0(b, l + 1);
    r = r && ObjectSelector_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DeclaredNamespace?
  private static boolean ObjectSelector_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector_1_0_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // StringLiteral|Identifier
  private static boolean ObjectSelector_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectSelector_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' ((KeyValuePairType)? (',' KeyValuePairType)*)?  '}' (Schema)?
  public static boolean ObjectType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY);
    r = r && ObjectType_1(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    r = r && ObjectType_3(b, l + 1);
    exit_section_(b, m, OBJECT_TYPE, r);
    return r;
  }

  // ((KeyValuePairType)? (',' KeyValuePairType)*)?
  private static boolean ObjectType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_1")) return false;
    ObjectType_1_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)? (',' KeyValuePairType)*
  private static boolean ObjectType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ObjectType_1_0_0(b, l + 1);
    r = r && ObjectType_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (KeyValuePairType)?
  private static boolean ObjectType_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_1_0_0")) return false;
    ObjectType_1_0_0_0(b, l + 1);
    return true;
  }

  // (KeyValuePairType)
  private static boolean ObjectType_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' KeyValuePairType)*
  private static boolean ObjectType_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ObjectType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ObjectType_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' KeyValuePairType
  private static boolean ObjectType_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectType_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && KeyValuePairType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPTION_ELEMENT, "<option element>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && LiteralExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!Options_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Options_1", c)) break;
      c = current_position_(b);
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
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPEN_ORDERED_KEYWORD);
    r = r && OrderedObjectType_1(b, l + 1);
    r = r && consumeToken(b, CLOSE_ORDERED_KEYWORD);
    r = r && OrderedObjectType_3(b, l + 1);
    exit_section_(b, m, ORDERED_OBJECT_TYPE, r);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!OrderedObjectType_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "OrderedObjectType_1_0_1", c)) break;
      c = current_position_(b);
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
  // OUTPUT_DIRECTIVE_KEYWORD (":" Type)? DataFormat Options?
  public static boolean OutputDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective")) return false;
    if (!nextTokenIs(b, OUTPUT_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OUTPUT_DIRECTIVE, null);
    r = consumeToken(b, OUTPUT_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, OutputDirective_1(b, l + 1));
    r = p && report_error_(b, DataFormat(b, l + 1)) && r;
    r = p && OutputDirective_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (":" Type)?
  private static boolean OutputDirective_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_1")) return false;
    OutputDirective_1_0(b, l + 1);
    return true;
  }

  // ":" Type
  private static boolean OutputDirective_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Options?
  private static boolean OutputDirective_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OutputDirective_3")) return false;
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
    int c = current_position_(b);
    while (r) {
      if (!PatternMatcherExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "PatternMatcherExpression_1", c)) break;
      c = current_position_(b);
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
  // FqnIdentifier ('<' Type (',' Type)* '>')? (Schema)?
  public static boolean ReferenceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, REFERENCE_TYPE, "<reference type>");
    r = FqnIdentifier(b, l + 1);
    r = r && ReferenceType_1(b, l + 1);
    r = r && ReferenceType_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!ReferenceType_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ReferenceType_1_0_2", c)) break;
      c = current_position_(b);
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

  // (Schema)?
  private static boolean ReferenceType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_2")) return false;
    ReferenceType_2_0(b, l + 1);
    return true;
  }

  // (Schema)
  private static boolean ReferenceType_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceType_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Schema(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'matches' AnyRegexLiteral '->' Expression
  public static boolean RegexPattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RegexPattern")) return false;
    if (!nextTokenIs(b, MATCHES_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MATCHES_KEYWORD);
    r = r && AnyRegexLiteral(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, REGEX_PATTERN, r);
    return r;
  }

  /* ********************************************************** */
  // '{' ( SchemaElement ( ',' SchemaElement )* )? '}'
  public static boolean Schema(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Schema")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY);
    r = r && Schema_1(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, m, SCHEMA, r);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!Schema_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Schema_1_0_1", c)) break;
      c = current_position_(b);
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

  /* ********************************************************** */
  // (Identifier | StringLiteral) ':' LiteralExpression
  public static boolean SchemaElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCHEMA_ELEMENT, "<schema element>");
    r = SchemaElement_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && LiteralExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // Identifier | StringLiteral
  private static boolean SchemaElement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaElement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '^'(DeclaredNamespace? (StringLiteral|Identifier))?
  public static boolean SchemaSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector")) return false;
    if (!nextTokenIs(b, XOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, XOR);
    r = r && SchemaSelector_1(b, l + 1);
    exit_section_(b, m, SCHEMA_SELECTOR, r);
    return r;
  }

  // (DeclaredNamespace? (StringLiteral|Identifier))?
  private static boolean SchemaSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector_1")) return false;
    SchemaSelector_1_0(b, l + 1);
    return true;
  }

  // DeclaredNamespace? (StringLiteral|Identifier)
  private static boolean SchemaSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SchemaSelector_1_0_0(b, l + 1);
    r = r && SchemaSelector_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DeclaredNamespace?
  private static boolean SchemaSelector_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector_1_0_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // StringLiteral|Identifier
  private static boolean SchemaSelector_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SchemaSelector_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ValueSelector |
  //          AttributeSelector |
  //          NamespaceSelector |
  //          SchemaSelector |
  //          ObjectSelector |
  //          MultiValueSelector
  public static boolean Selector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Selector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SELECTOR, "<selector>");
    r = ValueSelector(b, l + 1);
    if (!r) r = AttributeSelector(b, l + 1);
    if (!r) r = NamespaceSelector(b, l + 1);
    if (!r) r = SchemaSelector(b, l + 1);
    if (!r) r = ObjectSelector(b, l + 1);
    if (!r) r = MultiValueSelector(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Key ':' Expression
  public static boolean SimpleKeyValuePair(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleKeyValuePair")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_KEY_VALUE_PAIR, "<simple key value pair>");
    r = Key(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KeyValuePair
  public static boolean SingleKeyValuePairObj(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SingleKeyValuePairObj")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SINGLE_KEY_VALUE_PAIR_OBJ, "<single key value pair obj>");
    r = KeyValuePair(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // 'type' Identifier TypeParameterDeclaration? '=' (TypeLiteral | UndefinedLiteral)
  public static boolean TypeDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDirective")) return false;
    if (!nextTokenIs(b, TYPE_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPE_DIRECTIVE, null);
    r = consumeToken(b, TYPE_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, Identifier(b, l + 1));
    r = p && report_error_(b, TypeDirective_2(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, EQ)) && r;
    r = p && TypeDirective_4(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // TypeParameterDeclaration?
  private static boolean TypeDirective_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDirective_2")) return false;
    TypeParameterDeclaration(b, l + 1);
    return true;
  }

  // TypeLiteral | UndefinedLiteral
  private static boolean TypeDirective_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDirective_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeLiteral(b, l + 1);
    if (!r) r = UndefinedLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!TypeParameterDeclaration_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeParameterDeclaration_1_0_1", c)) break;
      c = current_position_(b);
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
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, TYPE_PATTERN, r);
    return r;
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
    int c = current_position_(b);
    while (true) {
      if (!UnionTypeExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "UnionType_1", c)) break;
      c = current_position_(b);
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
  // Identifier (":" Type)?
  static boolean VARIABLE_DECLARATION(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VARIABLE_DECLARATION")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Identifier(b, l + 1);
    r = r && VARIABLE_DECLARATION_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (":" Type)?
  private static boolean VARIABLE_DECLARATION_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VARIABLE_DECLARATION_1")) return false;
    VARIABLE_DECLARATION_1_0(b, l + 1);
    return true;
  }

  // ":" Type
  private static boolean VARIABLE_DECLARATION_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VARIABLE_DECLARATION_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DeclaredNamespace? (StringLiteral|Identifier)
  public static boolean ValueSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueSelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_SELECTOR, "<value selector>");
    r = ValueSelector_0(b, l + 1);
    r = r && ValueSelector_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DeclaredNamespace?
  private static boolean ValueSelector_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueSelector_0")) return false;
    DeclaredNamespace(b, l + 1);
    return true;
  }

  // StringLiteral|Identifier
  private static boolean ValueSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ValueSelector_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = StringLiteral(b, l + 1);
    if (!r) r = Identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // VARIABLE_DECLARATION '='  Expression
  public static boolean VariableDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DEFINITION, "<Variable>");
    r = VARIABLE_DECLARATION(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'var' VariableDefinition
  public static boolean VariableDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VariableDirective")) return false;
    if (!nextTokenIs(b, VAR_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DIRECTIVE, null);
    r = consumeToken(b, VAR_DIRECTIVE_KEYWORD);
    p = r; // pin = 1
    r = r && VariableDefinition(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '%dw'  DOUBLE_LITERAL
  public static boolean VersionDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VersionDirective")) return false;
    if (!nextTokenIs(b, VERSION_DIRECTIVE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VERSION_DIRECTIVE, null);
    r = consumeTokens(b, 1, VERSION_DIRECTIVE_KEYWORD, DOUBLE_LITERAL);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KeyValuePair | DynamicKeyValuePair
  static boolean objectEntryRule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectEntryRule")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = KeyValuePair(b, l + 1);
    if (!r) r = DynamicKeyValuePair(b, l + 1);
    exit_section_(b, m, null, r);
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
  // 1: ATOM(CustomInterpolatorExpression)
  // 2: BINARY(DefaultValueExpression)
  // 3: BINARY(BinaryExpression)
  // 4: BINARY(OrExpression) BINARY(AndExpression) BINARY(EqualityExpression) BINARY(GreaterThanExpression)
  //    BINARY(AdditionSubtractionExpression) BINARY(RightShiftExpression) BINARY(LeftShiftExpression) BINARY(MultiplicationDivisionExpression)
  //    POSTFIX(AsExpression) POSTFIX(IsExpression) ATOM(UndefinedLiteral) ATOM(UnaryMinusExpression)
  //    PREFIX(NotExpression) ATOM(ConditionalExpression) ATOM(UsingExpression) PREFIX(DoExpression)
  //    ATOM(LambdaLiteral) ATOM(ObjectDeconstructExpression) ATOM(ObjectExpression) ATOM(ArrayExpression)
  //    ATOM(VariableReferenceExpression) ATOM(LiteralExpression) PREFIX(EnclosedExpression) POSTFIX(FunctionCallExpression)
  //    POSTFIX(DotSelectorExpression) POSTFIX(BracketSelectorExpression)
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
      else if (g < 2 && consumeTokenSmart(b, DEFAULT)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, DEFAULT_VALUE_EXPRESSION, r, true, null);
      }
      else if (g < 3 && Identifier(b, l + 1)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, BINARY_EXPRESSION, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, OR_KEYWORD)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, OR_EXPRESSION, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, AND_KEYWORD)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, AND_EXPRESSION, r, true, null);
      }
      else if (g < 4 && EqualityExpression_0(b, l + 1)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, EQUALITY_EXPRESSION, r, true, null);
      }
      else if (g < 4 && GreaterThanExpression_0(b, l + 1)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, GREATER_THAN_EXPRESSION, r, true, null);
      }
      else if (g < 4 && AdditionSubtractionExpression_0(b, l + 1)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, ADDITION_SUBTRACTION_EXPRESSION, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, RIGHT_SHIFT)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, RIGHT_SHIFT_EXPRESSION, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, LEFT_SHIFT)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, LEFT_SHIFT_EXPRESSION, r, true, null);
      }
      else if (g < 4 && MultiplicationDivisionExpression_0(b, l + 1)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, MULTIPLICATION_DIVISION_EXPRESSION, r, true, null);
      }
      else if (g < 4 && AsExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, AS_EXPRESSION, r, true, null);
      }
      else if (g < 4 && IsExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, IS_EXPRESSION, r, true, null);
      }
      else if (g < 4 && FunctionCallExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, FUNCTION_CALL_EXPRESSION, r, true, null);
      }
      else if (g < 4 && DotSelectorExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, DOT_SELECTOR_EXPRESSION, r, true, null);
      }
      else if (g < 4 && BracketSelectorExpression_0(b, l + 1)) {
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

  // Identifier BACKTIKED_QUOTED_STRING
  public static boolean CustomInterpolatorExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CustomInterpolatorExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_INTERPOLATOR_EXPRESSION, "<custom interpolator expression>");
    r = Identifier(b, l + 1);
    r = r && consumeToken(b, BACKTIKED_QUOTED_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '==' | '!=' | '~='
  private static boolean EqualityExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "EqualityExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, EQUAL);
    if (!r) r = consumeTokenSmart(b, NOT_EQUAL);
    if (!r) r = consumeTokenSmart(b, SIMILAR);
    exit_section_(b, m, null, r);
    return r;
  }

  // '>' | '>=' | '<' | '<='
  private static boolean GreaterThanExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GreaterThanExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, GREATER);
    if (!r) r = consumeTokenSmart(b, GREATER_EQUAL);
    if (!r) r = consumeTokenSmart(b, LESS);
    if (!r) r = consumeTokenSmart(b, LESS_EQUAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+' | '-'
  private static boolean AdditionSubtractionExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AdditionSubtractionExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, PLUS);
    if (!r) r = consumeTokenSmart(b, MINUS);
    exit_section_(b, m, null, r);
    return r;
  }

  // '*' | '/'
  private static boolean MultiplicationDivisionExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MultiplicationDivisionExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, MULTIPLY);
    if (!r) r = consumeTokenSmart(b, DIVISION);
    exit_section_(b, m, null, r);
    return r;
  }

  // AS TypeLiteral
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
    r = r && Expression(b, l + 1, 3);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  public static boolean NotExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NotExpression")) return false;
    if (!nextTokenIsSmart(b, NOT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, NOT_KEYWORD);
    p = r;
    r = p && Expression(b, l, 3);
    exit_section_(b, l, m, NOT_EXPRESSION, r, p, null);
    return r || p;
  }

  // ( IF | UNLESS ) EnclosedExpression Expression ELSE Expression
  public static boolean ConditionalExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConditionalExpression")) return false;
    if (!nextTokenIsSmart(b, IF, UNLESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _COLLAPSE_, CONDITIONAL_EXPRESSION, "<conditional expression>");
    r = ConditionalExpression_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, EnclosedExpression(b, l + 1));
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
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, IF);
    if (!r) r = consumeTokenSmart(b, UNLESS);
    exit_section_(b, m, null, r);
    return r;
  }

  // USING '(' VariableDefinition ( ',' VariableDefinition )* ')' Expression
  public static boolean UsingExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingExpression")) return false;
    if (!nextTokenIsSmart(b, USING)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, USING_EXPRESSION, null);
    r = consumeTokensSmart(b, 1, USING, L_PARREN);
    p = r; // pin = 1
    r = r && report_error_(b, VariableDefinition(b, l + 1));
    r = p && report_error_(b, UsingExpression_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, R_PARREN)) && r;
    r = p && Expression(b, l + 1, -1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ( ',' VariableDefinition )*
  private static boolean UsingExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UsingExpression_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!UsingExpression_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "UsingExpression_3", c)) break;
      c = current_position_(b);
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

  public static boolean DoExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression")) return false;
    if (!nextTokenIsSmart(b, DO_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = DoExpression_0(b, l + 1);
    p = r;
    r = p && Expression(b, l, -1);
    r = p && report_error_(b, consumeToken(b, R_CURLY)) && r;
    exit_section_(b, l, m, DO_EXPRESSION, r, p, null);
    return r || p;
  }

  // 'do' '{' (DoDirectives '---')?
  private static boolean DoExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, DO_KEYWORD, L_CURLY);
    r = r && DoExpression_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DoDirectives '---')?
  private static boolean DoExpression_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression_0_2")) return false;
    DoExpression_0_2_0(b, l + 1);
    return true;
  }

  // DoDirectives '---'
  private static boolean DoExpression_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DoExpression_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = DoDirectives(b, l + 1);
    r = r && consumeToken(b, DOCUMENT_SEPARATOR);
    exit_section_(b, m, null, r);
    return r;
  }

  // TypeParameterDeclaration? '(' ( FunctionParameter ( ',' FunctionParameter )* )? ')' (':' Type)?  '->' SimpleExpression
  public static boolean LambdaLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral")) return false;
    if (!nextTokenIsSmart(b, L_PARREN, LESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LAMBDA_LITERAL, "<lambda literal>");
    r = LambdaLiteral_0(b, l + 1);
    r = r && consumeToken(b, L_PARREN);
    r = r && LambdaLiteral_2(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    r = r && LambdaLiteral_4(b, l + 1);
    r = r && consumeToken(b, ARROW_TOKEN);
    p = r; // pin = 6
    r = r && Expression(b, l + 1, 3);
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
    int c = current_position_(b);
    while (true) {
      if (!LambdaLiteral_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LambdaLiteral_2_0_1", c)) break;
      c = current_position_(b);
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

  // (':' Type)?
  private static boolean LambdaLiteral_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_4")) return false;
    LambdaLiteral_4_0(b, l + 1);
    return true;
  }

  // ':' Type
  private static boolean LambdaLiteral_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LambdaLiteral_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COLON);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '{' KeyValuePair '~' Expression '}'
  public static boolean ObjectDeconstructExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ObjectDeconstructExpression")) return false;
    if (!nextTokenIsSmart(b, L_CURLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_DECONSTRUCT_EXPRESSION, null);
    r = consumeTokenSmart(b, L_CURLY);
    r = r && KeyValuePair(b, l + 1);
    r = r && consumeToken(b, TILDE);
    p = r; // pin = 3
    r = r && report_error_(b, Expression(b, l + 1, -1));
    r = p && consumeToken(b, R_CURLY) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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

  // '[' ']' | '[' ( ArrayElement (('~' Expression)  | ( ',' ArrayElement )* (',')? )?) ']'
  public static boolean ArrayExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression")) return false;
    if (!nextTokenIsSmart(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokensSmart(b, 0, L_BRACKET, R_BRACKET);
    if (!r) r = ArrayExpression_1(b, l + 1);
    exit_section_(b, m, ARRAY_EXPRESSION, r);
    return r;
  }

  // '[' ( ArrayElement (('~' Expression)  | ( ',' ArrayElement )* (',')? )?) ']'
  private static boolean ArrayExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, L_BRACKET);
    r = r && ArrayExpression_1_1(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // ArrayElement (('~' Expression)  | ( ',' ArrayElement )* (',')? )?
  private static boolean ArrayExpression_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayElement(b, l + 1);
    r = r && ArrayExpression_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (('~' Expression)  | ( ',' ArrayElement )* (',')? )?
  private static boolean ArrayExpression_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1")) return false;
    ArrayExpression_1_1_1_0(b, l + 1);
    return true;
  }

  // ('~' Expression)  | ( ',' ArrayElement )* (',')?
  private static boolean ArrayExpression_1_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayExpression_1_1_1_0_0(b, l + 1);
    if (!r) r = ArrayExpression_1_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '~' Expression
  private static boolean ArrayExpression_1_1_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, TILDE);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' ArrayElement )* (',')?
  private static boolean ArrayExpression_1_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayExpression_1_1_1_0_1_0(b, l + 1);
    r = r && ArrayExpression_1_1_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' ArrayElement )*
  private static boolean ArrayExpression_1_1_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1_0_1_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ArrayExpression_1_1_1_0_1_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ArrayExpression_1_1_1_0_1_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' ArrayElement
  private static boolean ArrayExpression_1_1_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COMMA);
    r = r && ArrayElement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',')?
  private static boolean ArrayExpression_1_1_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayExpression_1_1_1_0_1_1")) return false;
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
  //            | AnyRegexLiteral
  public static boolean LiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, LITERAL_EXPRESSION, "<literal expression>");
    r = BooleanLiteral(b, l + 1);
    if (!r) r = NullLiteral(b, l + 1);
    if (!r) r = StringLiteral(b, l + 1);
    if (!r) r = NumberLiteral(b, l + 1);
    if (!r) r = AnyDateLiteral(b, l + 1);
    if (!r) r = AnyRegexLiteral(b, l + 1);
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
    r = p && report_error_(b, consumeToken(b, R_PARREN)) && r;
    exit_section_(b, l, m, ENCLOSED_EXPRESSION, r, p, null);
    return r || p;
  }

  // '(' ( Expression ( ',' Expression )* )? ')'
  private static boolean FunctionCallExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, L_PARREN);
    r = r && FunctionCallExpression_0_1(b, l + 1);
    r = r && consumeToken(b, R_PARREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( Expression ( ',' Expression )* )?
  private static boolean FunctionCallExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0_1")) return false;
    FunctionCallExpression_0_1_0(b, l + 1);
    return true;
  }

  // Expression ( ',' Expression )*
  private static boolean FunctionCallExpression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, -1);
    r = r && FunctionCallExpression_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( ',' Expression )*
  private static boolean FunctionCallExpression_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!FunctionCallExpression_0_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionCallExpression_0_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' Expression
  private static boolean FunctionCallExpression_0_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionCallExpression_0_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, COMMA);
    r = r && Expression(b, l + 1, -1);
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
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, "..");
    if (!r) r = consumeTokenSmart(b, ".");
    exit_section_(b, m, null, r);
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
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, ESCLAMATION);
    if (!r) r = consumeTokenSmart(b, QUESTION);
    exit_section_(b, m, null, r);
    return r;
  }

  // '[' ('?' |'@' | '&' | '^' |'*')? (BinaryExpression | SimpleExpression ) ']' ('!'| '?')?
  private static boolean BracketSelectorExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, L_BRACKET);
    r = r && BracketSelectorExpression_0_1(b, l + 1);
    r = r && BracketSelectorExpression_0_2(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    r = r && BracketSelectorExpression_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('?' |'@' | '&' | '^' |'*')?
  private static boolean BracketSelectorExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1")) return false;
    BracketSelectorExpression_0_1_0(b, l + 1);
    return true;
  }

  // '?' |'@' | '&' | '^' |'*'
  private static boolean BracketSelectorExpression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, QUESTION);
    if (!r) r = consumeTokenSmart(b, AT);
    if (!r) r = consumeTokenSmart(b, AND);
    if (!r) r = consumeTokenSmart(b, XOR);
    if (!r) r = consumeTokenSmart(b, MULTIPLY);
    exit_section_(b, m, null, r);
    return r;
  }

  // BinaryExpression | SimpleExpression
  private static boolean BracketSelectorExpression_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, 2);
    if (!r) r = Expression(b, l + 1, 3);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('!'| '?')?
  private static boolean BracketSelectorExpression_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_4")) return false;
    BracketSelectorExpression_0_4_0(b, l + 1);
    return true;
  }

  // '!'| '?'
  private static boolean BracketSelectorExpression_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BracketSelectorExpression_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, ESCLAMATION);
    if (!r) r = consumeTokenSmart(b, QUESTION);
    exit_section_(b, m, null, r);
    return r;
  }

  final static Parser HeaderRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return HeaderRecover(b, l + 1);
    }
  };
}
