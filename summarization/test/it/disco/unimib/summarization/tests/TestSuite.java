package it.disco.unimib.summarization.tests;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.IncludeJars;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)
@IncludeJars(true)
@ClassnameFilters({"it.disco.unimib.summarization.*Test"})
public class TestSuite {}
