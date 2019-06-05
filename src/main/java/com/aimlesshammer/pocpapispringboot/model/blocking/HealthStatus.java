
package com.aimlesshammer.pocpapispringboot.model.blocking;

import java.util.Objects;

public class HealthStatus {

    private String status;

    public HealthStatus() {
    }

    public HealthStatus(String status) {
        setStatus(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthStatus that = (HealthStatus) o;
        return Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        String jsonString = "%s";
        return String.format(jsonString, getStatus());
    }

}
