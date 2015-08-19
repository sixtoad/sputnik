package pl.touk.sputnik.review.filter;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class CpdFilter extends FileExtensionFilter {
    private static final List<String> CPD_SUPPORTED_EXTENSIONS = ImmutableList.of(
            "java",
            "jsp", "jspx",
            "js",
            "xml",
            "xsl",
            "h", "hpp", "hxx", "c", "cpp", "cxx", "cc", "C",
            "cs",
            "php", "phtml", "php3", "php4", "php5", "phps",//php
            "rb", "cgi", "class", //ruby
            "go",
            "for", "f", "f66", "f77", "f90",
            "sql", //PL/SQL
		    "trg", //Triggers
		    "prc",".fnc", // Standalone Procedures and Functions 
		    "pld", // Oracle*Forms 
		    "pls" ,"plh" ,"plb", // Packages
		    "pck" ,"pks" ,"pkh" ,"pkb", // Packages
		    "typ" ,"tyb", // Object Types
		    "tps" ,"tpb" // Object Types
            );


    public CpdFilter() {
        super(CPD_SUPPORTED_EXTENSIONS);
    }
}
