# Nukleus Maven Plugin

[![Build Status][build-status-image]][build-status]

[build-status-image]: https://travis-ci.org/reaktivity/nukleus-maven-plugin.svg?branch=develop
[build-status]: https://travis-ci.org/reaktivity/nukleus-maven-plugin

###Default values

- default values are allowed on int and uint types only, example: uint8 field1 = 10;
- the following types of fields are implicitly defaulted:
  - fields of type list (must appear last in their structure, default to empty)
  - fields of type octets with no specified size (must appear last in their structure, default to empty)
  - fields used to hold the size of a subsequent octets field (default to zero, automatically set to the correct value when the corresponding octets field is set)
  
###Rules for using the generated flyweight Builders
 
- builder field mutator methods must be called in the order the fields appear in the IDL, and must be called on all required fields (those with no explicit or implicit default)
