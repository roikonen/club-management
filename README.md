# Club Management App

>  Simple club management app with two functions: add and list clubs. Created for trying out Play + React and for playing around with [Akka Reactive Streams](https://doc.akka.io/docs/akka/2.5.23/stream/index.html).
 

Read more about the forked base project @ http://bit.ly/2A1AzEq ->
[Scala Play React Seed](http://bit.ly/2A1AzEq)

## Version Summary

* [Play Framework: 2.7.2](https://www.playframework.com/documentation/2.7.x/Home)
* [React: 16.8.6](https://reactjs.org/)
* [Create React App: 2.1.8](https://github.com/facebookincubator/create-react-app)

## How to use it?

### Prerequisites

* [Node.js](https://nodejs.org/)
* [scala](https://www.scala-lang.org/download/)

### Let's get started,

* Fork or clone this repository.

* Used any of the following [SBT](http://www.scala-sbt.org/) commands which will intern trigger frontend associated npm scripts.

```
    sbt clean           # Clean existing build artifacts

    sbt stage           # Build your application from your project’s source directory

    sbt run             # Run both backend and frontend builds in watch mode

    sbt dist            # Build both backend and frontend sources into a single distribution artifact

    sbt test            # Run both backend and frontend unit tests
```

* This seed is not using [scala play views](https://www.playframework.com/documentation/2.6.x/ScalaTemplates). All the views and frontend associated routes are served via [React](https://reactjs.org/) code base under `ui` directory.

## Database Schema

```
CREATE TABLE CLUB_MEMBERS (
  CLUB VARCHAR(255) NOT NULL,
  MEMBER VARCHAR(255) NOT NULL
);
```

## HTTP Endpoints

* HTTP GET /api/clubs
  * Get all clubs, returns an array of clubs
* HTTP POST /api/clubs
  * Post a new club
  
### JSON Schema For HTTP Payloads

#### Schema
```
{
  "$schema": "http://json-schema.org/draft-07/schema",
  "title": "Club schema for HTTP payloads",
  "type": "object",
  "required": [ "name", "members" ],
  "properties": {
    "name": {
      "description": "Name of the club",
      "type": "string",
      "minLength": 1
    },
    "members": {
      "description": "List of members in the club",
      "type": "array",
      "minItems": 1,
      "items":  {
        "type": "object",
        "required": [ "name" ]
      }
    }
  }
}
```

#### Example
```
{
  "name": "Club Name",
  "members": [
    { "name": "Club Member1" },
    { "name": "Club Member2" }
  ]
}
```

## Akka Reactive Streams

Data processing between application and database is enabled by creating a data management protocol by using stacked custom [BidiFlows](https://doc.akka.io/api/akka/2.5.23/akka/stream/scaladsl/BidiFlow$.html): codec and grouping.

```
                       +-------------------------------------------+
                       | stack                                     |
                       |                                           |
 +-------------+       |  +-------+                  +----------+  |            +----------+
 |             |  ~>   O~~o       |       ~>         |          o~~O    ~>      |          |
 | Application | Club  |  | codec | List[ClubMember] | grouping |  | ClubMember | Database |
 |             |  <~   O~~o       |       <~         |          o~~O    <~      |          |
 +-------------+       |  +-------+                  +----------+  |            +----------+
                       +-------------------------------------------+
```