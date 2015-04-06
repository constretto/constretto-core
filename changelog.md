## What's new in 2.2.2
* Support for YAML store contributed by [hamnis](//github.com/hamnis) - pull request #48
* Change scope of Constretto-test in the Constretto Spring module to "test" - pull request #47

## What's new in 2.2.1
* Support for specifying default values for Spring property placeholders (i.e `${missingValue:default}`). Contributed by [PavlikPolivka](https://github.com/PavlikPolivka) - pull request #46
* Updated Spring dependencies to v. 3.2.11

## What's new in 2.2.0 
* Improved support for Spring Java Config (issue #41) [example](constretto-spring/src/test/java/org/constretto/spring/ConstrettoJavaConfigTest.java)
* Added support for a simple mapping api method as contributed by [kenglxn](https://github.com/kenglxn) in pull request [#41](https://github.com/constretto/constretto-core/pull/43) 
* Upgraded Spring dependency to 3.2.9.RELEASE (4.x will be supported in the next major version)
* Removed direct dependency on commons-logging (the constretto-spring module still has a transitive dependency on it in through its dependency on Spring-Core)
* Upgraded AspectJ to 1.7.4
* Upgraded Commons Beanutils to 1.9.1
* No longer depends on commons-logging. Depends on slf4j-api instead, making it easier to configure your own logging backend


## What's new in 2.1.4
* Change StaticlyCachedConfiguration by adding the SystemPropertiesStore (contributed by @kolstae SHA: 0f0d6eda3a010ca5f9f16afbbd4e2d892ef9d117 )

## What's new in 2.1.3
* Fixed issue reported by @jhberges bad handling of leading characters in values by upgrading GSon.
  Be aware if you rely on stripping leading chars in property values as it will no longer work.
* Upgraded jasypt dependency to version 1.9.1

## What's new in 2.1.2
* Improved handling of generic fields and method parameter injections (thanks again to @ahaarrestad)

## What’s new in 2.1.1
* Bugfix contributed by @ahaarrestad. Resolving properties to Map is finally working :-)

## What’s new in 2.1
* Improved support for Junit 4.X after refactoring the [JUnit Rule](https://github.com/junit-team/junit/wiki/Rules) [ConstrettoRule](constretto-test/src/main/java/org/constretto/test/ConstrettoRule.java) added in 2.0.4 to be used as a @ClassRule.
  As a consequence the constretto-test-junit4 module has been merged into the constretto-test module.
  Look at the [example](#using-the-constrettorule-in-a-junit-test) for details
* LDAP configuration support. You can add configuration either by using DSN or by providing a LDAP search. [Example](#using-the-ldapconfigurationstore)
    * NOTE: Constretto will not close or even handle LDAP connection issues for you. The will make it easier to integrate with Spring LDAP or other third-party libraries.
    * The development of this feature has been sponsored by <a href="http://www.nextgentel.no">NextGenTel AS</a>
* Dropped support for Spring 2.X in favour of the latest Spring 3.2 release.
* The @Configure annotation can now be used on [constructors](#constructor-injection) (w/o Spring)
* The reconfigure() call on the [ConstrettoConfiguration](constretto-api/src/main/java/org/constretto/ConstrettoConfiguration.java) interface has been deprecated as it is not thread-safe.
    * It will be completely removed in the next release 
* As always: special thanks goes to those who made contributions to this release
