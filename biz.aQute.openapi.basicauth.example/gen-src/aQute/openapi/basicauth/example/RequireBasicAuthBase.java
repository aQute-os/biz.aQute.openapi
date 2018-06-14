package aQute.openapi.basicauth.example;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import aQute.bnd.annotation.headers.RequireCapability;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RequireCapability(ns="aQute.openapi", effective="active", filter="(&(aQute.openapi=aQute.openapi.basicauth.example.BasicAuthBase)(&(version>=1.0)(!(version>=2.0))))")
public @interface RequireBasicAuthBase {
}
