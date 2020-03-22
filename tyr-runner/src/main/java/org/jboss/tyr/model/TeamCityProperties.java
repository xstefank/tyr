package org.jboss.tyr.model;

import io.quarkus.arc.config.ConfigProperties;

import java.util.Optional;

@ConfigProperties(prefix = "teamcity")
public class TeamCityProperties {

    private Optional<String> host;
    private Optional<Integer> port;
    private Optional<String> username;
    private Optional<String> password;
    private Optional<String> mapping;

    public String getHost() {
        return failOrGet(host);
    }

    public void setHost(Optional<String> host) {
        this.host = host;
    }

    public int getPort() {
        return failOrGet(port);
    }

    public void setPort(Optional<Integer> port) {
        this.port = port;
    }

    public String getUsername() {
        return failOrGet(username);
    }

    public void setUsername(Optional<String> username) {
        this.username = username;
    }

    public String getPassword() {
        return failOrGet(password);
    }

    public void setPassword(Optional<String> password) {
        this.password = password;
    }

    public String getMapping() {
        return failOrGet(mapping);
    }

    public void setMapping(Optional<String> mapping) {
        this.mapping = mapping;
    }

    private <T> T failOrGet(Optional<T> property) {
        if (!property.isPresent()) {
            throw new IllegalStateException("One of the Teamcity properties is not specified");
        }
        return property.get();
    }
}
