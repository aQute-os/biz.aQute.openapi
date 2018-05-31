package aQute.openapi.example.petstore.user;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import aQute.bnd.annotation.headers.ProvideCapability;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ProvideCapability(ns = "aQute.openapi", effective = "active", name = "aQute.openapi.example.petstore.user.GeneratedUser", version = "1.0.1")
public @interface ProvideGeneratedUser {}
