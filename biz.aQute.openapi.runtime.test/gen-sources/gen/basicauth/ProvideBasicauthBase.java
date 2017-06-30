package gen.basicauth;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import aQute.bnd.annotation.headers.ProvideCapability;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ProvideCapability(ns="aQute.openapi", effective="active", name="gen.basicauth.BasicauthBase", version="1.0.1")
public @interface ProvideBasicauthBase {
}
