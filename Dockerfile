FROM java:8
RUN apt-get update
RUN apt-get install -y maven
RUN mkdir /target
VOLUME /target
COPY . /opt/amidst
WORKDIR /opt/amidst
RUN ["mvn", "clean"]
RUN ["mvn", "package"]
CMD ["cp", "target/amidst-v4-0.jar", "/target"]
