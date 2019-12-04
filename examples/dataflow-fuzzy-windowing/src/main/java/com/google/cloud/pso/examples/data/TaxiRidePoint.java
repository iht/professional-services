package com.google.cloud.pso.examples.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_TaxiRidePoint.Builder.class)
public abstract class TaxiRidePoint {
  /**
   * This is a data class for the objects that are read by the pipeline.
   *
   * <p>In this case, it represents part of a taxi ride (a "point"), that is read from a public
   * PubSub topic (<tt>projects/pubsub-public-data/topics/taxirides-realtime</tt>).
   *
   * <p>The data is encoded in JSON in PubSub, and here we include some annotations to map the JSON
   * field names to Java-style variable names.
   *
   * <p>To parse from JSON to a class, we need a Builder inner class (in addition to the value
   * class).
   */

  /** Builder pattern */
  public static Builder builder() {
    return new AutoValue_TaxiRidePoint.Builder();
  }

  /**
   * Properties
   *
   * <p>We don't annotate the value class properties, because we are not going to convert this class
   * to JSON.
   */
  public abstract Optional<String> rideId();

  public abstract Optional<String> pointIdx();

  public abstract Optional<Double> latitude();

  public abstract Optional<Double> longitude();

  public abstract Optional<String> timestamp();

  public abstract Optional<Double> meterReading();

  public abstract Optional<Double> meterIncrement();

  public abstract Optional<Integer> paxCount();

  @AutoValue.Builder
  public abstract static class Builder {
    /** Builder class */
    @JsonProperty("ride_id")
    public abstract Builder rideId(Optional<String> s);

    /**
     * Builder methods for all the properties
     *
     * <p>We need to annotate the properties if the name is different to the one in the parsed JSON.
     */
    @JsonProperty("point_idx")
    public abstract Builder pointIdx(Optional<String> s);

    public abstract Builder latitude(Optional<Double> d);

    public abstract Builder longitude(Optional<Double> d);

    public abstract Builder timestamp(Optional<String> s);

    @JsonProperty("meter_reading")
    public abstract Builder meterReading(Optional<Double> d);

    @JsonProperty("meter_increment")
    public abstract Builder meterIncrement(Optional<Double> d);

    @JsonProperty("passenger_count")
    public abstract Builder paxCount(Optional<Integer> i);

    public abstract TaxiRidePoint build();
  }
}
