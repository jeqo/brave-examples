.PHONY: all
all:

.PHONY: up
up:
	docker-compose up -d
.PHONY: up-kafka
up-kafka:
	docker-compose -f docker-compose-kafka.yml up -d
.PHONY: ps
ps:
	docker-compose ps