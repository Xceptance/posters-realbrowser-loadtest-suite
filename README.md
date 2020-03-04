# Real Browser Performance Test Suite for Posters

This repository is an example of a real browser test suite for load testing a website with [XLT](https://www.xceptance.com/xlt/). In comparison to our [advanced posters loadtest suite](https://github.com/Xceptance/posters-advanced-loadtest-suite), this test suite shows of a real browser testing approach with Selenide and XLT. It shows what is possible thanks to Java as scripting language on top of the XLT base feature set. The real browser posters test suite demostrates the following features and functionalities:

**Base XLT Features**
* Every test case is a JUnit test
* Use Eclipse or any other IDE to compile, run (as single test user), and debug
* The tests can run as a normal integration test or as part of a classic build process
* XLT measures, scales, and paces the testing
* Handling of test scaling, results collection and report building by XLT
* Fits your CI/CD pipeline, including comparison against previous runs
* Test design adhering to page object model
* Comfortable Maven setup

**Enhanced and New Features**
* Configuration via YAML files supporting a region, locale, and site arrangement
* Centralized configuration and mapping of objects to support data types
* Central context for test execution data enabling easier programming
* Component based modelling of the site under test
* Replayable randomness among test scenario executions

The test suite is ready to be executed against the Posters demo store [Posters](https://35.184.136.113:8443/posters/). Please note, this setup is for testing purposes only and does not handle a lot of load. Please setup your [own copy of the Posters demo store](https://github.com/Xceptance/posters-demo-store) if you want to experiment with executions of higher load factors and more complex test configurations.

## XLT Documentation

[XLT](https://www.xceptance.com/xlt) is a load testing tool build by [Xceptance](https://www.xceptance.com) using Java as the scripting language and the JVM as the execution environment. XLT is open source software under Apache License 2.0.

Here are some documentation links:

* [XLT Release Notes](https://lab.xceptance.de/releases/xlt/latest/release-notes/index.html)
* [XLT User Manual](https://lab.xceptance.de/releases/xlt/latest/user-manual/index.html)
* [XLT Introduction](https://training.xceptance.com/xlt/05-what-is-xlt.html#/)
* [Further XLT Training](https://training.xceptance.com/xlt/#/)
* [User Forum](https://ask.xceptance.de/)
* [GitHub Repository](https://github.com/Xceptance/XLT)

## Test Suite Structure

The following sections describes the organisation of the test suite. Major code interfaces are mentioned as a starting point to dive into the source code of the available test scenarios.

### General

Main parts of the test suite are the test scenarios source code, test configuration and test data as well as (automatically generated) test results.

General organization of the test suite's source code is described in the following section. The test suite comes as a maven project. Use your preferred IDE to import the project and run the test suite without further modification.

Test case and suite configuration is available in directory `/config`. Likewise test data and further configuration is available in directory `/config/data`. Configuration and data of the test suite adheres to some concepts described in later sections of this document.

Test results in form of XLTs result browser output - which is generated with each single mode test execution - is located in directory `/results`. You can find a short blog article on [how to use the result browser](https://blog.xceptance.com/2018/02/22/how-to-use-the-xlt-result-browser/) on our website.

### Source Code

Everything source code related can be found under `/src/main/java`. The main package of the test suite is `com.xceptance.loadtest`. The test suite is separated in a API and a site specific layer. The API layer provides the fundamental building blocks employed in modelling the site and creating the test scenarios.

### API

The test suite contains an API layer offering building blocks in form of various interfaces and base classes as well as a number of utilities and data containers, which can and should be employed when creating the project specific test scenarios. The API layer is not site specific and could be shared among different projects. The package containing the API layer can be found at `com.xceptance.loadtest.api`.

Please refer to later sections of this document for brief discussions of API layer specific components, like the `Context`, test data and general data objects, Site specific loading, and Page Lookup.

### Project Source Code

The project source code of the test suite can be found at `com.xceptance.loadtest.posters`. In general, this package contains a representation of the website under test and all its interactions, required to implement the individual test scenarios. The package further divides into the following, briefly reviewed sub-packages.

`models` - Contains all website specific structures. Separated into `models.pages` and `models.components`. The pages package contains all page objects which represent a single page or page impression, like the cart page. Each page object will typically utilise a number of reusable page components, like the mini cart. Components and page objects are **stateless** by design.

`actions` - An action will contain the interaction with a page, or more specifically the components of a page object. For example adding a product to the cart will be contained in an action called `AddToCart`, which in turn interacts with a product detail page of the Posters demo shop and it's components like the add to cart button.

`flows` - A flow describes a number of interactions (actions) with the website. A chain of actions represented as a flow provides the advantage of reuse in different test scenarios, e.g. a user login flow.

`tests` - The actual test cases (test scenarios) modelling the complete test that is being executed against the website. Each test scenario is represented by a distinct test case.

### API Layer Interfaces and Base Classes

Page objects and components employ the `PageInterface`, abstract `Page` class and `Component` interface. Actions will be typically based on `PageAction` or `AjaxAction` or a derivative. Tests will extend class `LoadTestCase` which in turn utilises `AbstractTestCase`, which is provided by XLT. All of these interfaces and base classes can be found in the sub-packages of `com.xceptance.loadtest.api`.

## Configuration and Test Data

Project configuration, project data and general load test execution configuration is described and implemented via Java and YAML properties. All configuration and data files are located in directory `/config` and subsequently `/config/data`. XLT requires this structure to allow correct upload of test data to the executing agents of the test when distrbuting the test across several machines. 

The following files are available with their general intent and contents briefly described:

`default.properties` - General XLT and suite configuration, like proxy settings, XLT timeout settings, http filter, Javascript settings, test data management settings, result and reporting settings. This file specifies the general set of properties and other property files will typically override these settings in a more specific way.

`dev-log4j.properties` - Logging properties for development mode.

`log4j.properties` - Logging properties employed during load test execution.

`project.properties` - Project specific properties referencing further configuration files, specifying settings related to test cases or simply overriding existing properties with site specific details.

`dev.properties` - Properties overriding other properties (typically default.properties or project.properties) during development and single user mode execution of a test case.

`sites.yaml` - Site specific configuration like languages/locale, resource paths or access information and test data.

`private-data.yaml` - Information on how to access the site under test, i.e. host details and credentials. Isolated in this file to be possibly excluded from version control, where required.

`test.properties` - Test run specific settings, e.g. executed test cases, test run configuration like number of users or test duration.

Further details about properties required during test case execution, especially in load test mode, are described in the [XLT documentation](https://lab.xceptance.de/releases/xlt/latest/user-manual/05-framework-config.html).

### Sites, Regions and Locale

Nowadays, a lot of sites come in several languages and often target different regions or markets. Data will be different for some sites (e.g. addresses) and others might share certain information (e.g. search phrases).
The test suite is multi-site by design even so it is only working on one demo shop. Data and configuration can be site, language or region specific and the test scripts will have a built-in way to determine which site they are working on, thereby automatically retrieving correct set of data. If you want to access information during runtime based on the current site and its properties, you can access the Context and ask:

```java
Context.get().data.site.id
Context.get().data.site.region
Context.get().data.site.locale
Context.get().data.site.language()
```

In case this is too ambiguous, you can easily extend the `Site` class to directly answer your questions, such as `isAPAC()` for instance. 

The language is automatically derived from the locale. For instance `en_CA` is `en` aka English and `fr_CA` is `fr` aka French both for Canada.

The idea is to allow reuse based on region and locale/language, while you separate everything else that is site specific.

#### File System

You will find a directory hierarchy under `/config/data` where the test suite is looking up information. During the data and configuration look up a fallback scheme is applied (discussed later on). This can look something like the following hierarchy:

```
posters/config/data/
├── languages
│   ├── default
│   │   ├── firstnames.txt
│   │   ├── lastnames.txt
│   │   └── words.txt
│   ├── en
│   │   ├── localization.yaml
│   │   └── searchterms.txt
│   ├── en_UK
│   │   ├── firstnames.txt
│   │   └── lastnames.txt
│   ├── en_US
│   │   └── stateCodesUS.txt
│   ├── fr_FR
│   │   ├── localization.yaml
│   │   └── searchterms.txt
│   └── zh_CN
│       ├── firstnames.txt
│       ├── lastnames.txt
│       ├── localization.yaml
│       └── searchterms.txt
├── regions
│   ├── APAC
│   ├── default
│   │   ├── flash.yaml
│   │   └── site.yaml
│   ├── Europe
│   └── NorthAmerica
└── sites
    ├── sites.yaml
    ├── China
    │   └── accounts.yaml
    ├── default
    │   ├── creditcards.yaml
    │   └── filters.yaml
    ├── France
    │   └── accounts.yaml
    ├── Italy
    │   └── accounts.yaml
    ├── Japan
    │   └── accounts.yaml
    ├── UK
    │   └── accounts.yaml
    └── US
        ├── accounts.yaml
        └── flash.yaml
```

There are regions, locales and languages as well as sites. Everything except default is free to be defined as needed. This hierarchy comes from information set in `/config/data/sites/sites.yaml`. Consider it the start point of the site definitions. This file is referenced in the `project.properties` via property `general.properties.yaml.global.files` and basically gets the YAML loading process started.

You can specify more files there which are loaded as initial defaults before the fallback based handling starts.

But it is important that one file of this contains the site list to use (see next example), otherwise the site concept will be broken.

```yaml
# Make all sites known upfront       
sites:
    -   id: US
        active: true  # In case you want to exclude it from a run 
        locale: en_US  # This is the language to be used
        region: NorthAmerica  # This is something like country or market
        marketshare: 10
    -   id: UK
        active: false
        locale: en_UK
        region: Europe
        marketshare: 10
```

### Properties

The test suite deals with different configuration settings (properties) and test data. The suite commonly uses a set of Java properties for configuration purposes but can replace most of them by also offering a flexible, easy to read and configure YAML based property extension. This concept was introduced to avoid boiler plate property configuration and support UTF-8. Furthermore it permits the means to simply overwrite by site, market or region.

#### YAML Properties

The YAML property files are an extension of the existing XLT property collection. The YAML properties extension is not delivered by XLT but the test suite.

*Important:* YAML does not like `TAB` at all. Make sure you modify your editor settings to correctly turn a TAB into spaces. YAML requires indentions to structure the document.

More about YAML and some easy rules can be found in the [Wikipedia](https://en.wikipedia.org/wiki/YAML).

The test suite transforms the YAML properties into regular properties which XLT and the suite can interpret. That means only the definition is in YAML but the runtime system will work on actual Java properties.

**Plain:**
```yaml
general:
    host: host.com
    baseUrl: ${host}/my-site
    credentials: # Comment here
        username: storefront # Comment there
        password: foobar
```

**Which translates into:**
```
general.host = host.com
general.host.baseUrl = host.com/my-site
general.credentials.username = storefront
general.credentials.password = foobar
```

#### Private Site Configuration

There is a `sites/private-data.yaml` which defines access and credentials for the site under test. If required, this file can be excluded from your repository (via .gitignore).
The Posters test suite contains a readily setup `private-data.yaml` file with access details for Xceptance's demo web shop [Posters](https://35.184.136.113:8443/posters/).

### Test Suite Data

Test data has been setup in a similar way as the previously discussed configuration. This permits easy reuse and targeted setup per region, site, or locale/language. File system and site considerations discussed above also apply to the test data. Furthermore, the suite tries to employ a strict concept of data separation and data maintenance.

#### Fallback on Data and Property Lookup

Data and property look up use the same fallback logic. The look up follows below steps until the property is found. If not the test suite will complain with an error.

1. Look for item in site first (e.g. US).
2. Look in default site.
3. Look in region.
4. Look in default region.
5. Look in locale (e.g. en_US, as specified for the site).
6. Look in language (e.g. en taken from en_US).
7. Look in default language.
8. If not found, the look up fails.

Empty files are permitted in case you specified something that does not have data for that specific case.

#### Site Specific Data Loading

The class `DataFileProvider.java` has the responsibility to load files following above described fallback pattern and the site context.

```java
public static Optional<File> dataFileBySite(final Site site, final String fileName)
```

#### Context and Test Data

The test suite contains the `Context`, a singleton, which holds (among others) test suite and scenario specific configuration settings and data, as well as runtime data of a test scenario. You can access the `Context` via `Context.get()`.
`Context.data` is an instance of class `TestData`. This instance is exclusive per running scenario (running user) and typically used to store all scenario and user specific data. Most data is made public for easier access and less boilerplate. If you need logic to hand out data, you have to add a methods for fetching the data. One important object contained in the `TestData` (`Context.data`) is the previously discussed `Site`.

## Test Execution and Test Results

The following section provides a brief overview on test suite execution and the resulting artefacts. For a detailed discussion of these topics please refer to the official [XLT documentation](https://lab.xceptance.de/releases/xlt/latest/getting-started/02-performance-testing.html#toc-import-test-suite-into-eclipse).

### Test Execution (Single User Mode)

All test cases contained in the suite are JUnit 4 tests. You can simply execute a test case from within your preferred IDE by using the built-in JUnit runner. Running a test case in this way will execute in single user mode and is typically employed during test case development or smoke testing a number of test scenarios. Executing the test case package will execute the whole suite in a sequential and again single user mode fashion.

### Test Output

Console output will inform about the executed test steps (actions), requests being fired and the test results (e.g. failed validation). At the end of a test case execution, the console output will provide a link to an XLT generated result browser. A result browser contains information collected during the execution of a test case. Test case, flow and action data as well as individual request and response data is contained. A result browser is a very useful tool in writing and modifying test cases. All result browsers created can be found in directory `/results` of the test suite. (The last generated result browser will be linked but result browser of previous scenario executions are also available in this directory. Clear the directory if you test suite takes up too much space.)

### Random Initial Value

Load test cases typically contain a good amount of randomness in their flows and actions. To be able to replay a test case exactly as executed previously, XLT test cases have the possibility to set a random seed, the so called random initial value. The initial value can be found at the end of the test case's console output or in the result browser (when clicking the test case title). Setting this initial value via property `com.xceptance.xlt.random.initValue` in one of the property files (typically `/config/dev.properties`) will initialise XLTs random generator (class `XltRandom`) in a way that the exact same sequence of random values are provided, thus making test case execution repeatable.

### Test Execution and Reporting in Load Test Mode

The XLT documentation contains detailed information on how to execute the test scenarios and test suite in an actual [load test setup](https://lab.xceptance.de/releases/xlt/latest/user-manual/08-loadtest.html) as well as [creating and interpreting XLT load test reports](https://lab.xceptance.de/releases/xlt/latest/user-manual/09-reports.html).

## Miscellaneous

In this section various concepts central to the test suite or test design with XLT in general are reviewed. Please follow the pointers to the official XLT documentation for an in depth discussion of the topic.

### Element Lookup

While interacting with the site it will be necessary to look up elements in the DOM. Typically the look up and manipulation of DOM elements is located in the components, actions and to some extent the pages. The test suite API provides a unified way to access page elements via CSS, ID or XPATH:

```java
// Resolve input element via CSS locator
LookUpResult result = Page.find().byCss("div.searchBar > input.searchField");
HtmlElement searchField = result.single();
```

In addition to the simple example above, the `LookUpResult` acts as a result set proxy, which can be used in a number of different ways. Among others the look up can be chained, validation can be executed or result sets can be manipulated before accessing the actual element(s).

```java
Page.find().byId("header")
	.byCss("ul.navigationItems > li")
	.asserted("Expected at least one navigation element")
	.filter(e -> e.getAttribute("data-name").equals("sale item"))
	.count()
```

The `find()` method of the `Page` object is just a convenience wrapper to `HPU.find().in(..page..)` for the current page. It gives access to the different look up strategies described above and provides a `LookUpResult`. (`HPU` being an abbreviation for Html Page Utils.)

The `Page` object can be found in package `com.xceptance.loadtest.api.models.pages`. The `LookUpResult` and the `HPU` utility can be found in package `com.xceptance.loadtest.api.hpu`.

Additionally, helpers to maninpulate HTML forms in a more convenient and direct way can be found in `com.xceptance.loadtest.api.util.FormUtils`.

### Actions and Timers

A manual (functional) test is typically separated into individual test steps. From the perspective of a load test case a similar concept is employed. Each interaction with a page or page impression is a single step of a test case and called an action. An action might result in a page load or not. The latter taking place when e.g. simply filling a form or interacting with the site in a way that an Ajax request is triggered. Page validations are not represented as individual actions. (Generally, validations are kept to a minimum during load testing, as the purpose is not functional testing.)

Actions are used to structure the interactions with the site. XLT uses the action concept further to set and manage timing markers (timers). An action automatically creates a new timer, which will terminate once the action is finished. The action information is maintained by XLT and will be used to structure the test outputs and timing information. The action markers and derived timings can be found in the result browser and the report. One or more requests can be associated with an action.

For a full explanation of actions and timers, please refer to the [XLT documentation](https://lab.xceptance.de/releases/xlt/latest/user-manual/04-framework.html#toc-xlt-action-api).

### Data Objects

The package `com.xceptance.loadtest.api.data` contains several helpers to represent common webshop data like `Account`, `Address`, `CreditCard` or `Email`. Some of these data objects are associated with managing or supplying objects, like the `AccountManager` or the `SearchTermSupplier`. Some of the mentioned data items are referenced in the previously discussed `TestData` (`Context.data`).
