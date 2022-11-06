###############
#    Init
###############
init: cp-env re-init
re-init: dc-kc-build dc-restart
cp-env:
	cp ./dev/.env.example ./dev/.env

###############
#    Docker
###############
dc-kc-start:
	cd dev && docker-compose up -d kafka-consumer-challenge && cd -
dc-kc-stop:
	cd dev && docker-compose stop kafka-consumer-challenge && docker-compose rm -f kafka-consumer-challenge && cd -
dc-kc-restart: dc-kc-stop dc-kc-build dc-kc-start

dc-up:
	cd dev && docker-compose up -d && cd -
dc-down:
	cd dev && docker-compose down && cd -
dc-restart: dc-down dc-up

dc-kc-build:
	cd dev && docker-compose build kafka-consumer-challenge && cd -
	
###############
#     App
###############
recompile:
	cd dev && \
	docker-compose exec kafka-consumer-challenge mvn clean package -DskipTests && \
	docker-compose exec kafka-consumer-challenge cp ./target/kafka-consumer-challenge.jar kafka-consumer-challenge.jar && \
	docker-compose restart kafka-consumer-challenge && \
	cd ../

recompile-locally:
	mvn clean package -DskipTests
	cp ./target/kafka-consumer-challenge.jar kafka-consumer-challenge.jar
	make dc-kc-restart

###############
#    Tests
###############
dc-test:
	 cd dev && docker-compose exec kafka-consumer-challenge bash -c "cd /development && mvn test" && cd -

test:
	mvn clean verify

unit-test:
	mvn clean verify -DskipITs=true

integration-test:
	mvn clean verify -DskipUTs=true
