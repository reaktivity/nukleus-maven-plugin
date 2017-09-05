# Nukleus Maven Plugin

[![Build Status][build-status-image]][build-status]

[build-status-image]: https://travis-ci.org/reaktivity/nukleus-maven-plugin.svg?branch=develop
[build-status]: https://travis-ci.org/reaktivity/nukleus-maven-plugin

Default values

- default values are allowed on int and uint types only, example: uint8 field1 = 10;
- fields of type octets with no specified size or list must appear last in their structure
  and are implicitly defaulted to empty
- builder field mutator methods must be called in the order the fields appear in the IDL
  structure, and must be called on all required fields (those with no explicit or implicit default)
