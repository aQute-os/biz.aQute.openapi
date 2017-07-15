package gen.enums;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * <li>{@link #enum$(AllEnumVariations) POST /enum =  enum}
 * 
 * </ul>
 * 
 */

@RequireEnumsBase
public abstract class EnumsBase extends OpenAPIBase {

public static final String BASE_PATH = "/enums";

/**
 * 
 * POST /enum = enum
 * 
 * @param body – Pet object that needs to be added to the store (body)
 * 
 */

protected abstract void enum$(AllEnumVariations body) throws Exception;

/**
 * 
 * SimpleEnum
 * 
 */

  public enum SimpleEnum {
    a("a"),
    b("b"),
    c("c");

    public final String value;

    SimpleEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * ArrayEnum
 * 
 */

  public enum ArrayEnum {
    e("e"),
    f("f"),
    g("g");

    public final String value;

    ArrayEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * AllEnumVariations
 * 
 */

public static class AllEnumVariations extends OpenAPIBase.DTO {

    public List<ArrayEnum> array;
    public List<MemberNamesEnum> memberNames;
    public SimpleEnum simple;
    public Optional<OptionalSimpleEnum> optionalSimple = Optional.empty();
    public Optional<IfEnum> if$ = Optional.empty();
    public Optional<List<OptionalArrayEnum>> optionalArray = Optional.empty();

    public AllEnumVariations array(List<ArrayEnum> array){ this.array=array; return this; }
    public List<ArrayEnum> array(){ return this.array; }

    public AllEnumVariations memberNames(List<MemberNamesEnum> memberNames){ this.memberNames=memberNames; return this; }
    public List<MemberNamesEnum> memberNames(){ return this.memberNames; }

    public AllEnumVariations simple(SimpleEnum simple){ this.simple=simple; return this; }
    public SimpleEnum simple(){ return this.simple; }

    public AllEnumVariations optionalSimple(OptionalSimpleEnum optionalSimple){ this.optionalSimple=Optional.ofNullable(optionalSimple); return this; }
    public Optional<OptionalSimpleEnum> optionalSimple(){ return this.optionalSimple; }

    public AllEnumVariations if$(IfEnum if$){ this.if$=Optional.ofNullable(if$); return this; }
    public Optional<IfEnum> if$(){ return this.if$; }

    public AllEnumVariations optionalArray(List<OptionalArrayEnum> optionalArray){ this.optionalArray=Optional.ofNullable(optionalArray); return this; }
    public Optional<List<OptionalArrayEnum>> optionalArray(){ return this.optionalArray; }

}

/**
 * 
 * IfEnum
 * 
 */

  public enum IfEnum {
    n("n"),
    o("o"),
    p("p");

    public final String value;

    IfEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * MemberNamesEnum
 * 
 */

  public enum MemberNamesEnum {
    new$("new"),
    n$5Ec("n^c"),
    m("m"),
    M("M");

    public final String value;

    MemberNamesEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * OptionalSimpleEnum
 * 
 */

  public enum OptionalSimpleEnum {
    h("h"),
    i("i"),
    j("j");

    public final String value;

    OptionalSimpleEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * OptionalArrayEnum
 * 
 */

  public enum OptionalArrayEnum {
    k("k"),
    l("l"),
    m("m");

    public final String value;

    OptionalArrayEnum(String value) {
      this.value = value;
    }
  }

  /*****************************************************************/

  public EnumsBase() {
    super(BASE_PATH,gen.enums.EnumsBase.class,
         "enum                 POST   /enum  PAYLOAD AllEnumVariations");
  }
  public static java.time.Instant toDateTime(String s) {
    return java.time.Instant.parse(s);
  }
  public static String fromDateTime(java.time.Instant s) {
    return s.toString();
  }
  public static LocalDate toDate(String s) {
    return LocalDate.parse(s);
  }
  public static String fromDate(LocalDate s) {
    return s.toString();
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "enum".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          enum$_post_(context);
          return true;
        } 
      }

      // end enum
    } 

    return false;
  }

private void enum$_post_(OpenAPIContext context) throws Exception{

    context.setOperation("enum");
AllEnumVariations body_ = context.body(AllEnumVariations.class);


    //  VALIDATORS 

    context.begin("enum");
    context.end();

    context.call( () -> { enum$(body_); return null; });
    context.setResult(null, 200);

}

}


// aQute OpenAPI generator version 0
