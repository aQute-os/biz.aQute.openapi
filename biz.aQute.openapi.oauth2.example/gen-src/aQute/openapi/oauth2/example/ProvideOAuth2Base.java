package aQute.openapi.oauth2.example;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import aQute.bnd.annotation.headers.ProvideCapability;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ProvideCapability(ns="aQute.openapi", effective="active", name="aQute.openapi.oauth2.example.OAuth2Base", version="1.0.1")
public @interface ProvideOAuth2Base {
}
