# Kafka Enricher Service

This service is part of the Code Challenge presented at the Access Team at Just Eat Takeaway.com
The idea of this challenge is to enrich one CloudEvent (MigrateUser) published to a Kafka Broker,
with information available at a public API and produce it back to Kafka under a new CloudEvent (MigrateUserEnriched)

## Getting Started

To run this project locally you can run the command below:
```
make init
```
This command will create the .env file needed to run this project locally and the dependencies (kafka, zookeeper, kafka-ui)

After the service is up and running you can trigger an event doing a `POST` call to the migrate user endpoint.

```
curl -XPOST http://localhost:8080/api/v1/migrate/user/1234
```

To stop the service you can run the following command:
```
make dc-kc-stop
```

To restart the service you can run the following command:
```
make dc-kc-restart
```

To stop all services you can run:
```
make dc-down
```
## Challenge

You received together with this project a file that contains the challenge description.

## API Consumption

Enrich event with the content presented at this service https://gorest.co.in

### Data Available at this service

- TODOs by User (/public/v2/users/`{{USER_ID}}`/todos)
- Posts by User (/public/v2/users/`{{USER_ID}}`/posts)
- Comments by Post (/public/v2/posts/`{{POST_ID}}`/comments)





