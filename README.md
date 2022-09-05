# crawler

This is a Java library for crawling static web sites.

## Installation

In order to be able to build this library, you need to have few dependencies installed on your machine:

- Java 8
- Maven

Install the dependencies and clone the project locally.

When the cloning process is complete, run the following commands to install the artifact in your local Maven repository:

```
cd crawler
mvn clean install
```

To run the example:

```
mvn exec:java
```

After you have the artifact installed in your local Maven repository, you can use the library in your Java project.

## Usage

First you need to add a dependency in your `pom.xml` file:
```
<dependencies>
    ...
    <dependency>
        <groupId>dev.filiptanu.crawler</groupId>
        <artifactId>crawler</artifactId>
        <version>1.0.0</version>
    </dependency>
    ...
</dependencies>
```

With this, the classes and interfaces defined in the crawler library can be used in your project's codebase.

### Example of crawling a page

For this example, we will use the site `http://www.anhoch.com`, which is a site for selling computer hardware.
We are interested in gathering all the products from the website.
For each product, we are interested in its title, price, category, description and image.

#### Define a source

The first thing you need to do is define a source to crawl.
You can do that by using the `Source.Builder` class.

```
Source.Builder sourceBuilder = new Source.Builder("anhoch", "http://www.anhoch.com");
```

You can define few things in the builder such as:

- which urls on the site should the crawler follow:

```
.toFollowUrlCssQueries(
    Arrays.asList(
        "#cat-sidemenu > li > ul > li > a",
        "#cat-sidemenu > li > a",
        ".pagination > ul > li > a"
    )
)
```

- which urls on the site should the crawler consider result pages:

```
.resultPageCssQueries(
    Arrays.asList(
        ".product-name > a"
    )
)
```

- on each result page, which sections of the page should be gathered:

```
.resultValueBuilderEntities(
    Arrays.asList(
        new Source.Builder.ResultValueBuilderEntity(
            "title",
            new ResultValueEntity(
                "#product > .box-stripes > .box-heading > h3",
                ResultType.TEXT
            )
        ),
        new Source.Builder.ResultValueBuilderEntity(
            "price",
            new ResultValueEntity(
                ".price > .nm",
                ResultType.TEXT
            )
        ),
        new Source.Builder.ResultValueBuilderEntity(
            "externalCategory",
            new ResultValueEntity(
                "#breadcrumbs li:nth-last-child(2)",
                ResultType.TEXT
            )
        ),
        new Source.Builder.ResultValueBuilderEntity(
            "description",
            new ResultValueEntity(
                ".tab-content > div > div > pre",
                ResultType.TEXT
            )
        ),
        new Source.Builder.ResultValueBuilderEntity(
            "imageUrl",
            new ResultValueEntity(
                "#product_gallery > img",
                ResultType.SRC
            )
        )
    )
)
```

- clean up some of the gathered sections:

```
.resultValueCleanupStrategyBuilderEntries(
    Arrays.asList(
        new Source.Builder.ResultValueCleanupStrategyBuilderEntry(
            "price",
            resultValue -> resultValue.replaceAll("[^0-9]", "")
        )
    )
)
```

- should the urls gathered from the site be modifed in some way (for example if the site uses relative urls, the base url needs to be added):

``` 
.urlCleanupStrategy(String::trim)
```

When you define all the things you need, you build the source.

```
Source source = sourceBuilder.build();
```

#### Define a crawler

After you have defined the source, you need to define a crawler.
You can do that by using the `Crawler.Builder` class.

```
Crawler.Builder crawlerBuilder = new Crawler.Builder(source, results -> logger.info("Saving results: " + results));
```

You can define additional things when building the crawler, such as the crawling strategy, or result processing strategy.
If they are not defined in the builder, default implementations will be created by the builder.
For simplicity, we will leave them out in this example.
If you are interested in the implementation of those interfaces, checko out the `StandardCrawlingStrategy` and `StandardResultProcessingStrategy` classes.

When you define all the things you need, you build the crawler.

```
Crawler crawler = crawlerBuilder.build();
```

#### Run the crawler and see the actual results in the terminal

To run the crawler, just call the `start()` method on it.

```
crawler.start()
```
