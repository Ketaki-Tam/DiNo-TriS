# DiNo-TriS
A Distributed Multi-Framework NoSQL Triple Store designed employing state-based objects

## Services Provided

### Query
This will retrieve all triples associated with a given subject.

### Update
This will update the “object” value based on provided “subject and predicate” combination. If the subject-predicate combination does not exist, it will create a new entry.

### Merge
This will enable users to decide when a particular server synchronizes with another server.

## Configuration

The following changes need to be made in the `application.properties` corresponding to each framework:

- Change the `uri` to your own database URI.
- Change the database name to the database you'll be using.
- Set the server port according to your choice.

## Instructions to Run

1. Start servers for all the frameworks.
2. The user interface we use is an application called Postman. GET and PUT requests can be made using this interface to query, update, and merge.
