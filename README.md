# Content Servish + RxJava FanOut test

This is a mock implementation of a Content Service, and a RxJava client that lists content, branches out to each content's history — and eventually fetches all versions.

N.b. Content list, content history and content versions are mutable – and might(!) change from request to request.

## Start the Content Servish

N.b. The Content Servish have a simulated latency of 500ms per request.

```bash
$ node server.js
```

## List all content

```bash
$ curl --silent http://localhost:1234/ | jq
[
  "0.ll1v72zloqs",
  "0.jibblp50gm",
  "0.m679sc5rh8h",
  "0.zec5whgf20p",
  ...
  "0.97r6rm9ly1n"
]
```

## List content history

```bash
$ curl --silent http://localhost:1234/history/0.66sd1gss4w9 | jq
[
  "0.4xqsdk6flqe",
  "0.62e4jm9yzdi",
  "0.6yppqcssddi",
  "0.fat4nnasjss",
  "0.09c6pkt3apk8",
  ...
  "0.c7umjcczr3"
]
```

## Get a content

```bash
$ curl --silent http://localhost:1234/content/0.66sd1gss4w9 | jq
{
  "aspects": {
    "0.pxadsx3gl1": {
      "0.gm3o8cnra5h": "0.ac4kmolqou",
      "0.laqe43ggyg": "0.dsiclo29ob8",
      "0.dovtslir4pk": "0.7chv9xqv97q"
    },
    "0.ddpifjwdi6m": {
      "0.n9gimtxmzop": "0.5os4wn11fz4",
      "0.aviw1yzro84": "0.cuc4h2hw0bd",
      "0.answgck5pyc": "0.95couwdel8k"
    },
    "0.36fk1gbsof3": {
      "0.nfqqyjaxw9": "0.vzue98v9fqb",
      "0.pvcxoupbnj8": "0.3w3mmcxlu9j",
      "0.tidq6dd3obe": "0.kzg2w0ii6mr"
    },
    "0.38gilcayx7o": {
      "0.8mow0x1l9c2": "0.en7uee6lxiq",
      "0.6ffzb4j5u46": "0.3yaa8gw2dw6",
      "0.waw2ms3wqx": "0.65xg9vm1z1"
    }
  }
}
```

## Run FanOut client

Either build and run from the command line, or run the main method from IntelliJ.

```bash
$ mvn package
$ java -Xmx1G -jar target/rx-java-fanout-1.0-SNAPSHOT-jar-with-dependencies.jar
```
