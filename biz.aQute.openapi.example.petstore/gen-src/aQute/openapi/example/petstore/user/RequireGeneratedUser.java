package aQute.openapi.example.petstore.user;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import aQute.bnd.annotation.headers.RequireCapability;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RequireCapability(ns = "aQute.openapi", effective = "active", filter = "(&(aQute.openapi=aQute.openapi.example.petstore.user.GeneratedUser)(&(version>=1.0)(!(version>=2.0))))")
public @interface RequireGeneratedUser {}
